package com.wazing.gank.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.wazing.gank.R
import com.wazing.gank.base.BaseFragment
import com.wazing.gank.bean.Gank
import com.wazing.gank.bean.History
import com.wazing.gank.bean.NetworkState
import com.wazing.gank.bean.Status
import com.wazing.gank.listener.OnItemClickListener
import com.wazing.gank.respository.HomeRepository
import com.wazing.gank.ui.activity.GankDetailsActivity
import com.wazing.gank.ui.activity.ImagePageActivity
import com.wazing.gank.ui.activity.MainActivity
import com.wazing.gank.ui.adapter.GankAdapter
import com.wazing.gank.ui.adapter.HistoryAdapter
import com.wazing.gank.utils.SpaceItemDecoration
import com.wazing.gank.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*
import org.jetbrains.anko.support.v4.sp
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var repository: HomeRepository

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        })[HomeViewModel::class.java]
    }

    private val adapter by lazy { GankAdapter() }

    private val historyAdapter by lazy { HistoryAdapter() }

    private var historyBottomSheetDialog: com.google.android.material.bottomsheet.BottomSheetDialog? = null

    override fun getLayoutId(): Int = R.layout.layout_refresh_recycler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.historyLiveData.observe(this, Observer { it ->
            it?.let {
                (activity as MainActivity).toolbar.title = it.desc
            }
        })

        viewModel.historyList.observe(this, Observer {
            it?.let { list ->
                if (historyBottomSheetDialog == null) {
                    val historyRv = androidx.recyclerview.widget.RecyclerView(requireContext())
                    historyRv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                    historyRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
                    historyRv.adapter = historyAdapter

                    historyBottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(activity!!)
                    historyBottomSheetDialog?.apply {
                        setContentView(historyRv)
                    }
                    historyAdapter.setOnItemClickListener(object : OnItemClickListener.SimpleOnItemClickListener<History>() {
                        override fun onItemClick(view: View, item: History, position: Int) {
                            viewModel.getHistoryGankList(item)
                            historyBottomSheetDialog?.dismiss()
                        }
                    })
                }
                historyAdapter.setNewItem(list)
            }
        })

        viewModel.refreshState.observe(this, Observer {
            swipe_refresh_layout.isRefreshing = it == NetworkState.LOADING
        })

        viewModel.historyNetworkState.observe(this, Observer { it ->
            when (it?.status) {
                Status.FAILED -> {
                    com.google.android.material.snackbar.Snackbar.make(view, it.msg!!,
                            com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE).setAction("重试") { viewModel.refresh() }
                            .show()
                }
                else -> {
                }
            }
        })

        viewModel.gankList.observe(this, Observer {
            it?.let { list ->
                adapter.setNewItem(list)
            }
        })

        swipe_refresh_layout.setOnRefreshListener { viewModel.refresh() }
        recycler_view.addItemDecoration(SpaceItemDecoration(spacing = sp(5), includeEdge = true))
        recycler_view.adapter = adapter

        adapter.setOnItemClickListener(object : OnItemClickListener.SimpleOnItemClickListener<Gank>() {
            override fun onItemClick(view: View, item: Gank, position: Int) {
                when (item.type) {
                    "福利" -> startActivity(ImagePageActivity.newInstance(activity!!, item))
                    else -> startActivity(GankDetailsActivity.newInstance(activity!!, item))
                }
            }
        })

        (activity as MainActivity).fab.setOnClickListener { _ ->
            historyBottomSheetDialog?.let { if (!it.isShowing) it.show() }
        }
    }

    override fun onDestroy() {
        viewModel.clearDisposables()
        super.onDestroy()
    }

}