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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.elderaid.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
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
        horizontalAlignment = Alignment.Start
    ) {
        Text("New Task", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        // Title input
        CustomTextFieldWithLine(label = "Title", value = title) { title = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Date and Location Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Date", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Image(
                    painter = painterResource(id = R.drawable.line_3),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Start).fillMaxWidth(0.8f)
                )
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = date?.let { dateFormatter.format(it) } ?: "Select Date", color = Color.Black)
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("Location", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Image(
                    painter = painterResource(id = R.drawable.line_3),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Start).fillMaxWidth(0.8f)
                )
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (location.isBlank()) "Fetch Location" else location, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start and End Time Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Start Time", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Image(
                    painter = painterResource(id = R.drawable.line_3),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Start).fillMaxWidth(0.8f)
                )
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = startTime?.let { timeFormatter.format(it) } ?: "Select Start Time", color = Color.Black)
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("End Time", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Image(
                    painter = painterResource(id = R.drawable.line_3),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Start).fillMaxWidth(0.8f)
                )
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = endTime?.let { timeFormatter.format(it) } ?: "Select End Time", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        CustomTextFieldWithLine(label = "Description", value = description) { description = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Section
        Text("Category", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Image(
                painter = painterResource(id = R.drawable.homeshopping),
                contentDescription = "Home Shopping",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "homeshopping" }
            )
            Image(
                painter = painterResource(id = R.drawable.coffeandteatime),
                contentDescription = "Coffee and Tea Time",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "coffeandteatime" }
            )
            Image(
                painter = painterResource(id = R.drawable.pharmacy),
                contentDescription = "Pharmacy",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "pharmacy" }
            )
            Image(
                painter = painterResource(id = R.drawable.housecleaning),
                contentDescription = "House Cleaning",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "housecleaning" }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Image(
                painter = painterResource(id = R.drawable.brainexercises),
                contentDescription = "Brain Exercises",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "braineexercises" }
            )
            Image(
                painter = painterResource(id = R.drawable.chatting),
                contentDescription = "Chatting",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "chatting" }
            )
            Image(
                painter = painterResource(id = R.drawable.walks),
                contentDescription = "Walks",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "walks" }
            )
            Image(
                painter = painterResource(id = R.drawable.foodprovision),
                contentDescription = "Food Provision",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { category = "foodprovision" }
            )
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
                        "acceptedVolunteers" to emptyList<String>(),
                        "timestamp" to Timestamp.now()
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit", color = Color.White)
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Main Screen", color = Color.White)
        }
    }
}

@Composable
fun CustomTextFieldWithLine(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(0.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.align(Alignment.BottomStart))
        }
    }
}
