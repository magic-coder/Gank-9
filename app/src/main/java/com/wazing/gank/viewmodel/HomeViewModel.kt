package com.wazing.gank.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.wazing.gank.bean.History
import com.wazing.gank.bean.Listing
import com.wazing.gank.respository.HomeRepository

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val historyListing: LiveData<Listing<History>> = repository.getHistory()

    val refreshState = Transformations.switchMap(historyListing) {
        it.refreshState
    }!!

    val historyLiveData = repository.historyLiveData

    val historyList: LiveData<List<History>> = Transformations.switchMap(historyListing) {
        return@switchMap it.list
    }

    val historyNetworkState = Transformations.switchMap(historyListing) {
        it.networkState
    }!!

    val gankList = Transformations.switchMap(repository.historyLiveData) {
        repository.getGankList(it)
    }!!

    fun refresh() = repository.refresh()

    fun getHistoryGankList(history: History) {
        repository.historyLiveData.value = history
    }

    fun clearDisposables() = repository.clear()

}