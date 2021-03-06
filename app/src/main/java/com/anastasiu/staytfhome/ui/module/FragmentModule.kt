package com.anastasiu.staytfhome.ui.module

import com.anastasiu.staytfhome.ui.fragment.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    abstract fun mainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun signinFragment(): SigninFragment

    @ContributesAndroidInjector
    abstract fun signupFragment(): SignupFragment

    @ContributesAndroidInjector(modules = [ReportManagerFragmentModule::class])
    abstract fun reportManagerFragment(): ReportManagerFragment

    @ContributesAndroidInjector
    abstract fun reportCreateUpdateFragment(): ReportCreateUpdateFragment

    @ContributesAndroidInjector
    abstract fun profileManagerFragment(): ProfileManagerFragment
}