package com.example.elderaid.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.elderaid.R
import com.example.elderaid.ui.viewmodel.SignupViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val context = LocalContext.current

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
                // Header Images
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.Start),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.elder),
                        contentDescription = "Elder",
                        modifier = Modifier.height(30.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.aid),
                        contentDescription = "Aid",
                        modifier = Modifier.height(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Profile Picture
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(80.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    IconButton(
                        onClick = { /* Edit Action */ },
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
                            onFailure = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Sign Up", fontSize = 14.sp)
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
