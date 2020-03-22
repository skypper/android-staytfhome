package com.anastasiu.staytfhome.data.service

data class UserSignInRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean
)
