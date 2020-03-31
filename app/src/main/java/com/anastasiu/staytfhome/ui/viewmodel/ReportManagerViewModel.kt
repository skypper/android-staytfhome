package com.anastasiu.staytfhome.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.forms.ReportForm
import com.anastasiu.staytfhome.data.model.Report
import com.anastasiu.staytfhome.data.repository.ReportRepository
import com.anastasiu.staytfhome.ui.event.Event
import io.reactivex.disposables.CompositeDisposable
import java.time.ZonedDateTime
import javax.inject.Inject

class ReportManagerViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val loginViewModel: LoginViewModel
) : ViewModel() {
    companion object {
        private val TAG: String? = "ReportManagerViewModel"
    }
    val reports = MutableLiveData<List<Report>>()
    val myReport = Transformations.map(reports) { reports ->
        reports.find { report -> report.userId == loginViewModel.user.value!!.id  }
    }
    var reportForm = ReportForm()

    val reportUpdateNavigateEvent = MutableLiveData<Event<Int>>()
    val reportCreateSubmitFormEvent = MutableLiveData<Event<ReportForm>>()
    val reportUpdateSubmitFormEvent = MutableLiveData<Event<ReportForm>>()

    private val compositeDisposable = CompositeDisposable()

    init {
        fetchReports()
    }

    fun fetchReports() {
        compositeDisposable.add(reportRepository.reports().subscribe({ response ->
            if (response.isSuccessful) {
                reports.postValue(response.body())
            }
        }, { e ->
            Log.e(TAG, e.toString())
        }))
    }

    fun deleteReport(report: Report) {
        compositeDisposable.add(reportRepository.delete(report).subscribe({ response ->
            if (response.isSuccessful) {
                val newReports = reports.value!!.toMutableList() - report
                reports.postValue(newReports)
            }
        }, { e ->
            Log.e(TAG, e.toString())
        }))
    }

    fun clearReport() {
        reports.postValue(emptyList())
    }

    fun submitReportForm() {
        Log.e(TAG, "reportForm ${reportForm.comment} ${reportForm.hasCough} ${reportForm.testStatus}")
        if (reportForm.validateInput()) {
            val symptoms = mutableListOf<String>()
            if (reportForm.hasCough) {
                symptoms.add("cough")
            }
            if (reportForm.hasFever) {
                symptoms.add("fever")
            }
            if (reportForm.hasTiredness) {
                symptoms.add("tiredness")
            }
            if (reportForm.hasDifficultyBreathing) {
                symptoms.add("difficulty_breathing")
            }

            val testStatus = when (reportForm.testStatus) {
                R.id.rbTestStatusNegative -> "0"
                R.id.rbTestStatusPositive -> "1"
                else -> "-1"
            }
            val report = Report(
                reportForm.id,
                symptoms,
                reportForm.latitude!!,
                reportForm.longitude!!,
                reportForm.comment,
                testStatus,
                ZonedDateTime.now()
            )

            if (reportForm.id == null) {
                compositeDisposable.add(reportRepository.create(report)
                    .subscribe({ response ->
                        if (response.isSuccessful) {
                            fetchReports()
                            reportCreateSubmitFormEvent.postValue(Event(reportForm))
                        }
                    }, { e ->
                        Log.e(TAG, e.toString())
                    })
                )
            } else {
                compositeDisposable.add(reportRepository.update(report)
                    .subscribe({ response ->
                        if (response.isSuccessful) {
                            fetchReports()
                            reportUpdateSubmitFormEvent.postValue(Event(reportForm))
                        }
                    }, { e ->
                        Log.e(TAG, e.toString())
                    })
                )
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}