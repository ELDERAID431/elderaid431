package com.example.elderaid.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.elderaid.R
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import coil.compose.rememberAsyncImagePainter


@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var photoUrl by remember { mutableStateOf<String?>(null) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

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
                        age = document.getString("age") ?: ""
                        location = document.getString("location") ?: ""
                        role = document.getString("role") ?: ""
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
            uploadImageToFirebase(storage, auth, it) { newUrl ->
                photoUrl = newUrl
                successMessage = "Profile picture updated successfully"
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
            Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Picture
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(100.dp)) {
                Image(
                    painter = if (photoUrl != null) {
                        rememberAsyncImagePainter(photoUrl) // Use Coil or Glide
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
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Picture")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            ProfileTextField(label = "Full Name", value = fullName) { fullName = it }
            ProfileTextField(label = "Email", value = email) { email = it }
            ProfileTextField(label = "Phone Number", value = phoneNumber) { phoneNumber = it }
            ProfileTextField(label = "Age", value = age, keyboardType = KeyboardType.Number) { age = it }
            ProfileTextField(label = "Location", value = location) { location = it }
            ProfileTextField(label = "Role", value = role) { role = it }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userUpdates = mapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "phoneNumber" to phoneNumber,
                            "age" to age,
                            "location" to location,
                            "role" to role
                        )
                        firestore.collection("users").document(userId)
                            .update(userUpdates)
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = {
                    auth.signOut()
                    onLogout()
                }) {
                    Text("Logout")
                }
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
        // Label Text
        Text(label, style = MaterialTheme.typography.bodySmall)

        // TextField Component
        TextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



fun uploadImageToFirebase(
    storage: FirebaseStorage,
    auth: FirebaseAuth,
    uri: Uri,
    onSuccess: (String) -> Unit
) {
    val userId = auth.currentUser?.uid ?: return
    val storageRef = storage.reference.child("users/$userId/profile.jpg")

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(userId)
                    .update("photoUrl", downloadUri.toString())
                    .addOnSuccessListener {
                        onSuccess(downloadUri.toString())
                    }
            }
        }
        .addOnFailureListener {
            // Handle failure
        }
}
