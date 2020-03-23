package com.anastasiu.staytfhome.data.service

data class GroupUpdateRequest(
    val id: Int = -1,
    val title: String,
    val address: String,
    val geolocation: String?,
    val comment: String
)
