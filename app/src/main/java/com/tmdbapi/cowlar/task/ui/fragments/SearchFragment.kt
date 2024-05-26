package com.tmdbapi.cowlar.task.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.tmdbapi.cowlar.task.utility.CommonFunctions.launchActivity
import com.tmdbapi.cowlar.task.utility.CommonFunctions.shortToast
import com.codecollapse.tmdbmovies.models.datasources.utils.Status
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.adapter.MovieSearchAdapter
import com.tmdbapi.cowlar.task.databinding.FragmentSearchBinding
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.ui.activities.MovieDetailedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val TAG = "SearchFragment"
    private var param1: String? = null
    private var param2: String? = null
    private val startupViewModel: StartupViewModel by viewModels()
    lateinit var binding: FragmentSearchBinding
    private lateinit var upComingMoviesAdapter: MovieSearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchDataAdapter()
        iniSearchViewLaunch()
        initSearchCacheData()
        initAdapterEventResponse()
    }

    private fun initSearchCacheData() {
           viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
               startupViewModel.getSearchCacheData().collect { result ->
                   when (result.status) {
                       Status.LOADING -> {
                           Log.e(TAG, "Loading ....: ")
                       }

                       Status.SUCCESS -> {
                               withContext(Dispatchers.Main){
                                   if (!result.data.isNullOrEmpty()) {
                                       upComingMoviesAdapter.submitList(result.data as ArrayList<MovieSearchResult.MovieDetails>)
                               }

                           }

                       }

                       Status.ERROR -> {
                           Log.e(TAG, "Error: ${result.message}")
                       }
                   }
               }
           }
    }

    private fun iniSearchViewLaunch() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.searchView.getQueryTextChangeStateFlow()
                .debounce(500)
                .filter { query ->
                    if (query.isEmpty()) {
                        withContext(Dispatchers.Main){
                            requireContext().shortToast(R.string.enter_valid_name)
                        }
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    startupViewModel.getSearchResult(query)
                }
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    when (result.status) {
                        Status.LOADING -> {
                            Log.e(TAG, "Loading ....: ")
                        }

                        Status.SUCCESS -> {
                            withContext(Dispatchers.Main) {
                                if (!result.data.isNullOrEmpty()) {
                                    upComingMoviesAdapter.submitList(result.data as ArrayList<MovieSearchResult.MovieDetails>)
                                }
                            }

                        }

                        Status.ERROR -> {
                            Log.e(TAG, "Error: ${result.message}")
                        }
                    }
                }
        }

    }

    fun SearchView.getQueryTextChangeStateFlow(): StateFlow<String> {

        val query = MutableStateFlow("")

        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                query.value = newText
                return true
            }
        })

        return query

    }

    private fun initSearchDataAdapter() {
        binding.recyclerViewSearch.let { recyclerView ->
            upComingMoviesAdapter = MovieSearchAdapter(requireContext())
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.adapter = upComingMoviesAdapter
        }
    }

    private fun initAdapterEventResponse() {
        upComingMoviesAdapter.selectedMovie.observe(viewLifecycleOwner, Observer {
            requireContext().launchActivity<MovieDetailedActivity>() {
                putExtra("movieId", it.id)
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}