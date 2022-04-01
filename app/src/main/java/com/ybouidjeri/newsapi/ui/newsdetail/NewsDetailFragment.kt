package com.ybouidjeri.newsapi.ui.newsdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.ybouidjeri.newsapi.R
import com.ybouidjeri.newsapi.Utils
import com.ybouidjeri.newsapi.databinding.FragmentNewsDetailBinding
import com.ybouidjeri.newsapi.models.Headline
import com.ybouidjeri.newsapi.obtainViewModel
import com.ybouidjeri.newsapi.ui.main.SharedViewModel

import java.io.File

class NewsDetailFragment : Fragment() {


    private val model: SharedViewModel by activityViewModels()

    private lateinit var newsDetailViewModel: NewsDetailViewModel

    private lateinit var binding: FragmentNewsDetailBinding
    private lateinit var headline: Headline

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        newsDetailViewModel = obtainViewModel(NewsDetailViewModel::class.java)

        binding = FragmentNewsDetailBinding.inflate(layoutInflater)
        val root: View = binding.root

        val cacheDir = requireActivity().cacheDir.absolutePath

        model.selectedHeadline.observe(this, object: Observer<Headline> {
            override fun onChanged(value: Headline) {
                headline = value

                //set title
                val actionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
                actionBar?.title = headline.title

                binding.tvTitle.text = headline.title
                binding.tvDescription.text = headline.description
                binding.tvContent.text = headline.content

                if (headline.urlToImage != null && headline.urlToImage!!.isNotEmpty() ) {
                    val hashUrl = Utils.md5(headline.urlToImage!!)
                    val filePath = cacheDir + File.separator + hashUrl
                    val file = File(filePath)

                    newsDetailViewModel.fileReady.observe(this@NewsDetailFragment, object: Observer<Boolean> {
                        override fun onChanged(ready: Boolean) {
                            if (ready) {
                                Picasso.get()
                                    .load(file)
                                    .into(binding.ivHeadlineImg, object: Callback {
                                        override fun onSuccess() {
                                            binding.pbLoadingImage.visibility = View.INVISIBLE
                                        }
                                        override fun onError(ex : Exception) {
                                            ex.printStackTrace()
                                            binding.pbLoadingImage.visibility = View.INVISIBLE
                                            binding.ivHeadlineImg.setImageResource(R.drawable.ic_alert_circle_outline_grey600_48dp)
                                        }
                                    })
                            }
                        }
                    })

                    binding.pbLoadingImage.visibility = View.VISIBLE
                    newsDetailViewModel.getImageFile(headline.urlToImage!!, cacheDir)

                } else {
                    //binding.ivHeadlineImg.setImageResource(R.drawable.ic_image_outline_grey600_48dp)
                    binding.pbLoadingImage.visibility = View.INVISIBLE
                }
            }
        })

        binding.tvReadMore.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                if (::headline.isInitialized) {
                    if (headline.url.isNotEmpty()) {
                        //Start intent
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(headline.url)
                        startActivity(intent)
                    }
                }
            }
        })

        return  root
    }
}