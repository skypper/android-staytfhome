package com.anastasiu.staytfhome.data.module

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.anastasiu.staytfhome.data.auth.CredentialsAuthenticatorProvider
import com.anastasiu.staytfhome.data.repository.ReportRepository
import com.anastasiu.staytfhome.data.repository.UserRepository
import com.anastasiu.staytfhome.data.service.MainApiService
import com.anastasiu.staytfhome.utils.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

@Module
class MainApiModule {
    @Provides
    fun provideRestAdapter(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(LocalDate::class.java, object : TypeAdapter<LocalDate>() {
                    override fun write(out: JsonWriter?, value: LocalDate?) {
                        out?.value(value.toString())
                    }

                    override fun read(`in`: JsonReader?): LocalDate {
                        return LocalDate.parse(`in`?.nextString())
                    }
                }).registerTypeAdapter(LocalTime::class.java, object : TypeAdapter<LocalTime>() {
                    override fun write(out: JsonWriter?, value: LocalTime?) {
                        out?.value(value.toString())
                    }

                    override fun read(`in`: JsonReader?): LocalTime {
                        return LocalTime.parse(`in`?.nextString())
                    }
                }).registerTypeAdapter(ZonedDateTime::class.java, object : TypeAdapter<ZonedDateTime>() {
                    override fun write(out: JsonWriter?, value: ZonedDateTime?) {
                        out?.value(value.toString())
                    }

                    override fun read(`in`: JsonReader?): ZonedDateTime {
                        return ZonedDateTime.parse(`in`?.nextString())
                    }
                }).create()))
        .build()

    @Provides
    fun provideMainApiService(restAdapter: Retrofit): MainApiService =
        restAdapter.create(MainApiService::class.java)

    @Provides
    fun provideCredentialsAuthenticatorProvider(mainApiService: MainApiService) =
        CredentialsAuthenticatorProvider(mainApiService)

    @Provides
    fun provideUserRepository(mainApiService: MainApiService) = UserRepository(mainApiService)

    @Provides
    fun provideReportRepository(mainApiService: MainApiService) = ReportRepository(mainApiService)
}
