package com.dicoding.picodiploma.loginwithanimation.view.detailStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.Story
import kotlinx.coroutines.launch

class DetailViewModel (private val repository: UserRepository): ViewModel() {
    private val _detailStory = MutableLiveData<Story?>()
    val detailStory : LiveData<Story?> = _detailStory

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            val response = repository.getDetailStory(id)
            _detailStory.postValue(response?.story)
        }
    }
}