package com.wazing.gank.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.wazing.gank.R
import com.wazing.gank.base.BaseInjectorActivity
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.bean.Status
import com.wazing.gank.listener.OnItemClickListener
import com.wazing.gank.listener.OnLoadMoreListener
import com.wazing.gank.respository.SearchRepository
import com.wazing.gank.ui.adapter.GankAdapter
import com.wazing.gank.utils.SpaceItemDecoration
import com.wazing.gank.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*
import org.jetbrains.anko.sp
import org.jetbrains.anko.support.v4.onRefresh
import javax.inject.Inject

class SearchActivity : BaseInjectorActivity() {

    @Inject
    lateinit var repository: SearchRepository

    private val adapter by lazy { GankAdapter() }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(repository) as T
            }
        })[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        // 设置输入框提示文字样式，设置提示文字颜色，设置内容文字颜色
        val searchAutoComplete = search_view.findViewById(R.id.search_src_text) as SearchView.SearchAutoComplete
        searchAutoComplete.setHintTextColor(ContextCompat.getColor(this, android.R.color.white))
        searchAutoComplete.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0 ?: return false
                viewModel.search(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?) = false
        })

        viewModel.refreshState.observe(this, Observer {
            swipe_refresh_layout.isRefreshing = it == NetworkState.LOADING
            if (it?.status == Status.SUCCESS) {
                adapter.setNewItem(arrayListOf())
            }
        })

        viewModel.networkState.observe(this, Observer { it ->
            when (it?.status) {
                Status.FAILED -> {
                    if (adapter.getAllItem().isEmpty()) {
                        com.google.android.material.snackbar.Snackbar.make(recycler_view, it.msg!!,
                                com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE).setAction("重试") { viewModel.refresh() }.show()
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
                    "福利" -> startActivity(ImagePageActivity.newInstance(this@SearchActivity, item))
                    else -> startActivity(GankDetailsActivity.newInstance(this@SearchActivity, item))
                }
            }
        })
    }

    override fun onDestroy() {
        repository.clear()
        super.onDestroy()
    }

}