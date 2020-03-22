package com.anastasiu.staytfhome.data.repository

import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.data.service.MainApiService
import com.anastasiu.staytfhome.data.service.UserInviteRequest
import com.anastasiu.staytfhome.data.service.UserUpdateRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class UserRepository constructor (private val mainApiService: MainApiService) {
    fun updateCurrent(user: User): Single<Response<Unit>> =
        mainApiService.updateCurrentUser(
            UserUpdateRequest(
                user.id!!,
                user.userId,
                user.name,
                user.email,
                user.avatarURL,
                user.activated,
                user.failedLoginAttempts,
                user.invitedBy,
                user.roles)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun invite(email: String): Single<Response<Unit>> =
        mainApiService.inviteEmail(UserInviteRequest(email))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun uploadAvatar(avatarFile: File): Single<Response<Unit>> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), avatarFile)
        return mainApiService.uploadAvatar(
            MultipartBody.Part.createFormData(
                "avatar",
                avatarFile.name,
                requestFile
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
