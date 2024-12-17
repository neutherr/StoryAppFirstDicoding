package com.dicoding.picodiploma.nganugramstoryapp.repository

import android.util.Log
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserModel
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserPreference
import com.dicoding.picodiploma.nganugramstoryapp.data.response.LoginResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String) = apiService.register(name, email, password)

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            Log.d("UserRepository", "Step 5: Repository login started")
            val response = apiService.login(email, password)
            Log.d("UserRepository", "Step 6: API Response: $response")

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    loginResponse.loginResult?.token?.let { token ->
                        val user = UserModel(
                            email = email,
                            token = token,
                            isLogin = true
                        )
                        Log.d("UserRepository", "Step 7: Saving session: $user")
                        saveSession(user)
                    }
                    Result.success(loginResponse)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Login Error: ", e)
            Result.failure(e)
        }
    }

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
        Log.d("UserRepository", "Session berhasil disimpan: $user")
    }


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}