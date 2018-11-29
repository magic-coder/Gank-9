package com.wazing.gank.di.component

import com.wazing.gank.App
import com.wazing.gank.di.module.AppModule
import com.wazing.gank.di.module.InjectActivityModule
import com.wazing.gank.di.module.NetModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(modules = [
    AppModule::class,
    NetModule::class,
    InjectActivityModule::class,
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class
])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}