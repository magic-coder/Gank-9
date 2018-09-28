package com.wazing.gank.respository.net

import com.wazing.gank.bean.BaseResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxHelper {

    fun <T> ioMain(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> handlerResult(): ObservableTransformer<BaseResult<T>, List<T>> {
        return ObservableTransformer { upstream ->
            upstream.flatMap { t ->
                if (!t.error) {
                    val observable: Observable<List<T>> = object :Observable<List<T>>(){
                        override fun subscribeActual(observer: Observer<in List<T>>) {
                            try {
                                observer.onNext(t.results)
                                observer.onComplete()
                            } catch (e: Exception) {
                                observer.onError(e)
                            }
                        }
                    }
                    observable
                } else {
                    Observable.error { ApiCodeException("获取数据失败") }
                }
            }
        }
    }

}