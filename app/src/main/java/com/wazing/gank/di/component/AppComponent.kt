package com.wazing.gank.di.component

import com.wazing.gank.AppApplication
import com.wazing.gank.di.module.InjectActivityModule
import com.wazing.gank.di.module.NetModule
import com.wazing.gank.respository.net.ApiService
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import retrofit2.Retrofit

@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    InjectActivityModule::class,
    NetModule::class
])
interface AppComponent {

    fun inject(application: AppApplication)

    fun getRetrofit(): Retrofit

    fun getApiService(): ApiService
}