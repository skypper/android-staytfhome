package com.anastasiu.staytfhome.data.model

import java.util.*

data class User(
    val id: Int? = -1,
    val userId: UUID = UUID.randomUUID(),
    val name: String = "",
    val email: String = "",
    val avatarURL: String?,
    val activated: Boolean = false,
    val failedLoginAttempts: Int = 0,
    val invitedBy: Int?,
    val roles: String = ""
)
