package com.dicoding.picodiploma.nganugramstoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nganugramstoryapp.di.Injection
import com.dicoding.picodiploma.nganugramstoryapp.repository.StoryRepository
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
import com.dicoding.picodiploma.nganugramstoryapp.view.add.AddStoryViewModel
import com.dicoding.picodiploma.nganugramstoryapp.view.detail.DetailViewModel
import com.dicoding.picodiploma.nganugramstoryapp.view.login.LoginViewModel
import com.dicoding.picodiploma.nganugramstoryapp.view.main.MainViewModel
import com.dicoding.picodiploma.nganugramstoryapp.view.signup.SignupViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository, userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideStoryRepository(context)
                )
            }.also { INSTANCE = it }
        }
    }
}
