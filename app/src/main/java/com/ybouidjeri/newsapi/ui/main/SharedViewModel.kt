package com.ybouidjeri.newsapi.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybouidjeri.newsapi.models.Headline

class SharedViewModel : ViewModel() {

    val selectedHeadline = MutableLiveData<Headline>()

    fun setSelectetHeadline(headline: Headline) {
        selectedHeadline.value = headline
    }
}