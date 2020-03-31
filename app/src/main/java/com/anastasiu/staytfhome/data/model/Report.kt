package com.anastasiu.staytfhome.data.model

import java.time.ZonedDateTime

data class Report(
    val id: Int?,
    val symptoms: List<String>,
    val latitude: Double,
    val longitude: Double,
    val comment: String,
    val testStatus: String,
    val dateTime: ZonedDateTime,
    val userId: Int? = -1
)