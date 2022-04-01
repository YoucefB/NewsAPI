package com.ybouidjeri.newsapi.data

import com.ybouidjeri.bbcnews.data.HeadlinesInterface
import com.ybouidjeri.newsapi.models.*
import io.reactivex.Single
import okhttp3.ResponseBody

class ApiRepository(private val api: HeadlinesInterface): NewsRepository {

    override fun getTopHeadlines(source: String): Single<HeadlinesResponse> {
        return api.getTopHeadlinesResponseBySourcesIds(source)
    }

    override fun getSources(): Single<SourcesResponse> {
        return api.getSourcesResponse()
    }

    override fun getFile(fileUrl: String): Single<ResponseBody> {
        return api.downloadFile(fileUrl)
    }


}