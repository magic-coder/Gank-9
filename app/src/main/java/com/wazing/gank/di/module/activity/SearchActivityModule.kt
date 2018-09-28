package com.wazing.gank.di.module.activity

import com.wazing.gank.ui.activity.SearchActivity
import dagger.Module
import dagger.Provides

@Module
class SearchActivityModule {

    @Provides
    fun providesName(activity: SearchActivity): String = activity.javaClass.name

}