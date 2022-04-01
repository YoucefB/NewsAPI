package com.ybouidjeri.newsapi.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ybouidjeri.newsapi.Utils
import com.ybouidjeri.newsapi.adapters.HeadlinesAdapter
import com.ybouidjeri.newsapi.databinding.FragmentHomeBinding
import com.ybouidjeri.newsapi.models.Headline

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ybouidjeri.newsapi.R
import com.ybouidjeri.newsapi.models.HeadlinesResponse
import com.ybouidjeri.newsapi.models.Source
import com.ybouidjeri.newsapi.obtainViewModel
import com.ybouidjeri.newsapi.ui.main.SharedViewModel

class HomeFragment : Fragment() {

    companion object {
        const val TAG = "NEWSAPI_HOME_FRAG"
    }

    lateinit var headlinesList: MutableList<Headline>
    private val model: SharedViewModel by activityViewModels()

    private lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefSource: Source



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel = obtainViewModel(HomeViewModel::class.java)



        binding = FragmentHomeBinding.inflate(layoutInflater)
        val root: View = binding.root
        val srl: SwipeRefreshLayout = binding.srl
        val rvHeadlines = binding.rvHeadlines
        headlinesList = mutableListOf()
        prefSource = Utils.getPreferedSource(requireContext())

        homeViewModel.apiResponseMLD.observe(this, object : Observer<HeadlinesResponse> {
            override fun onChanged(headlinesResponse: HeadlinesResponse?) {
                //stop showing progressbar
                srl.isRefreshing = false

                if (headlinesResponse != null) {
                    binding.tvError.visibility = View.GONE
                    Log.d(TAG, "status: ${headlinesResponse.status}")
                    Log.d(TAG, "totalResults: ${headlinesResponse.totalResults}")

                    headlinesList.clear()
                    headlinesList.addAll(headlinesResponse.headlines)
                    headlinesAdapter.notifyDataSetChanged()
                } else {
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        })

        homeViewModel.loading.observe(this, object: Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    binding.tvError.visibility = View.INVISIBLE

                    if (srl.isRefreshing) {
                        binding.pbLoading.visibility = View.INVISIBLE
                    } else {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                } else {
                    binding.pbLoading.visibility = View.INVISIBLE
                }
            }
        })


        homeViewModel.error.observe(this, object: Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    headlinesList.clear()
                    headlinesAdapter.notifyDataSetChanged()
                    binding.tvError.visibility = View.VISIBLE
                    srl.isRefreshing = false

                } else {
                    binding.tvError.visibility = View.INVISIBLE
                }
            }
        })

        //Swipe to Refresh
        srl.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")
            homeViewModel.getTopHealinesResponse(prefSource.id)
        }

        val clickListener = object : HeadlinesAdapter.ClickListener {
            override fun onPositionClicked(view: View, position: Int) {
                //prevent app crash when updating and user click on list Item
                if (position < headlinesList.size) {
                    val headline = headlinesList.get(position)
                    model.setSelectetHeadline(headline)
                    findNavController().navigate(R.id.action_nav_home_to_newsDetailFragment)
                }
            }
        }

        val cacheDirectory = requireActivity().cacheDir.absolutePath
        rvHeadlines.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        headlinesAdapter = HeadlinesAdapter(headlinesList, clickListener, cacheDirectory)
        rvHeadlines.setAdapter(headlinesAdapter)

        binding.tvError.visibility = View.INVISIBLE
        return root
    }

    override fun onResume() {
        super.onResume()
        //set title
        val actionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = prefSource.name

        prefSource = Utils.getPreferedSource(requireContext())
        homeViewModel.getTopHealinesResponse(prefSource.id)
    }
}