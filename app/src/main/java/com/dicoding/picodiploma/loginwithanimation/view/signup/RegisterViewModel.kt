package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.request.RegisterRequest
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel(){

    fun register(registerRequest: RegisterRequest, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                if (registerRequest.name.isBlank() || registerRequest.email.isBlank() || registerRequest.password.isBlank()) {
                    onError("Please fill in all fields")
                    return@launch
                }
                val response = userRepository.register(
                    registerRequest.name,
                    registerRequest.email,
                    registerRequest.password
                )
                if (response.error == false) {
                    onSuccess()
                } else {
                    onError(response.message ?: "Registration failed")
                }
            } catch (e: HttpException) {
                onError("Network error: ${e.localizedMessage}")
            } catch (e: IOException) {
                onError("Connection error: ${e.localizedMessage}")
            } catch (e: Exception) {
                onError("An unexpected error occurred: ${e.localizedMessage}")
                Log.e("RegisterViewModel", "Registration error", e)
            }
        }
    }
}