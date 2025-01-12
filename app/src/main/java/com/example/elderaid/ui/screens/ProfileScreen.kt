package com.example.elderaid.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.elderaid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.InputStream

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var photoUrl by remember { mutableStateOf<String?>(null) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Fetch user information
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        photoUrl = document.getString("photoUrl")
                        fullName = document.getString("fullName") ?: ""
                        email = document.getString("email") ?: ""
                        phoneNumber = document.getString("phoneNumber") ?: ""
                        isLoading = false
                    } else {
                        errorMessage = "User data not found"
                        isLoading = false
                    }
                }
                .addOnFailureListener { exception ->
                    errorMessage = exception.localizedMessage ?: "Error fetching data"
                    isLoading = false
                }
        } else {
            errorMessage = "User not logged in"
            isLoading = false
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                val newPhotoUrl = uploadImageToCloudinary(it, context)
                if (newPhotoUrl != null) {
                    photoUrl = newPhotoUrl
                    updatePhotoUrlInFirestore(newPhotoUrl, auth, firestore)
                    successMessage = "Profile picture updated successfully"
                } else {
                    errorMessage = "Failed to upload image"
                }
            }
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            Image(
                painter = painterResource(id = R.drawable.rectangle_2),
                contentDescription = "Background Shape",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(100.dp)) {
                Image(
                    painter = if (photoUrl != null) {
                        rememberAsyncImagePainter(photoUrl)
                    } else {
                        painterResource(id = R.drawable.profile)
                    },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Picture")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(label = "Full Name", value = fullName) { fullName = it }
            ProfileTextField(label = "Email Address", value = email) { email = it }
            ProfileTextField(label = "Phone Number", value = phoneNumber, keyboardType = KeyboardType.Phone) { phoneNumber = it }
            ProfileTextField(label = "Password", value = password, keyboardType = KeyboardType.Password) { password = it }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val updates = mapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "phoneNumber" to phoneNumber
                        )
                        firestore.collection("users").document(userId)
                            .update(updates)
                            .addOnSuccessListener {
                                successMessage = "Profile updated successfully"
                            }
                            .addOnFailureListener { exception ->
                                errorMessage = exception.localizedMessage ?: "Error updating profile"
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            successMessage?.let {
                Text(it, color = Color.Green)
            }

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        TextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

suspend fun uploadImageToCloudinary(uri: Uri, context: Context): String? {
    val cloudName = "djops0jje"
    val apiKey = "619774378823937"
    val uploadPreset = "default_preset"

    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "image.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes!!)
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body!!.string())
                jsonResponse.getString("secure_url")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun updatePhotoUrlInFirestore(
    newPhotoUrl: String,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore
) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
        firestore.collection("users").document(userId)
            .update("photoUrl", newPhotoUrl)
    }
}
