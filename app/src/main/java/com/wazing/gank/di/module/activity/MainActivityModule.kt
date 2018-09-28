package com.wazing.gank.di.module.activity

import com.wazing.gank.ui.activity.MainActivity
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun providesName(activity: MainActivity): String = activity.javaClass.name

}