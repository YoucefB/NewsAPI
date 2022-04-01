package com.ybouidjeri.newsapi

import androidx.multidex.MultiDexApplication
import com.ybouidjeri.bbcnews.data.HeadlinesInterface
import com.ybouidjeri.newsapi.data.ApiRepository
import com.ybouidjeri.newsapi.data.NewsRepository
import com.ybouidjeri.newsapi.data.ServiceBuilder

class MyApp: MultiDexApplication() {

    @Override
    override fun onCreate() {
        super.onCreate()
    }

    val newsRepository : NewsRepository
        get() = ApiRepository(ServiceBuilder.buildService(HeadlinesInterface::class.java))
}