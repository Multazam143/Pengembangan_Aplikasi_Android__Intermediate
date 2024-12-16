package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.request.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _session = MutableLiveData<UserModel>()

    fun login(loginRequest: LoginRequest, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                val response = repository.login(loginRequest)

                if (response.loginResult?.token.isNullOrEmpty()){
                    onError("Password Anda Salah")

                }
                val user = UserModel(
                    email = loginRequest.email,
                    token = response.loginResult?.token ?: "",
                    isLogin = true
                )
                repository.saveSession(user)
                _session.value = user
                onSuccess()
            }catch (e: Exception) {
                onError(e.message ?: "Login failed") }
        }

    }

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
    }
