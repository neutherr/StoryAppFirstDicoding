package com.dicoding.picodiploma.nganugramstoryapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.nganugramstoryapp.data.response.LoginResponse
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
import com.dicoding.picodiploma.nganugramstoryapp.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.nganugramstoryapp.BuildConfig

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (BuildConfig.DEBUG) {
                    // Logging hanya muncul di mode debug
                    logDebug("Step 1: Starting login process for email: $email")
                }

                repository.login(email, password).fold(
                    onSuccess = { loginResponse ->
                        if (BuildConfig.DEBUG) {
                            logDebug("Step 2: Login successful")
                        }
                        _loginResult.value = Result.Success(loginResponse)
                    },
                    onFailure = { exception ->
                        if (BuildConfig.DEBUG) {
                            logError("Step 2: Login failed", exception)
                        }
                        _loginResult.value = Result.Error(exception.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    logError("Login Error", e)
                }
                _loginResult.value = Result.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(TAG, message)
        }
    }

    private fun logError(message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, message, throwable)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
