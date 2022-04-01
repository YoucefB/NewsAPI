package com.ybouidjeri.newsapi

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner


fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>): T {
    val newsRepository = (this.requireActivity().application as MyApp).newsRepository
    return ViewModelProvider(this as ViewModelStoreOwner, ViewModelFactory(newsRepository)).get(viewModelClass)
}