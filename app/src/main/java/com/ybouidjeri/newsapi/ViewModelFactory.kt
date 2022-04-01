package com.ybouidjeri.newsapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ybouidjeri.newsapi.data.NewsRepository
import com.ybouidjeri.newsapi.ui.home.HomeViewModel
import com.ybouidjeri.newsapi.ui.newsdetail.NewsDetailViewModel
import com.ybouidjeri.newsapi.ui.preferences.PreferencesViewModel

class ViewModelFactory constructor(private val newsRepository: NewsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(newsRepository)
                isAssignableFrom(PreferencesViewModel::class.java) -> PreferencesViewModel(newsRepository)
                isAssignableFrom(NewsDetailViewModel::class.java) -> NewsDetailViewModel(newsRepository)

                else ->
                    throw IllegalArgumentException("ViewModel class (${modelClass.name}) is not mapped")
            }
        } as T
}