package com.dicoding.picodiploma.nganugramstoryapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.nganugramstoryapp.data.PagingStoriesSource
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserPreference
import com.dicoding.picodiploma.nganugramstoryapp.data.response.AddStoryResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ResponseDetailStory
import com.dicoding.picodiploma.nganugramstoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStoryDetail(id: String): Result<ResponseDetailStory> {
        return try {
            val session = userPreference.getSession().firstOrNull()
            if (session?.token.isNullOrEmpty()) {
                return Result.failure(Exception("Token not found"))
            }
            val response = apiService.getDetailStory("Bearer ${session?.token}", id)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getListStoriesPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PagingStoriesSource(storiesRepository = this)
            }
        ).liveData
    }


    suspend fun getListPaging(
        location: Int? = null,
        page: Int? = null,
        size: Int? = null
    ): List<ListStoryItem> {
        return try {
            // Ambil token dari UserPreference
            val token = userPreference.getSession().firstOrNull()?.token
                ?: throw Exception("Token not found")

            // Panggil API
            val response = apiService.getStories("Bearer $token", page, size, location)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Pastikan elemen null dihapus
                    body.listStory?.filterNotNull() ?: emptyList()
                } else {
                    throw Exception("Response body is null")
                }
            } else {
                // Tangani kesalahan respons
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                throw Exception("Stories Error with code: ${response.code()}, message: $errorMessage")
            }
        } catch (e: Exception) {
            // Lempar ulang exception agar PagingSource dapat menampilkan error
            throw Exception("Failed to fetch stories: ${e.message}", e)
        }
    }


    suspend fun addStory(file: File, description: String, token: String): Result<AddStoryResponse> {
        return try {
            val filePart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.addStory("Bearer $token", filePart, descriptionPart)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload story"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoriesWithLocation(): List<ListStoryItem> {
        val session = userPreference.getSession().firstOrNull()
        val token = session?.token
        Log.d("StoryRepository", "Token digunakan: $token") // Tambahkan log ini untuk memastikan token

        if (token.isNullOrEmpty()) {
            throw Exception("Token not found or invalid")
        }

        val response = apiService.getStoriesWithLocation("Bearer $token")
        if (response.isSuccessful) {
            val body = response.body()
            Log.d("StoryRepository", "Response body: $body")
            return body?.listStory?.filterNotNull()?.filter {
                it.lat != null && it.lon != null
            } ?: emptyList()
        } else {
            Log.e("StoryRepository", "Error response: ${response.errorBody()?.string()}")
            throw Exception("Failed to load stories with location")
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}