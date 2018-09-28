package com.wazing.gank.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.wazing.gank.R
import com.wazing.gank.base.BaseLazyFragment
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.bean.Status
import com.wazing.gank.listener.OnItemClickListener
import com.wazing.gank.listener.OnLoadMoreListener
import com.wazing.gank.respository.CategoryRepository
import com.wazing.gank.ui.activity.GankDetailsActivity
import com.wazing.gank.ui.activity.ImagePageActivity
import com.wazing.gank.ui.adapter.GankAdapter
import com.wazing.gank.utils.SpaceItemDecoration
import com.wazing.gank.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.layout_refresh_recycler.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.sp
import javax.inject.Inject

class CategoryFragment : BaseLazyFragment() {

    @Inject
    lateinit var repository: CategoryRepository

    private val viewModel: CategoryViewModel by lazy {
        val category = arguments?.getString(KEY_CATEGORY)
        return@lazy ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CategoryViewModel(category!!, repository) as T
            }
        })[CategoryViewModel::class.java]
    }

    private val adapter by lazy { GankAdapter() }

    override fun getLayoutId(): Int = R.layout.layout_refresh_recycler

    override fun onFirstUserVisible(isVisibleToUser: Boolean) {
        if (!isVisibleToUser) return

        viewModel.refreshState.observe(this, Observer {
            swipe_refresh_layout.isRefreshing = it == NetworkState.LOADING
            if (it?.status == Status.SUCCESS) {
                adapter.cleanItem()
            }
        })
        viewModel.networkState.observe(this, Observer { it ->
            when (it?.status) {
                Status.FAILED -> {
                    if (adapter.getAllItem().isEmpty()) {
                        Snackbar.make(view!!, it.msg!!,
                                Snackbar.LENGTH_INDEFINITE).setAction("重试") { viewModel.refresh() }
                                .show()
                    } else {
                        adapter.loadMoreFail()
                    }
                }
                else -> {

                }
            }
        })
        viewModel.gankList.observe(this, Observer { list ->
            list?.let { adapter.setItem(it) }
        })

        swipe_refresh_layout.onRefresh { viewModel.refresh() }
        recycler_view.addItemDecoration(SpaceItemDecoration(spacing = sp(5), includeEdge = true))
        recycler_view.adapter = adapter
        adapter.setOnLoadMoreListener(object : OnLoadMoreListener.SimpleOnLoadMoreListener() {
            override fun onRetry() {
                viewModel.retry()
            }

            override fun onLoadMore() {
                viewModel.loadMore()
            }
        })

        adapter.setOnItemClickListener(object : OnItemClickListener.SimpleOnItemClickListener<Gank>() {
            override fun onItemClick(view: View, item: Gank, position: Int) {
                when (item.type) {
                    "福利" -> {
                        val list = adapter.getAllItem().filter { it.type == "福利" } as ArrayList
                        startActivity(ImagePageActivity.newInstance(activity!!, list, list.indexOf(item)))
                    }
                    else -> startActivity(GankDetailsActivity.newInstance(activity!!, item))
                }
            }
        })
    }

    override fun onUserVisible(isVisibleToUser: Boolean) {
    }

    override fun onDestroy() {
        repository.clear()
        super.onDestroy()
    }

    companion object {
        private const val KEY_CATEGORY = "category"

        fun newInstance(category: String): CategoryFragment {
            val bundle = Bundle()
            bundle.putString(KEY_CATEGORY, category)
            val fragment = CategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}