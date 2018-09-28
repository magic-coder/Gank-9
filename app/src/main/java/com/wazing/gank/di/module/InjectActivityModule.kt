package com.wazing.gank.di.module

import com.wazing.gank.di.ActivityScope
import com.wazing.gank.di.module.activity.MainActivityModule
import com.wazing.gank.di.module.activity.SearchActivityModule
import com.wazing.gank.di.module.provider.MainActivityProvider
import com.wazing.gank.ui.activity.MainActivity
import com.wazing.gank.ui.activity.SearchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectActivityModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityProvider::class])
    abstract fun bindMainActivitytInjector(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SearchActivityModule::class])
    abstract fun bindSearchActivitytInjector(): SearchActivity

}