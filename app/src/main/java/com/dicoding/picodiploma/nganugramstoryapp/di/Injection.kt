package com.dicoding.picodiploma.nganugramstoryapp.di

import com.dicoding.picodiploma.nganugramstoryapp.repository.StoryRepository
import android.content.Context
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserPreference
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.dataStore
import com.dicoding.picodiploma.nganugramstoryapp.data.retrofit.ApiConfig
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, pref)
    }
}