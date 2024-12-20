package com.dicoding.picodiploma.nganugramstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.nganugramstoryapp.data.pref.UserModel
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.nganugramstoryapp.repository.StoryRepository
import com.dicoding.picodiploma.nganugramstoryapp.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _resultMaps = MutableLiveData<List<ListStoryItem>>()
    val resultMaps: LiveData<List<ListStoryItem>> = _resultMaps

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val stories = storyRepository.getStoriesWithLocation()
                _resultMaps.postValue(stories)
            } catch (e: Exception) {
                setError("Gagal memuat data lokasi: ${e.message}")
                _resultMaps.postValue(emptyList())
            } finally {
                setLoading(false)
            }
        }
    }

    val pagingStories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getListStoriesPaging().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    private fun setError(message: String?) {
        _errorMessage.value = message
    }
}