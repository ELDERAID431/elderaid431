package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elderaid.ui.viewmodel.SignupViewModel

@Composable
fun SignupScreen(viewModel: SignupViewModel = viewModel(), onSignupSuccess: () -> Unit) {
    // State'ler
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedCity by remember { mutableStateOf("İstanbul") } // Varsayılan şehir
    var selectedDistrict by remember { mutableStateOf("") } // Varsayılan ilçe
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Şehir ve ilçe bilgileri
    val citiesWithDistricts = mapOf(
        "Adana" to listOf("Seyhan", "Çukurova", "Yüreğir", "Ceyhan"),
        "Ankara" to listOf("Çankaya", "Keçiören", "Yenimahalle", "Mamak"),
        "İstanbul" to listOf("Kadıköy", "Beşiktaş", "Üsküdar", "Bakırköy"),
        "İzmir" to listOf("Bornova", "Konak", "Karşıyaka", "Buca")
    )

    val cities = citiesWithDistricts.keys.toList()
    val districts = citiesWithDistricts[selectedCity] ?: emptyList() // Seçilen şehre göre ilçeler

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ad
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))

        // Soyad
        TextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") })
        Spacer(modifier = Modifier.height(8.dp))

        // Email
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        // Şifre
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Cinsiyet Seçimi
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Gender:")
            Spacer(modifier = Modifier.width(8.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(gender)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Male") },
                        onClick = {
                            gender = "Male"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Female") },
                        onClick = {
                            gender = "Female"
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Şehir Seçimi
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "City:")
            Spacer(modifier = Modifier.width(8.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(selectedCity)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                selectedCity = city
                                selectedDistrict = "" // İlçeyi sıfırla
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // İlçe Seçimi
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "District:")
            Spacer(modifier = Modifier.width(8.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(if (selectedDistrict.isNotBlank()) selectedDistrict else "Select District")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    districts.forEach { district ->
                        DropdownMenuItem(
                            text = { Text(district) },
                            onClick = {
                                selectedDistrict = district
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Kaydol Butonu
        Button(onClick = {
            if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || selectedDistrict.isBlank()) {
                errorMessage = "All fields are required"
                successMessage = ""
            } else {
                viewModel.signup(
                    name, surname, gender, selectedCity, selectedDistrict, email, password,
                    onSuccess = { successMessage = "Successful"; errorMessage = "" },
                    onFailure = { errorMessage = "Unsuccessful"; successMessage = "" }
                )
            }
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Başarı veya Hata Mesajı
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else if (successMessage.isNotEmpty()) {
            Text(text = successMessage, color = MaterialTheme.colorScheme.primary)
        }
    }
}
