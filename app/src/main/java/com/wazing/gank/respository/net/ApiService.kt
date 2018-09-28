package com.wazing.gank.respository.net

import com.wazing.gank.bean.BaseResult
import com.wazing.gank.bean.Gank
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/day/history")
    fun getHistoryList(): Observable<BaseResult<String>>

    @GET("api/day/{datetime}")
    fun getTodayList(@Path("datetime") datetime: String): Observable<ResponseBody>

    @GET("api/data/{category}/{count}/{page}")
    fun getGankList(@Path("category") category: String,
                    @Path("count") count: Int,
                    @Path("page") page: Int): Observable<BaseResult<Gank>>

    @GET("api/search/query/{queryText}/category/{category}/count/{count}/page/{page}")
    fun searchGankList(@Path("category") category: String,
                       @Path("queryText") queryText: String,
                       @Path("count") count: Int,
                       @Path("page") page: Int): Observable<BaseResult<Gank>>

}