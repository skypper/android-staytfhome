package com.anastasiu.staytfhome.data.repository

import com.anastasiu.staytfhome.data.model.Group
import com.anastasiu.staytfhome.data.service.GroupCreateRequest
import com.anastasiu.staytfhome.data.service.GroupUpdateRequest
import com.anastasiu.staytfhome.data.service.MainApiService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class GroupRepository constructor (private val mainApiService: MainApiService) {
    fun groups(): Single<Response<List<Group>>> = mainApiService.getGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun create(group: Group): Single<Response<Unit>> =
        mainApiService.createGroup(
            GroupCreateRequest(
                group.title,
                group.address,
                group.geolocation,
                group.comment
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun update(group: Group): Single<Response<Unit>> =
        mainApiService.updateGroup(
            GroupUpdateRequest(
                group.id!!,
                group.title,
                group.address,
                group.geolocation,
                group.comment
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun delete(group: Group): Single<Response<Unit>> =
        group.id?.let {
            mainApiService.deleteGroup(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }!!
}
