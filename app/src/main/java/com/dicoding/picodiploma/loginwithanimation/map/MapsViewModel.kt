package com.dicoding.picodiploma.loginwithanimation.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> get() = _storiesWithLocation


    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val stories = repository.getStoriesWithLocation()
                _storiesWithLocation.value = stories
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}