package com.anastasiu.staytfhome.data.service

data class UserSocialAuthRequest (
    val accessToken: String,
    val tokenType: String? = null,
    val expiresIn: Int? = null,
    val refreshToken: String? = null
)
