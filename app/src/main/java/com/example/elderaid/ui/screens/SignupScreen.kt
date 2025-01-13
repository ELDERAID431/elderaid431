package com.example.elderaid.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.elderaid.R
import com.example.elderaid.ui.viewmodel.SignupViewModel
import com.example.elderaid.ui.viewmodel.User
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = viewModel(),
    onSignupSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Volunteer") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                val newPhotoUrl = uploadImageToCloudinary(it, context)
                if (newPhotoUrl != null) {
                    photoUrl = newPhotoUrl
                    successMessage = "Profile picture uploaded successfully"
                } else {
                    errorMessage = "Failed to upload image"
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE0E0E0))
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Picture
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(80.dp)) {
                    Image(
                        painter = if (photoUrl != null) {
                            rememberAsyncImagePainter(photoUrl)
                        } else {
                            painterResource(id = R.drawable.profile)
                        },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Fields
                SignupTextFieldWithLine("Full Name", fullName) { fullName = it }
                SignupTextFieldWithLine("Email Address", email) { email = it }
                SignupTextFieldWithLine("Phone Number", phoneNumber) { phoneNumber = it }
                SignupTextFieldWithLine("Age", age, KeyboardType.Number) { age = it }
                SignupTextFieldWithLine("Location", location) { location = it }
                SignupTextFieldWithLine("Password", password, KeyboardType.Password, PasswordVisualTransformation()) {
                    password = it
                }

                Spacer(modifier = Modifier.weight(1f))

                // Role Selection
                Text("Log in as ?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoleSelectionButton("Volunteer", selectedRole == "Volunteer") { selectedRole = "Volunteer" }
                    RoleSelectionButton("Elder", selectedRole == "Elder") { selectedRole = "Elder" }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Button (geri.png)
                    Image(
                        painter = painterResource(id = R.drawable.geri),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onBack() }
                    )
                    // Submit Button (ileri.png)
                    Image(
                        painter = painterResource(id = R.drawable.ileri),
                        contentDescription = "Submit",
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                if (photoUrl.isNullOrEmpty()) {
                                    errorMessage = "Please upload a profile picture"
                                    return@clickable
                                }
                                val user = User(
                                    fullName = fullName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    age = age,
                                    location = location,
                                    role = selectedRole,
                                    photoUrl = photoUrl ?: ""
                                )
                                viewModel.signup(user, password,
                                    onSuccess = { onSignupSuccess() },
                                    onFailure = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                successMessage?.let {
                    Text(it, color = Color.Green)
                }

                errorMessage?.let {
                    Text(it, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SignupTextFieldWithLine(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

@Composable
fun RoleSelectionButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
        )
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
