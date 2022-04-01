package com.ybouidjeri.newsapi.ui.newsdetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybouidjeri.newsapi.Utils
import com.ybouidjeri.newsapi.adapters.HeadlinesAdapter
import com.ybouidjeri.newsapi.data.NewsRepository
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.File

class NewsDetailViewModel(private val newsRepository: NewsRepository): ViewModel() {

    companion object {
        const val TAG= "NEWSAPI_DETAIL_VM"
    }

    val fileReady: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    fun getImageFile(imageUrl: String, cacheDir: String) {
        //First check if file exist in cache
        val hashUrl = Utils.md5(imageUrl)
        val filePath = cacheDir + File.separator + hashUrl
        val file = File(filePath)
        if (file.exists()) {
            Log.d(TAG, "File in cache : already downloaded")
            fileReady.value = true
        } else {
            Log.d(TAG, "File not found in cache : downloading...")

            val observable: Single<ResponseBody> = newsRepository.getFile(imageUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

            val observer: SingleObserver<ResponseBody> = object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    loading.value = true
                }

                override fun onSuccess(responseBody: ResponseBody) {
                    loading.value = false
                    val inputStream = responseBody.byteStream()
                    val savedInCache: Boolean = Utils.saveInputStreamInFile(inputStream, filePath)
                    if (savedInCache) {
                        Log.d(TAG, "Saving file in cache Successed")
                        fileReady.value = true
                    } else {
                        Log.d(HeadlinesAdapter.TAG, "Saving file in cache Failed")
                        error.value = true
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Error: ${e.message}")
                    e.printStackTrace()
                    loading.value = false
                    error.value = true
                }
            }
            observable.subscribe(observer)
        }
    }


}