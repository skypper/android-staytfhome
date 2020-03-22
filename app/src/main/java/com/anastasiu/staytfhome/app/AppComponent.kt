package com.anastasiu.staytfhome.app

import android.app.Application
import com.anastasiu.staytfhome.data.component.ApiComponent
import com.anastasiu.staytfhome.ui.module.*
import com.anastasiu.staytfhome.ui.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        FragmentModule::class,
        MainActivityBindingModule::class,
        ViewModelModule::class
    ],
    dependencies = [ApiComponent::class]
)
interface AppComponent {
    fun inject(stayTFHomeApplication: StayTFHomeApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun apiComponent(apiComponent: ApiComponent): Builder

        fun build(): AppComponent
    }
}
