package com.anastasiu.staytfhome.data.service

import java.util.*

data class UserUpdateRequest(
    val id: Int,
    val userId: UUID,
    val name: String,
    val email: String,
    val avatarURL: String?,
    val activated: Boolean,
    val failedLoginAttempts: Int,
    val invitedBy: Int?,
    val roles: String
)
