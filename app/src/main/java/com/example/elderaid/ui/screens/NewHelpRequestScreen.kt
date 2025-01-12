package com.example.elderaid.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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

    fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker(onTimeSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Use 12-hour format
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Task", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Date", fontSize = 16.sp)
                Button(onClick = { showDatePicker { selectedDate -> date = selectedDate } }) {
                    Text(date?.let { dateFormatter.format(it) } ?: "Select Date")
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("Location", fontSize = 16.sp)
                TextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Start Time", fontSize = 16.sp)
                Button(onClick = { showTimePicker { selectedTime -> startTime = selectedTime } }) {
                    Text(startTime?.let { timeFormatter.format(it) } ?: "Select Start Time")
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("End Time", fontSize = 16.sp)
                Button(onClick = { showTimePicker { selectedTime -> endTime = selectedTime } }) {
                    Text(endTime?.let { timeFormatter.format(it) } ?: "Select End Time")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description", fontSize = 16.sp)
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Category", fontSize = 16.sp)
        val categories = listOf(
            "Home shopping", "Coffee and tea time", "Pharmacy", "House cleaning",
            "Brain exercises", "Chatting", "Walks", "Food provision"
        )
        categories.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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

        Button(
            onClick = {
                val userId = auth.currentUser?.uid
                if (userId != null && date != null && startTime != null && endTime != null &&
                    location.isNotBlank() && description.isNotBlank() && category.isNotBlank()
                ) {
                    val helpRequest = hashMapOf(
                        "date" to date!!.time,
                        "startTime" to startTime!!.time,
                        "endTime" to endTime!!.time,
                        "location" to location,
                        "description" to description,
                        "category" to category,
                        "creatorId" to userId
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
    }
}
