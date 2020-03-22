package com.anastasiu.staytfhome.data.component

import com.anastasiu.staytfhome.data.auth.CredentialsAuthenticatorProvider
import com.anastasiu.staytfhome.data.module.MainApiModule
import com.anastasiu.staytfhome.data.module.NetworkModule
import com.anastasiu.staytfhome.data.repository.UserRepository
import dagger.Component

@Component(
    modules = [
        NetworkModule::class,
        MainApiModule::class
    ]
)
interface ApiComponent {
    fun credentialsAuthenticatorProvider(): CredentialsAuthenticatorProvider
    fun userRepository(): UserRepository
}
