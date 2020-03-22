package com.anastasiu.staytfhome.ui.module

import com.anastasiu.staytfhome.ui.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityBindingModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity
}
