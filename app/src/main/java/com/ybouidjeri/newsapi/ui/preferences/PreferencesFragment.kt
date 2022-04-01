package com.ybouidjeri.newsapi.ui.preferences

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ybouidjeri.newsapi.databinding.FragmentPreferencesBinding
import com.ybouidjeri.newsapi.models.Source
import com.ybouidjeri.newsapi.models.SourcesResponse

import com.ybouidjeri.newsapi.Utils
import com.ybouidjeri.newsapi.obtainViewModel
import java.io.File


class PreferencesFragment : Fragment() {

    companion object {
        const val TAG = "NEWSAPI_PREF_FRAG"
    }

    lateinit var sourcesList: MutableList<Source>

    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var binding: FragmentPreferencesBinding
    lateinit var sourcesSpinnerListener : AdapterView.OnItemSelectedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        preferencesViewModel = obtainViewModel(PreferencesViewModel::class.java)
        binding = FragmentPreferencesBinding.inflate(layoutInflater)
        val root: View = binding.root

        sourcesList = mutableListOf()
        val prefSource = Utils.getPreferedSource(requireContext())
        Log.d(TAG, "Pref Source: ${prefSource.name}")

        preferencesViewModel.sourcesResponseMLD.observe(this, object : Observer<SourcesResponse> {
            override fun onChanged(sourcesResponse: SourcesResponse?) {

                if (sourcesResponse != null) {
                    sourcesList.clear()
                    sourcesList.addAll(sourcesResponse.sources)
                    loadSourcesInSpinner(sourcesList)

                    //set source in spinner
                    for (i in 0..sourcesList.size-1) {
                        val source = sourcesList.get(i)
                        if (source.id == prefSource.id) {
                            binding.spSources.setSelection(i)
                            break
                        }
                    }
                    binding.spSources.setOnItemSelectedListener(sourcesSpinnerListener)

                } else {
                    Toast.makeText(requireContext(), "Error loading list of sources from server !",
                    Toast.LENGTH_LONG).show()
                }
            }
        })

        preferencesViewModel.error.observe(this, object: Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    Toast.makeText(requireContext(), "Error loading list of sources from server !",
                        Toast.LENGTH_LONG).show()

                    //Add default Source form Shared Pref as onLy item
                    sourcesList.clear()
                    sourcesList.add(prefSource)
                    loadSourcesInSpinner(sourcesList)
                }
            }
        })


        //When user select an source item in Spinner ==> Save item in SharedPreferences
        sourcesSpinnerListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val source = sourcesList.get(position)
                Utils.setPreferedSource(requireContext(), source)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.btnClearCache.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val cacheDir: String = requireActivity().cacheDir.absolutePath
                val directory = File(cacheDir)
                val files = directory.listFiles()
                for (i in files!!.indices) {
                    val file = files[i]
                    file.delete()
                }
            }
        })

        preferencesViewModel.getSourcesResponse()
        return root
    }

    fun loadSourcesInSpinner(sources: List<Source>) {
        //Convert List<Object>  => List<String>
        val sourcesListStr : MutableList<String> = ArrayList()
        for (source in sources ) {
            sourcesListStr.add(source.name)
        }
        val sourcesAdapter : ArrayAdapter<String> =  ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        sourcesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourcesAdapter.addAll(sourcesListStr)
        binding.spSources.setAdapter(sourcesAdapter)
    }
}