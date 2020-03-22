package com.anastasiu.staytfhome.data.service

data class UserSignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val invitedBy: Int?
)
