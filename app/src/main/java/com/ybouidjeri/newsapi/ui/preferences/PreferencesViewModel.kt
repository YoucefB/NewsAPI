package com.ybouidjeri.newsapi.ui.preferences

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybouidjeri.newsapi.data.NewsRepository
import com.ybouidjeri.newsapi.models.SourcesResponse
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PreferencesViewModel(private val newsRepository: NewsRepository) : ViewModel(){

    val sourcesResponseMLD: MutableLiveData<SourcesResponse> = MutableLiveData<SourcesResponse>()
    val error = MutableLiveData<Boolean>()

    fun getSourcesResponse() {
        val observable: Single<SourcesResponse> = newsRepository.getSources()
            .subscribeOn(Schedulers.io())//Up stream
            .observeOn(AndroidSchedulers.mainThread()) //Down stream

        val observer: SingleObserver<SourcesResponse> = object: SingleObserver<SourcesResponse> {

            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(sourcesResponse: SourcesResponse) {
                sourcesResponseMLD.value = sourcesResponse
            }

            override fun onError(e: Throwable) {
                error.value = true
                e.printStackTrace()
            }
        }
        observable.subscribe(observer)
    }
}