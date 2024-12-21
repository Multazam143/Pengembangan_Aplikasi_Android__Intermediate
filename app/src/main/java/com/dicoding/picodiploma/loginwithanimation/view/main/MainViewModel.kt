package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _story = MutableLiveData<List<ListStoryItem>>()
    val story : LiveData<List<ListStoryItem>> = _story

    private val _isLoading = MutableLiveData<Boolean>()

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    init {
        viewModelScope.launch{
            fetchStories()
        }
    }

    suspend fun logout(){
        repository.logout()
        _isLoggedOut.postValue(true)
    }

    private fun fetchStories(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getStory()
                _story.value = response.listStory

            } catch (e: HttpException){
                Log.e("MainViewModel", "Network error: ${e.localizedMessage}")
            } catch (e: IOException) {
                Log.e("MainViewModel", "Connection error: ${e.localizedMessage}")
            } catch (e: Exception){
                Log.e("MainViewModel", "An unexpected error occurred: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }

    }
}