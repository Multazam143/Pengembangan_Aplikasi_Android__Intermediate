package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.response.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository (
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStory(): StoryResponse{
        val token = userPreference.getSession().first().token
        return apiService.getStories("Bearer $token")
    }

    suspend fun getDetailStory(id: String): DetailStoryResponse? {
        return try {
            val token = userPreference.getSession().first().token
            apiService.getDetailStory("Bearer $token", id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getStoriesWithLocation(): List<ListStoryItem>{
        val token = userPreference.getSession().first().token
        val response =   apiService.getStoriesWithLocation(location = 1, token = "Bearer $token")
        return response.listStory.filter { it.lat != null && it.lon != null }

    }

    suspend fun register(name: String, email: String, password: String) = apiService.register(name, email, password)

    suspend fun login(loginRequest: LoginRequest)= apiService.logInUser(loginRequest.email,loginRequest.password)

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, userPreference)
            }
        ).liveData
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody): LiveData<Result<AddNewStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.addStory("Bearer $token", file, description)
            emit(Result.Success(response))
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}