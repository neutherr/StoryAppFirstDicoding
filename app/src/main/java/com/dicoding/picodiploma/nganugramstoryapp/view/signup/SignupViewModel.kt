    package com.dicoding.picodiploma.nganugramstoryapp.view.signup

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.dicoding.picodiploma.nganugramstoryapp.data.response.RegisterResponse
    import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
    import com.dicoding.picodiploma.nganugramstoryapp.utils.Result
    import com.google.gson.Gson
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch
    import retrofit2.HttpException

    class SignupViewModel(private val repository: UserRepository) : ViewModel() {
        private val _registerResult = MutableStateFlow<Result<RegisterResponse>?>(null)
        val registerResult: StateFlow<Result<RegisterResponse>?> = _registerResult

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        suspend fun register(name: String, email: String, password: String) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    val response = repository.register(name, email, password)
                    if (response.isSuccessful) {
                        response.body()?.let { registerResponse ->
                            _registerResult.value = Result.Success(registerResponse)
                        } ?: run {
                            _registerResult.value = Result.Error("Response body is null")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                        _registerResult.value = Result.Error(errorResponse.message ?: "Unknown error")
                    }
                } catch (e: HttpException) {
                    _registerResult.value = Result.Error("HTTP Exception: ${e.message()}")
                } catch (e: Exception) {
                    Log.e(TAG, "Register failed: ", e)
                    _registerResult.value = Result.Error(e.message ?: "Unknown error")
                } finally {
                    _isLoading.value = false
                }
            }
        }
        fun resetRegisterResult() {
            _registerResult.value = null
        }

        companion object {
            private const val TAG = "SignupViewModel"
        }
    }
