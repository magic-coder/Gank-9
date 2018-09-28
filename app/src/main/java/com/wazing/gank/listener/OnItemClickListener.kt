package com.wazing.gank.listener

import android.view.View

interface OnItemClickListener<T> {

    fun onItemClick(view: View, item: T, position: Int)

    fun onItemLongClick(view: View, item: T, position: Int)

    open class SimpleOnItemClickListener<T> : OnItemClickListener<T> {

        override fun onItemClick(view: View, item: T, position: Int) {

        }

        override fun onItemLongClick(view: View, item: T, position: Int) {

        }
    }
}