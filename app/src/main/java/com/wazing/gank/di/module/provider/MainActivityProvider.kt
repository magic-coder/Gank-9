package com.wazing.gank.di.module.provider

import com.wazing.gank.di.FragmentScope
import com.wazing.gank.di.module.CategoryFragmentModule
import com.wazing.gank.di.module.HomeFragmentModule
import com.wazing.gank.di.module.TabLayoutFragmentModule
import com.wazing.gank.ui.fragment.CategoryFragment
import com.wazing.gank.ui.fragment.HomeFragment
import com.wazing.gank.ui.fragment.TabLayoutFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityProvider {

    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun bindHomeFragmentInjector(): HomeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TabLayoutFragmentModule::class])
    abstract fun bindTabLayoutFragmentInjector(): TabLayoutFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CategoryFragmentModule::class])
    abstract fun bindCategoryFragmentInjector(): CategoryFragment

}