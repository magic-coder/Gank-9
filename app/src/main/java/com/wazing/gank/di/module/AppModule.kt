package com.wazing.gank.di.module

import android.content.Context
import com.wazing.gank.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: App): Context = application
}