package com.anastasiu.staytfhome.data.forms

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
import java.util.*

class UserForm : BaseObservable() {
    var id: Int? = null

    var userId: UUID? = null

    var name: String? = null
        @Bindable get
        set(destination) {
            field = destination
            notifyPropertyChanged(BR.name)
        }

    var nameError = ObservableField<String>()

    var email: String? = null
        @Bindable get
        set(email) {
            field = email
            notifyPropertyChanged(BR.email)
        }

    var emailError = ObservableField<String>()

    var avatarURL: String? = null

    var activated: Boolean? = null
        @Bindable get
        set(activated) {
            field = activated
            notifyPropertyChanged(BR.activated)
        }

    var failedLoginAttempts: Int? = null

    var blocked: Boolean? = null
        @Bindable get
        set(blocked) {
            field = blocked
            notifyPropertyChanged(BR.blocked)
        }

    var invitedBy: Int? = null

    var grantSimpleUser: Boolean? = null
        @Bindable get
        set(grantSimpleUser) {
            field = grantSimpleUser
            notifyPropertyChanged(BR.grantSimpleUser)
        }

    var grantUserManager: Boolean? = null
        @Bindable get
        set(grantUserManager) {
            field = grantUserManager
            notifyPropertyChanged(BR.grantUserManager)
        }

    var grantAdmin: Boolean? = null
        @Bindable get
        set(grantAdmin) {
            field = grantAdmin
            notifyPropertyChanged(BR.grantAdmin)
        }

    fun validateInput(): Boolean {
        var isValid = true
        if (name.isNullOrEmpty()) {
            nameError.set("Please enter the name!")
            isValid = false
        } else {
            nameError.set(null)
        }
        if (id != null) {
            if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.set("Please enter the email!")
                isValid = false
            } else {
                emailError.set(null)
            }
        }
        return isValid
    }

    fun clearInput() {
        id = null
        userId = null
        name = null
        email = null
        avatarURL = null
        activated = null
        failedLoginAttempts = null
        blocked = null
        invitedBy = null
        grantSimpleUser = null
        grantUserManager = null
        grantAdmin = null

        nameError.set(null)
        emailError.set(null)
    }
}