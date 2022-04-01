package com.ybouidjeri.newsapi.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ybouidjeri.bbcnews.data.HeadlinesInterface
import com.ybouidjeri.newsapi.data.ServiceBuilder
import com.ybouidjeri.newsapi.R
import com.ybouidjeri.newsapi.Utils
import com.ybouidjeri.newsapi.databinding.HeadlineListItemBinding
import com.ybouidjeri.newsapi.models.Headline
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.File
import java.lang.ref.WeakReference

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class HeadlinesAdapter() : RecyclerView.Adapter<HeadlinesAdapter.HeadlineViewHolder>() {

    private lateinit var headlines: List<Headline>
    private lateinit var listener: ClickListener
    private lateinit var cacheDir: String

    constructor(headlines: List<Headline>, listener: ClickListener, cacheDir: String) : this() {
        this.headlines = headlines
        this.listener = listener
        this.cacheDir = cacheDir
    }

    companion object {
        const val TAG = "NEWSAPI_ADAPTER"
    }

    //private var headlines: List<Headline> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val context = parent.context
        val itemView : View = LayoutInflater.from(context)
            .inflate(R.layout.headline_list_item, parent, false)

        return HeadlineViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        val headline : Headline = headlines[position]
        holder.bind(headline)
    }

    override fun getItemCount(): Int {
        return headlines.size
    }

    inner class HeadlineViewHolder(itemView: View, listener: ClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val binding = HeadlineListItemBinding.bind(itemView)
        private val listenerRef: WeakReference<ClickListener> = WeakReference(listener)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(headline: Headline) {
            binding.tvTitle.text = headline.title
            binding.ivHeadlineImg.setImageResource(R.drawable.ic_image_outline_grey600_48dp)

            if (headline.urlToImage != null && headline.urlToImage.isNotEmpty()) {
                //download image
                binding.pbLoadingImage.visibility = View.VISIBLE
                val hashUrl = Utils.md5(headline.urlToImage)
                val filePath = cacheDir + File.separator + hashUrl
                val file = File(filePath)
                if (file.exists()) {
                    Log.d(TAG, "File in cache : already downloaded")
                    loadImageFromFile(file)
                } else {
                    Log.d(TAG, "File not found in cache : downloading...")
                    val api: HeadlinesInterface = ServiceBuilder.buildService(HeadlinesInterface::class.java)

                    val observable: Single<ResponseBody> = api.downloadFile(headline.urlToImage)
                        .subscribeOn(Schedulers.io())//Up stream
                        .observeOn(AndroidSchedulers.mainThread()) //Down stream

                    val observer: SingleObserver<ResponseBody> = object: SingleObserver<ResponseBody> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onSuccess(responseBody: ResponseBody) {
                            val inputStream = responseBody.byteStream()
                            val savedInCache: Boolean = Utils.saveInputStreamInFile(inputStream, filePath)
                            binding.pbLoadingImage.visibility = View.INVISIBLE
                            if (savedInCache) {
                                Log.d(TAG, "Saving file in cache Successed")
                                loadImageFromFile(file)
                            } else {
                                Log.d(TAG, "Saving file in cache Failed")
                                binding.ivHeadlineImg.setImageResource(R.drawable.ic_alert_circle_outline_grey600_48dp)
                                binding.pbLoadingImage.visibility = View.INVISIBLE
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "Error: ${e.message}")
                            e.printStackTrace()
                            binding.ivHeadlineImg.setImageResource(R.drawable.ic_alert_circle_outline_grey600_48dp)
                            binding.pbLoadingImage.visibility = View.INVISIBLE
                        }
                    }
                    observable.subscribe(observer)
                }

            } else {
                binding.pbLoadingImage.visibility = View.INVISIBLE
            }

        }

        fun loadImageFromFile(file: File) {
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

        override fun onClick(view: View?) {
            val pos : Int = adapterPosition
            listenerRef.get()?.onPositionClicked(view!!, pos)
        }
    }

    interface ClickListener {
        fun onPositionClicked(view: View, position: Int)
    }
}