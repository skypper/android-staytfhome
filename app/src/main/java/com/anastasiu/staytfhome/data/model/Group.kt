package com.anastasiu.staytfhome.data.model

data class Group(
    val id: Int?,
    val title: String,
    val address: String,
    val geolocation: String?,
    val comment: String,
    val userId: Int?
)