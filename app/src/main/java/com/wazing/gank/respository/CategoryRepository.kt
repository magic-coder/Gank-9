package com.wazing.gank.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.Listing
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.respository.net.ApiCodeException
import com.wazing.gank.respository.net.ApiService
import com.wazing.gank.respository.net.RxHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: ApiService) {

    companion object {
        private const val COUNT: Int = 30
    }

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    val pageLiveData: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }

    private val refreshStateLiveData by lazy { MutableLiveData<NetworkState>() }
    private val networkStateLiveData by lazy { MutableLiveData<NetworkState>() }

    private val gankListLiveData = MutableLiveData<List<Gank>>()

    fun getGank(): LiveData<Listing<Gank>> {
        val listingLiveData = MutableLiveData<Listing<Gank>>()
        listingLiveData.value = Listing(
                list = gankListLiveData,
                refreshState = refreshStateLiveData,
                networkState = networkStateLiveData)
        pageLiveData.postValue(1)
        return listingLiveData
    }

    fun getGankList(category: String, page: Int): LiveData<List<Gank>> {
        if (page == 1) refreshStateLiveData.postValue(NetworkState.LOADING)
        val disposable = apiService.getGankList(category, COUNT, page)
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