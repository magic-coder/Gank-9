package com.wazing.gank.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

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