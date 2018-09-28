package com.wazing.gank.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.Listing
import com.wazing.gank.respository.CategoryRepository

class CategoryViewModel(
        private val category: String, private val repository: CategoryRepository
) : ViewModel() {

    private val result: LiveData<Listing<Gank>> = repository.getGank()

    val refreshState = Transformations.switchMap(result) { it.refreshState }!!

    val networkState = Transformations.switchMap(result) { it.networkState }!!

    val gankList = Transformations.switchMap(repository.pageLiveData) {
        repository.getGankList(category, it)
    }!!

    fun refresh() = repository.pageLiveData.postValue(1)

    fun retry() = repository.pageLiveData.apply { postValue(value!!) }

    fun loadMore() = repository.pageLiveData.apply { postValue(value!! + 1) }

}