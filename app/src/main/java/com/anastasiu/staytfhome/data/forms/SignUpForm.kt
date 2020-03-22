package com.anastasiu.staytfhome.data.forms

data class SignUpForm(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val password: String?,
    val invitedBy: Int?
)
