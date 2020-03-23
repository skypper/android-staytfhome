package com.anastasiu.staytfhome.data.service

import com.anastasiu.staytfhome.data.model.Group
import com.anastasiu.staytfhome.data.model.User
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MainApiService {
    /**
     * User API
     */
    @POST("/api/user/signUp")
    fun signUp(@Body signUpRequest: UserSignUpRequest): Single<Response<Unit>>

    @POST("/api/user/signIn")
    fun signIn(@Body signInRequest: UserSignInRequest): Single<Response<Unit>>

    @GET("/api/user/signOut")
    fun signOut(): Single<Response<Unit>>

    @POST("/api/user/authenticateToken/facebook")
    fun facebookAuthenticateToken(@Body socialAuthRequest: UserSocialAuthRequest): Single<Response<Unit>>

    @POST("/api/user/authenticateToken/google")
    fun googleAuthenticateToken(@Body socialAuthRequest: UserSocialAuthRequest): Single<Response<Unit>>

    @GET("/api/user/current")
    fun currentUser(): Single<Response<User>>

    @PUT("/api/user/updateCurrent")
    fun updateCurrentUser(@Body updateRequest: UserUpdateRequest): Single<Response<Unit>>

    @POST("/api/user/inviteEmail")
    fun inviteEmail(@Body inviteRequest: UserInviteRequest): Single<Response<Unit>>

    @Multipart
    @POST("/api/user/uploadAvatar")
    fun uploadAvatar(@Part avatar: MultipartBody.Part): Single<Response<Unit>>

    /**
     * Group API
     */
    @GET("/api/group/list")
    fun getGroups(): Single<Response<List<Group>>>

    @POST("/api/group/create")
    fun createGroup(@Body createRequest: GroupCreateRequest): Single<Response<Unit>>

    @PUT("/api/group/update")
    fun updateGroup(@Body updateRequest: GroupUpdateRequest): Single<Response<Unit>>

    @DELETE("/api/group/delete/{id}")
    fun deleteGroup(@Path("id") id: Int): Single<Response<Unit>>
}
