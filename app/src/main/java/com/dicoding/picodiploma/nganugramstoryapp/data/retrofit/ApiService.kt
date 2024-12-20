package com.dicoding.picodiploma.nganugramstoryapp.data.retrofit

import com.dicoding.picodiploma.nganugramstoryapp.data.response.AddStoryResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.response.LoginResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.response.RegisterResponse
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ResponseDetailStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<ListStoryResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ResponseDetailStory>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<AddStoryResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Response<ListStoryResponse>
}