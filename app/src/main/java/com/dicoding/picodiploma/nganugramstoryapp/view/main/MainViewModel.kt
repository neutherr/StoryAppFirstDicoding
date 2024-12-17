package com.dicoding.picodiploma.nganugramstoryapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserModel
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.nganugramstoryapp.repository.StoryRepository
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _resultStories = MutableLiveData<List<ListStoryItem>>()
    val resultStories: LiveData<List<ListStoryItem>> = _resultStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getListStories() {
        viewModelScope.launch {
            val session = userRepository.getSession().firstOrNull()
            if (session?.isLogin == true && !session.token.isNullOrEmpty()) {
                _isLoading.value = true
                try {
                    storyRepository.getStories().fold(
                        onSuccess = { response ->
                            response.listStory?.let { stories ->
                                _resultStories.value = stories.filterNotNull()
                            }
                        },
                        onFailure = { exception ->
                            _errorMessage.value = exception.message
                        }
                    )
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                } finally {
                    _isLoading.value = false
                }
            } else {
                // Jika user belum login atau token tidak ada, kirim pesan error
                _errorMessage.value = "User belum login. Silakan login terlebih dahulu."
                Log.e("MainViewModel", "User belum login, tidak bisa mengambil data stories")
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}