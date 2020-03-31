package com.anastasiu.staytfhome.data.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.anastasiu.staytfhome.R

class ReportForm : BaseObservable() {
    var id: Int? = null

    var hasCough: Boolean = false
        @Bindable get
        set(hasCough) {
            field = hasCough
            notifyPropertyChanged(BR.hasCough)
        }

    var hasFever: Boolean = false
        @Bindable get
        set(hasFever) {
            field = hasFever
            notifyPropertyChanged(BR.hasFever)
        }

    var hasTiredness: Boolean = false
        @Bindable get
        set(hasTiredness) {
            field = hasTiredness
            notifyPropertyChanged(BR.hasTiredness)
        }

    var hasDifficultyBreathing: Boolean = false
        @Bindable get
        set(hasDifficultyBreathing) {
            field = hasDifficultyBreathing
            notifyPropertyChanged(BR.hasDifficultyBreathing)
        }

    var latitude: Double? = null
        @Bindable get
        set(latitude) {
            field = latitude
            notifyPropertyChanged(BR.latitude)
        }

    var longitude: Double? = null
        @Bindable get
        set(longitude) {
            field = longitude
            notifyPropertyChanged(BR.longitude)
        }

    var comment: String = ""
        @Bindable get
        set(comment) {
            field = comment
            notifyPropertyChanged(BR.comment)
        }

    var testStatus: Int = R.id.rbTestStatusNotTested
        @Bindable get
        set(testStatus) {
            field = testStatus
            notifyPropertyChanged(BR.testStatus)
        }

    var userId: Int? = null

    fun validateInput(): Boolean {
        if (!hasCough && !hasFever && !hasTiredness && !hasDifficultyBreathing) {
            return false
        }
        return true
    }

    fun clearInput() {
        id = null
        hasCough = false
        hasFever = false
        hasTiredness = false
        hasDifficultyBreathing = false
        comment = ""
        testStatus = R.id.rbTestStatusNotTested
        userId = null
    }
}