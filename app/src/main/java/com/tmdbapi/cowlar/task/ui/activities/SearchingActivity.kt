package com.tmdbapi.cowlar.task.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmdbapi.cowlar.task.utility.CommonFunctions.launchActivity
import com.tmdbapi.cowlar.task.utility.CommonFunctions.shortToast
import com.codecollapse.tmdbmovies.models.datasources.utils.Status
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.adapter.MovieSearchingAdapter
import com.tmdbapi.cowlar.task.databinding.ActivitySearchingBinding
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.utility.NetworkConnection
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

@AndroidEntryPoint
class SearchingActivity : AppCompatActivity() {
    private val TAG = "SearchingActivity"
    private lateinit var binding: ActivitySearchingBinding
    private lateinit var searhingMovieAdapter: MovieSearchingAdapter
    private val startupViewModel: StartupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNetworkConnection()
        initSearchViewLaunch()
        initSearchDataAdapter()
        initAdapterEventResponse()
    }

    private fun initNetworkConnection() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            binding.noInternet.isVisible = !isConnected
        }
    }
    private fun initSearchViewLaunch() {
        lifecycleScope.launch {
            binding.searchView.getQueryTextChangeStateFlow()
                .debounce(500)
                .filter { query ->
                    if (query.isEmpty()) {
                        withContext(Dispatchers.Main){
                            shortToast(R.string.enter_valid_name)
                        }
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    startupViewModel.getMovieSearchingResult(query)
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
                                    searhingMovieAdapter.submitList(result.data as ArrayList<MovieSearchResult.MovieDetails>)
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
            searhingMovieAdapter = MovieSearchingAdapter(this)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = searhingMovieAdapter
        }
    }

    private fun initAdapterEventResponse() {
        searhingMovieAdapter.selectedMovie.observe(this, Observer {
            launchActivity<MovieDetailedActivity>() {
                putExtra("movieId", it.id)
            }
        })

    }
}