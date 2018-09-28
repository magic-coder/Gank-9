package com.wazing.gank.respository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.History
import com.wazing.gank.bean.Listing
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.respository.net.ApiCodeException
import com.wazing.gank.respository.net.ApiService
import com.wazing.gank.respository.net.RxHelper
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    private val refreshStateLiveData by lazy { MutableLiveData<NetworkState>() }

    private val historyListLiveData by lazy { MutableLiveData<List<History>>() }
    private val networkStateLiveData by lazy { MutableLiveData<NetworkState>() }

    val historyLiveData by lazy { MutableLiveData<History>() }

    private val gankListLiveData = MutableLiveData<List<Gank>>()

    fun getHistory(): LiveData<Listing<History>> {
        val listingLiveData = MutableLiveData<Listing<History>>()
        listingLiveData.value = Listing(
                list = historyListLiveData,
                refreshState = refreshStateLiveData,
                networkState = networkStateLiveData)
        getHistoryList()
        return listingLiveData
    }

    private fun getHistoryList() {
        doAsync {
            refreshStateLiveData.postValue(NetworkState.LOADING)
            val list = ArrayList<History>()
            val doc: Document = try {
                Jsoup.connect("https://gank.io/history")
                        .timeout(10 * 1000)
                        .get()
            } catch (e: Exception) {
                val errorMsg = ApiCodeException.checkException(e)
                refreshStateLiveData.postValue(NetworkState.error(errorMsg))
                networkStateLiveData.postValue(NetworkState.error(errorMsg))
                throw IllegalArgumentException("error")
            }
            val elements = doc.body().select("div.typo").select("div.content ul li")
            for (element in elements) {
                val desc = element.select("a").text()
                val date = element.select("span").text().replace("-", "/")
                list.add(History(desc, date))
            }
            uiThread {
                if (list.isNotEmpty()) {
                    historyLiveData.postValue(list[0])
                    historyListLiveData.postValue(list)
                } else {
                    networkStateLiveData.postValue(NetworkState.error("history is empty."))
                }
                refreshStateLiveData.postValue(NetworkState.LOADED)
            }
        }
    }

    fun getGankList(history: History): LiveData<List<Gank>> {
        refreshStateLiveData.postValue(NetworkState.LOADING)
        val disposable = apiService.getTodayList(history.date)
                .map {
                    val result = it.string()
                    val jsonObject = JSONObject(result)
                    val error: Boolean = jsonObject.optBoolean("error", true)
                    if (error) {
                        throw ApiCodeException("服务器返回异常")
                    } else {
                        val categoryArrays: JSONArray = jsonObject.optJSONArray("category")
                        val resultJsonObject = jsonObject.optJSONObject("results")
                        val list = ArrayList<Gank>()
                        for (i in 0 until categoryArrays.length()) {
                            val category = categoryArrays[i].toString()
                            val resultJsonArray: JSONArray = resultJsonObject.optJSONArray(category)
                            for (j in 0 until resultJsonArray.length()) {
                                val data = Gson().fromJson<Gank>(resultJsonArray[j].toString(),
                                        Gank::class.java)
                                list.add(data)
                            }
                        }
                        return@map list
                    }
                }
                .compose(RxHelper.ioMain())
                .subscribe({
                    refreshStateLiveData.postValue(NetworkState.LOADED)
                    gankListLiveData.postValue(it)
                }, {
                    refreshStateLiveData.postValue(NetworkState.LOADED)
                    val msg = ApiCodeException.checkException(it)
                    networkStateLiveData.postValue(NetworkState.error(msg))
                })
        disposables.add(disposable)
        return gankListLiveData
    }

    fun refresh() {
        val history: History? = historyLiveData.value
        if (history == null) {
            getHistoryList()
        } else {
            getGankList(history)
        }
    }

    fun clear() = disposables.clear()

}