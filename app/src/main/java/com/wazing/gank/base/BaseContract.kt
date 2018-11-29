package com.wazing.gank.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

interface BaseContract {

    interface View {

        fun onApiFail(msg: String)
    }

    interface Presenter : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun detachView()
    }

    interface Model {

        fun onDestroy()
    }

}