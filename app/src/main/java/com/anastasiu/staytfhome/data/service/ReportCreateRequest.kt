package com.anastasiu.staytfhome.data.service

import java.time.ZonedDateTime

data class ReportCreateRequest(
    val symptoms: List<String>,
    val latitude: Double,
    val longitude: Double,
    val comment: String,
    val testStatus: String,
    val dateTime: ZonedDateTime
)
