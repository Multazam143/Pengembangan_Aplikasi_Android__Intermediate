package com.dicoding.picodiploma.loginwithanimation.addstory

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel (private val repository: UserRepository) : ViewModel() {
    fun addStory(file : MultipartBody.Part,decription:RequestBody) = repository.addStory(file, decription)
}