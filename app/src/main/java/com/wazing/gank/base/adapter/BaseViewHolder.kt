package com.wazing.gank.base.adapter

import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class BaseViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    private val mViews: SparseArray<View> = SparseArray()

    companion object {
        fun get(parent: ViewGroup, @LayoutRes layoutId: Int): BaseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return get(itemView)
        }

        fun get(@NonNull view: View) = BaseViewHolder(view)
    }

    fun <T : View> getView(viewId: Int): T {
        var view = mViews.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        @Suppress("UNCHECKED_CAST")
        return view as T
    }

    fun setText(viewId: Int, text: String): BaseViewHolder {
        val tv = getView<TextView>(viewId)
        tv.text = text
        return this
    }

}