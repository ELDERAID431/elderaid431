package com.example.elderaid.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewHelpRequestScreen(
    onSubmitSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(null) }
    var startTime by remember { mutableStateOf<Date?>(null) }
    var endTime by remember { mutableStateOf<Date?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormatter = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Function to fetch current location
    fun fetchCurrentLocation(context: Context, onLocationFetched: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onLocationFetched("Permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                if (!addresses.isNullOrEmpty()) {
                    val fullAddress = addresses[0].getAddressLine(0)
                    onLocationFetched(fullAddress)
                } else {
                    onLocationFetched("Location not found")
                }
            } else {
                onLocationFetched("Unable to fetch location")
            }
        }.addOnFailureListener {
            onLocationFetched("Location fetch failed: ${it.localizedMessage}")
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fetchCurrentLocation(context) { fetchedLocation ->
                    location = fetchedLocation
                }
            } else {
                location = "Permission not granted"
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Help Request", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Title input
        Text("Title", fontSize = 16.sp)
        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Enter title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date and Location
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Date", fontSize = 16.sp)
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            date = calendar.time
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(date?.let { dateFormatter.format(it) } ?: "Select Date")
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("Location", fontSize = 16.sp)
                Button(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fetchCurrentLocation(context) { fetchedLocation ->
                            location = fetchedLocation
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) {
                    Text(if (location.isBlank()) "Fetch Location" else location)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start and End Time
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Start Time", fontSize = 16.sp)
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            startTime = calendar.time
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    ).show()
                }) {
                    Text(startTime?.let { timeFormatter.format(it) } ?: "Select Start Time")
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("End Time", fontSize = 16.sp)
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            endTime = calendar.time
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    ).show()
                }) {
                    Text(endTime?.let { timeFormatter.format(it) } ?: "Select End Time")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text("Description", fontSize = 16.sp)
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Enter description") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category
        Text("Category", fontSize = 16.sp)
        val categories = listOf(
            "Home shopping", "Coffee and tea time", "Pharmacy", "House cleaning",
            "Brain exercises", "Chatting", "Walks", "Food provision"
        )
        categories.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { categoryName ->
                    OutlinedButton(
                        onClick = { category = categoryName },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (category == categoryName) Color.Gray else Color.White
                        ),
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Text(categoryName, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                val userId = auth.currentUser?.uid
                if (userId != null && title.isNotBlank() && date != null && startTime != null && endTime != null &&
                    location.isNotBlank() && description.isNotBlank() && category.isNotBlank()
                ) {
                    val helpRequest = hashMapOf(
                        "title" to title,
                        "date" to date!!.time,
                        "startTime" to startTime!!.time,
                        "endTime" to endTime!!.time,
                        "location" to location,
                        "description" to description,
                        "category" to category,
                        "creatorId" to userId,
                        "creatorRole" to "elder",
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )
                    isLoading = true
                    firestore.collection("help_requests")
                        .add(helpRequest)
                        .addOnSuccessListener {
                            isLoading = false
                            onSubmitSuccess()
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            errorMessage = "Failed to save request: ${exception.localizedMessage}"
                        }
                } else {
                    errorMessage = "All fields are required."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        Button(
            onClick = { onCancel() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Back to Main Screen")
        }
    }
}
