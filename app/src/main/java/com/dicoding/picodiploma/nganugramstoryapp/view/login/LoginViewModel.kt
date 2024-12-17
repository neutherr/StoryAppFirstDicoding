package com.dicoding.picodiploma.nganugramstoryapp.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.nganugramstoryapp.data.response.LoginResponse
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
import com.dicoding.picodiploma.nganugramstoryapp.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "Step 1: Starting login process for email: $email")

                repository.login(email, password).fold(
                    onSuccess = { loginResponse ->
                        Log.d(TAG, "Step 2: Login successful")
                        _loginResult.value = Result.Success(loginResponse)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Step 2: Login failed", exception)
                        _loginResult.value = Result.Error(exception.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Login Error", e)
                _loginResult.value = Result.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}