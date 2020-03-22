package com.anastasiu.staytfhome.data.auth

import com.facebook.AccessToken
import com.anastasiu.staytfhome.data.forms.SignInForm
import com.anastasiu.staytfhome.data.forms.SignUpForm
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.data.service.UserSignInRequest
import com.anastasiu.staytfhome.data.service.UserSignUpRequest
import com.anastasiu.staytfhome.data.service.MainApiService
import com.anastasiu.staytfhome.data.service.UserSocialAuthRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class CredentialsAuthenticatorProvider constructor(private val mainApiService: MainApiService) {
    fun signUp(signUpForm: SignUpForm): Single<Response<Unit>> =
        mainApiService.signUp(UserSignUpRequest(
            signUpForm.firstName ?: "",
            signUpForm.lastName ?: "",
            signUpForm.email!!,
            signUpForm.password!!,
            signUpForm.invitedBy)
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun signIn(signInForm: SignInForm): Single<Response<Unit>> = mainApiService.signIn(
        UserSignInRequest(
            signInForm.email!!,
            signInForm.password!!,
            signInForm.rememberMe ?: false))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun signOut(): Single<Response<Unit>> = mainApiService.signOut()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun facebookAuthenticateToken(accessToken: AccessToken): Single<Response<Unit>> =
        mainApiService.facebookAuthenticateToken(UserSocialAuthRequest(accessToken.token))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun googleAuthenticateToken(accessToken: String): Single<Response<Unit>> =
        mainApiService.googleAuthenticateToken(UserSocialAuthRequest(accessToken))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun currentUser(): Single<Response<User>> = mainApiService.currentUser()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
