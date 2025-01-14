package com.example.elderaid

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

class ElderAidUnitTest {

    // Function: formatTime
    private fun formatTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    // Function: acceptTask (mocked for testing)
    private fun acceptTask(
        acceptedVolunteers: MutableList<String>,
        volunteerId: String
    ): Boolean {
        if (volunteerId in acceptedVolunteers) return false
        acceptedVolunteers.add(volunteerId)
        return true
    }

    // Function: validateEmail (example validation logic)
    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function: validatePhoneNumber (mock example for testing)
    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^\\+?[0-9]{10,13}\$"))
    }

    // Function: formatUserName
    private fun formatUserName(firstName: String?, lastName: String?): String {
        return "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
    }

    @Test
    fun testFormatTime_correctFormatting() {
        val timestamp: Long = 1672531200000 // January 1, 2023, 12:00 AM UTC
        val expected = "12:00 AM"
        val actual = formatTime(timestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun testFormatTime_differentTimestamps() {
        val timestamp: Long = 1672563600000 // January 1, 2023, 9:00 AM UTC
        val expected = "09:00 AM"
        val actual = formatTime(timestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun testAcceptTask_newVolunteer() {
        val acceptedVolunteers = mutableListOf("volunteer1", "volunteer2")
        val newVolunteerId = "volunteer3"
        val isAccepted = acceptTask(acceptedVolunteers, newVolunteerId)
        assertTrue(isAccepted)
        assertTrue(acceptedVolunteers.contains(newVolunteerId))
    }

    @Test
    fun testAcceptTask_duplicateVolunteer() {
        val acceptedVolunteers = mutableListOf("volunteer1", "volunteer2")
        val duplicateVolunteerId = "volunteer1"
        val isAccepted = acceptTask(acceptedVolunteers, duplicateVolunteerId)
        assertFalse(isAccepted)
    }

    @Test
    fun testValidatePhoneNumber_validPhone() {
        val phone = "+1234567890"
        assertTrue(validatePhoneNumber(phone))
    }

    @Test
    fun testValidatePhoneNumber_invalidPhone() {
        val phone = "12345"
        assertFalse(validatePhoneNumber(phone))
    }

    @Test
    fun testFormatUserName_bothNamesPresent() {
        val firstName = "John"
        val lastName = "Doe"
        val expected = "John Doe"
        val actual = formatUserName(firstName, lastName)
        assertEquals(expected, actual)
    }

    @Test
    fun testFormatUserName_onlyFirstName() {
        val firstName = "John"
        val lastName: String? = null
        val expected = "John"
        val actual = formatUserName(firstName, lastName)
        assertEquals(expected, actual)
    }

    @Test
    fun testFormatUserName_emptyNames() {
        val firstName: String? = null
        val lastName: String? = null
        val expected = ""
        val actual = formatUserName(firstName, lastName)
        assertEquals(expected, actual)
    }
}
