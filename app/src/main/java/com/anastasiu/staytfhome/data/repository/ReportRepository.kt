package com.anastasiu.staytfhome.data.repository

import com.anastasiu.staytfhome.data.model.Report
import com.anastasiu.staytfhome.data.service.ReportCreateRequest
import com.anastasiu.staytfhome.data.service.ReportUpdateRequest
import com.anastasiu.staytfhome.data.service.MainApiService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ReportRepository constructor (private val mainApiService: MainApiService) {
    fun reports(): Single<Response<List<Report>>> = mainApiService.getReports()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun create(report: Report): Single<Response<Unit>> =
        mainApiService.createReport(
            ReportCreateRequest(
                report.symptoms,
                report.latitude,
                report.longitude,
                report.comment,
                report.testStatus,
                report.dateTime
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun update(report: Report): Single<Response<Unit>> =
        mainApiService.updateReport(
            ReportUpdateRequest(
                report.id!!,
                report.symptoms,
                report.latitude,
                report.longitude,
                report.comment,
                report.testStatus,
                report.dateTime
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun delete(report: Report): Single<Response<Unit>> =
        report.id?.let {
            mainApiService.deleteReport(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }!!
}
