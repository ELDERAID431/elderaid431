package com.example.elderaid_431.login


import androidx.lifecycle.ViewModel
import com.example.elderaid_431.data.UserRepository


class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.loginUser(email, password, onComplete)
    }
}
