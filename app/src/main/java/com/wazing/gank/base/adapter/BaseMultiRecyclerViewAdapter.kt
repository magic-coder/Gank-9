package com.wazing.gank.base.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.wazing.gank.R
import com.wazing.gank.listener.OnItemClickListener
import com.wazing.gank.listener.OnLoadMoreListener
import java.util.*

abstract class BaseMultiRecyclerViewAdapter<T> @JvmOverloads constructor(
        private val dataSourceList: ArrayList<T> = ArrayList()
) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        internal const val TYPE_ITEM = 10000
        private const val TYPE_LOAD_MORE = 10003
    }

    private var isOpenLoadMore = false
    private var isLoadingMore = false
    private var isLoadMoreEnd = false
    private var mLoadMoreViewHolder: BaseViewHolder? = null

    private var mOnItemClickListener: OnItemClickListener<T>? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        this.mOnItemClickListener = listener
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = listener
        this.isOpenLoadMore = true
    }

    override fun getItemCount(): Int {
        var count = 0
        if (isOpenLoadMore) {
            count++
        }
        count += dataSourceList.size
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return if (isOpenLoadMore && itemCount == position + 1) {
            TYPE_LOAD_MORE
        } else addItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            TYPE_LOAD_MORE -> {
                mLoadMoreViewHolder = BaseViewHolder.get(parent, R.layout.recycler_load_more)
                mLoadMoreViewHolder!!.itemView.setOnClickListener(View.OnClickListener {
                    if (isLoadingMore || isLoadMoreEnd) return@OnClickListener
                    retry()
                })
                return mLoadMoreViewHolder!!
            }
            else -> {
                val holder = onCreateBaseViewHolder(parent, viewType)
                holder.itemView.setOnClickListener { v ->
                    mOnItemClickListener?.let {
                        val position = holder.adapterPosition
                        it.onItemClick(v, dataSourceList[position], position)
                    }
                }
                holder.itemView.setOnLongClickListener { v ->
                    mOnItemClickListener?.let {
                        val position = holder.adapterPosition
                        it.onItemLongClick(v, dataSourceList[position], position)
                    }
                    true
                }
                return holder
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val viewType = holder.itemViewType
        when (viewType) {
            TYPE_LOAD_MORE -> startLoadMore()
            else -> convert(holder, dataSourceList[position], position)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null && layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
            layoutManager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isOpenLoadMore && itemCount == position + 1) {
                        layoutManager.spanCount
                    } else 1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) {
            val position = holder.adapterPosition
            if (isOpenLoadMore && itemCount == position + 1) {
                lp.isFullSpan = true
            }
        }
    }

    fun getItem(position: Int): T = dataSourceList[position]

    fun getAllItem(): List<T> = dataSourceList

    fun cleanItem() {
        dataSourceList.clear()
        notifyDataSetChanged()
    }

    fun setNewItem(list: Collection<T>) {
        dataSourceList.clear()
        if (list.isNotEmpty()) {
            dataSourceList.addAll(list)
        }
        notifyDataSetChanged()
        // 所有数据加载完成 = false
        isLoadMoreEnd = false
        isLoadingMore = false
    }

    fun setItem(list: Collection<T>) {
        if (list.isEmpty()) {
            loadMoreEnd()
        } else {
            dataSourceList.addAll(list)
            notifyItemRangeInserted(itemCount, list.size)
        }
        isLoadingMore = false
    }

    private fun startLoadMore() {
        if (!isOpenLoadMore || mOnLoadMoreListener == null || dataSourceList.isEmpty() || isLoadingMore
                || isLoadMoreEnd) return
        loadMoreLoading()
        mOnLoadMoreListener!!.onLoadMore()
    }

    private fun retry() {
        if (!isOpenLoadMore || mOnLoadMoreListener == null || dataSourceList.isEmpty() || isLoadingMore
                || isLoadMoreEnd) return
        loadMoreLoading()
        mOnLoadMoreListener!!.onRetry()
    }

    private fun loadMoreLoading() {
        isLoadingMore = true
        loadMoreStatus(true, "正在加载...")
    }

    @JvmOverloads
    fun loadMoreFail(message: String = "加载失败，点击重试") {
        isLoadingMore = false
        loadMoreStatus(false, message)
    }

    @JvmOverloads
    fun loadMoreEnd(message: String = "没有更多了 (=・ω・=)") {
        isLoadingMore = false
        isLoadMoreEnd = true
        loadMoreStatus(false, message)
    }

    private fun loadMoreStatus(isVisible: Boolean, message: String) {
        mLoadMoreViewHolder?.let {
            if (it.itemView is ViewGroup) {
                val viewGroup = it.itemView
                for (i in 0 until viewGroup.childCount) {
                    val childView = viewGroup.getChildAt(i)
                    if (childView is ProgressBar) {
                        childView.setVisibility(if (isVisible) View.VISIBLE else View.GONE)
                    } else if (childView is TextView) {
                        childView.text = message
                    }
                }
            }
        }
    }

    abstract fun addItemViewType(position: Int): Int

    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    abstract fun convert(holder: BaseViewHolder, item: T, position: Int)
}