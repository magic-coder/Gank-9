package com.wazing.gank.respository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.Listing
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.respository.net.ApiCodeException
import com.wazing.gank.respository.net.ApiService
import com.wazing.gank.respository.net.RxHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchRepository @Inject constructor(private val apiService: ApiService) {

    companion object {
        private const val COUNT = 20
    }

    private val disposables by lazy { CompositeDisposable() }

    private val refreshStateLiveData by lazy { MutableLiveData<NetworkState>() }
    private val networkStateLiveData by lazy { MutableLiveData<NetworkState>() }

    val queryTextLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val pageLiveData: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }

    private val gankListLiveData = MutableLiveData<List<Gank>>()

    fun getGank(): LiveData<Listing<Gank>> {
        val listingLiveData = MutableLiveData<Listing<Gank>>()
        listingLiveData.value = Listing(
                list = gankListLiveData,
                refreshState = refreshStateLiveData,
                networkState = networkStateLiveData)
        return listingLiveData
    }

    fun getGankList(category: String = "all",
                    queryText: String? = queryTextLiveData.value,
                    page: Int): LiveData<List<Gank>> {
        queryText ?: return gankListLiveData
        if (page == 1) refreshStateLiveData.postValue(NetworkState.LOADING)
        val disposable = apiService.searchGankList(category, queryText, COUNT, page)
                .compose(RxHelper.handlerResult())
                .compose(RxHelper.ioMain())
                .subscribe({
                    if (page == 1) refreshStateLiveData.postValue(NetworkState.LOADED)
                    gankListLiveData.postValue(it)
                }, {
                    if (page == 1) refreshStateLiveData.postValue(NetworkState.LOADED)
                    val msg = ApiCodeException.checkException(it)
                    networkStateLiveData.postValue(NetworkState.error(msg))
                })
        disposables.add(disposable)
        return gankListLiveData
    }

    fun clear() = disposables.clear()

}