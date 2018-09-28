package com.wazing.gank.bean

import android.arch.lifecycle.LiveData
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BaseResult<T>(
        val error: Boolean,
        val results: List<T>
)

data class History(val desc: String, val date: String)

@Parcelize
data class Gank(
        @SerializedName("_id") val id: String?,
        val desc: String,
        val url: String,
        val type: String,
        val source: String?,
        val createdAt: String?,
        val publishedAt: String,
        val who: String?
) : Parcelable

data class Listing<T>(
        val list: LiveData<List<T>>,
        val refreshState: LiveData<NetworkState>,
        val networkState: LiveData<NetworkState>
)

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    END
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String? = "message is empty") {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
        val END = NetworkState(Status.END)
    }
}