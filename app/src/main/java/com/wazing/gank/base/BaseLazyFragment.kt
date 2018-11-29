package com.wazing.gank.base

import android.os.Bundle
import android.view.View

abstract class BaseLazyFragment : BaseFragment() {

    private var isPrepared = false
    private var isFirstVisible = true
    private var isFirstInvisible = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPrepare()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                initPrepare()
            } else {
                onUserVisible()
                onUserVisible(true)
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false
                onFirstUserInvisible()
                onFirstUserVisible(false)
            } else {
                onUserInvisible()
                onUserVisible(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    @Synchronized
    private fun initPrepare() {
        if (isPrepared) {
            onFirstUserVisible()
            onFirstUserVisible(true)
        } else isPrepared = true
    }

    private fun onFirstUserVisible() {}

    private fun onFirstUserInvisible() {}

    private fun onUserVisible() {}

    private fun onUserInvisible() {}

    abstract fun onFirstUserVisible(isVisibleToUser: Boolean)

    abstract fun onUserVisible(isVisibleToUser: Boolean)

    private fun initViews() {}

}