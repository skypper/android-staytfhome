package com.anastasiu.staytfhome.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.data.auth.CredentialsAuthenticatorProvider
import com.anastasiu.staytfhome.data.forms.UserForm
import com.anastasiu.staytfhome.data.repository.UserRepository
import com.anastasiu.staytfhome.ui.event.Event
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val credentialsAuthenticatorProvider: CredentialsAuthenticatorProvider,
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val TAG: String? = "LoginViewModel"
    }
    val user = MutableLiveData<User>()
    var userForm = UserForm()

    val userSubmitFormEvent = MutableLiveData<Event<UserForm>>()
    val signOutEvent = MutableLiveData<Event<User>>()

    private val compositeDisposable = CompositeDisposable()

    fun fetchUser() {
        compositeDisposable.add(credentialsAuthenticatorProvider.currentUser().subscribe({ response ->
            if (response.isSuccessful) {
                setUser(response.body())
            } else {
                setUser(null)
            }
        }, { e ->
            Log.e(TAG, e.toString())
            setUser(null)
        }))
    }

    fun uploadAvatar(avatarFile: File) {
        compositeDisposable.add(userRepository.uploadAvatar(avatarFile).subscribe({ response ->
            if (response.isSuccessful) {
                Log.e(TAG, "Successfully uploaded!")
            }
        }, { e ->
            Log.e(TAG, e.toString())
        }))
    }

    fun submitUserForm() {
        if (userForm.validateInput()) {
            val user = User(
                user.value!!.id,
                user.value!!.userId,
                userForm.name!!,
                user.value!!.email,
                user.value!!.avatarURL,
                user.value!!.activated,
                user.value!!.failedLoginAttempts,
                user.value!!.invitedBy,
                user.value!!.roles)
            compositeDisposable.add(userRepository.updateCurrent(user).subscribe({ response ->
                if (response.isSuccessful) {
                    userSubmitFormEvent.postValue(Event(userForm))
                }
            }, { e ->
                Log.e(TAG, e.toString())
            }))
        }
    }

    fun signOut() {
        compositeDisposable.add(credentialsAuthenticatorProvider.signOut().subscribe({ response ->
            if (response.isSuccessful) {
                signOutEvent.postValue(Event(user.value!!))
            }
        }, { e ->
            Log.e(TAG, e.toString())
        }))
    }

    private fun setUser(user_: User?) {
        user.postValue(user_)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
