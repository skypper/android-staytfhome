package com.anastasiu.staytfhome.data.service

data class GroupCreateRequest(
    val title: String,
    val address: String,
    val geolocation: String?,
    val comment: String
)
