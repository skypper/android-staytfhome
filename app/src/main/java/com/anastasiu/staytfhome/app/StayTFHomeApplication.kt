package com.anastasiu.staytfhome.app

import android.app.Activity
import android.app.Application
import com.anastasiu.staytfhome.data.component.DaggerApiComponent
import com.anastasiu.staytfhome.data.module.NetworkModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class StayTFHomeApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
            .builder()
            .application(this)
            .apiComponent(
                DaggerApiComponent
                    .builder()
                    .networkModule(NetworkModule(this))
                    .build())
            .build()
            .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }
}
