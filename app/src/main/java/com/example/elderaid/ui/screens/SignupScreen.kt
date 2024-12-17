package com.example.elderaid.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import com.example.elderaid.R
import com.example.elderaid.ui.viewmodel.SignupViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.elderaid.ui.viewmodel.User

@Composable
fun SignupScreen(viewModel: SignupViewModel = viewModel(), onSignupSuccess: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Volunteer") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Images
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp)
                    .align(Alignment.Start)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.elder),
                    contentDescription = "Elder",
                    modifier = Modifier.height(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.aid),
                    contentDescription = "Aid",
                    modifier = Modifier.height(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Profile Picture Edit
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(120.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                IconButton(
                    onClick = { /* Edit Profile Action */ },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            SignupTextFieldWithLine("Full Name", fullName) { fullName = it }
            SignupTextFieldWithLine("Email Address", email) { email = it }
            SignupTextFieldWithLine("Phone Number", phoneNumber) { phoneNumber = it }
            SignupTextFieldWithLine("Age", age, KeyboardType.Number) { age = it }
            SignupTextFieldWithLine("Location", location) { location = it }
            SignupTextFieldWithLine("Password", password, visualTransformation = PasswordVisualTransformation()) {
                password = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selection
            Text("Log in as ?", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleSelectionButton("Volunteer", selectedRole == "Volunteer") { selectedRole = "Volunteer" }
                RoleSelectionButton("Elder", selectedRole == "Elder") { selectedRole = "Elder" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
            Button(
                onClick = {
                    val user = User(
                        fullName = fullName,
                        email = email,
                        phoneNumber = phoneNumber,
                        age = age,
                        location = location,
                        role = selectedRole
                    )
                    viewModel.signup(user, password,
                        onSuccess = { onSignupSuccess() },
                        onFailure = { errorMessage = it }
                    )
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Sign Up")
            }

            // Error Message
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red)
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
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
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
            .padding(4.dp)
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Text(label, color = Color.Black, fontSize = 16.sp)
    }
}
