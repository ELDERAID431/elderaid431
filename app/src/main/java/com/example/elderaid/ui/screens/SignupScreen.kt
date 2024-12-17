package com.example.elderaid.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TextFieldDefaults


@Composable
fun SignupScreen(onSignupSuccess: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Volunteer") }

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
            // Header with Images (Elder Aid - Vertical)
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp)
                    .align(Alignment.Start), // Sol üst köşeye hizala
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.elder),
                    contentDescription = "Elder",
                    modifier = Modifier
                        .height(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp)) // Boşluk
                Image(
                    painter = painterResource(id = R.drawable.aid),
                    contentDescription = "Aid",
                    modifier = Modifier
                        .height(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Profile Picture Edit
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(120.dp)
            ) {
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

            // Input Fields with Lines
            SignupTextFieldWithLine(label = "Full Name", value = fullName, onValueChange = { fullName = it })
            SignupTextFieldWithLine(label = "Email Address", value = email, onValueChange = { email = it })
            SignupTextFieldWithLine(label = "Phone Number", value = phoneNumber, onValueChange = { phoneNumber = it })
            SignupTextFieldWithLine(label = "Age", value = age, onValueChange = { age = it }, keyboardType = KeyboardType.Number)
            SignupTextFieldWithLine(label = "Location", value = location, onValueChange = { location = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selection
            Text("Log in as ?", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleSelectionButton(label = "Volunteer", isSelected = selectedRole == "Volunteer") {
                    selectedRole = "Volunteer"
                }
                RoleSelectionButton(label = "Elder", isSelected = selectedRole == "Elder") {
                    selectedRole = "Elder"
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
            Button(
                onClick = { onSignupSuccess() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Sign Up")
            }
        }
    }
}


@Composable
fun SignupTextFieldWithLine(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Placeholder Label
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Input Field with Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Yükseklik ayarı
        ) {
            // Line Image
            Image(
                painter = painterResource(id = R.drawable.line),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp) // Daha kalın çizgi
                    .align(Alignment.BottomCenter)
            )

            // Custom Input Field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp, start = 8.dp)
            )
        }
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
