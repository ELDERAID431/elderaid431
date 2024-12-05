package com.example.elderaid_431.register

import com.example.elderaid_431.data.UserRepository


import androidx.lifecycle.ViewModel
import com.example.elderaid_431.data.User


class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(email: String, password: String, name: String, userType: Int, onComplete: (Boolean, String?) -> Unit) {
        val user = User(name, userType)
        repository.registerUser(email, password, user, onComplete)
    }
}
