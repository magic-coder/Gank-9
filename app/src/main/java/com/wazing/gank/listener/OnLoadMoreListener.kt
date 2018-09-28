package com.wazing.gank.listener

interface OnLoadMoreListener {
    fun onLoadMore()
    fun onRetry()

    open class SimpleOnLoadMoreListener : OnLoadMoreListener {
        override fun onRetry() {
        }

        override fun onLoadMore() {
        }
    }
}