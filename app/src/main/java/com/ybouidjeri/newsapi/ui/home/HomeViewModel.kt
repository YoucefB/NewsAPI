package com.ybouidjeri.newsapi.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybouidjeri.newsapi.data.NewsRepository
import com.ybouidjeri.newsapi.models.HeadlinesResponse
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    companion object {
        const val TAG = "NEWSAPI_HOME_VM"
    }

    val apiResponseMLD: MutableLiveData<HeadlinesResponse> = MutableLiveData<HeadlinesResponse>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    fun getTopHealinesResponse(sourceId: String) {
        val observable: Single<HeadlinesResponse> = newsRepository.getTopHeadlines(sourceId)
            .subscribeOn(Schedulers.io())//Up stream
            .observeOn(AndroidSchedulers.mainThread()) //Down stream

        val observer: SingleObserver<HeadlinesResponse> = object: SingleObserver<HeadlinesResponse> {

            override fun onSubscribe(d: Disposable) {
                loading.value = true
            }

            override fun onSuccess(headlinesResponse: HeadlinesResponse) {
                Log.d(TAG, "onSuccess")
                loading.value = false
                apiResponseMLD.value = headlinesResponse
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Error: ${e.message}")
                loading.value = false
                error.value = true
                e.printStackTrace()
            }
        }
        observable.subscribe(observer)
    }

}