package com.tmdbapi.cowlar.task.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tmdbapi.cowlar.task.utility.AppConstants
import com.tmdbapi.cowlar.task.utility.CommonFunctions.launchActivity
import com.tmdbapi.cowlar.task.utility.CommonFunctions.shortToast
import com.codecollapse.tmdbmovies.models.adapter.MoviesGenresAdapter
import com.codecollapse.tmdbmovies.models.datamodels.MovieDetail
import com.codecollapse.tmdbmovies.models.datasources.utils.Status
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.databinding.ActivityMovieDetailedBinding
import com.tmdbapi.cowlar.task.ui.fragments.WatchVideoFragment
import com.tmdbapi.cowlar.task.utility.Converter
import com.tmdbapi.cowlar.task.utility.NetworkConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MovieDetailedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailedBinding
    private lateinit var moviesGenresAdapter: MoviesGenresAdapter
    private val startupViewModel: StartupViewModel by viewModels()
    private var movieId = 0
    var savedState: Bundle? = null
    var movieDetail: MovieDetail? = null
    private var isDataSync = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedState = savedInstanceState
        initViewClicks()
        initRecyclerView()
        getMovieDetailed()
        initNetworkConnection()

    }

    private fun initNetworkConnection() {
        val networkConnection = NetworkConnection(this)
        networkConnection.observe(this) { isConnected ->
            binding.noInternet.isVisible = !isConnected
            if (isDataSync && isConnected) {
                getMovieDetailed()
            }
        }
    }

    private fun getMovieDetailed() {
        if (intent.hasExtra("movieId")) {
            movieId = intent.getIntExtra("movieId", 0)
            movieId.let {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        startupViewModel.getMovieDetails(movieId, "en").collect {
                            when (it.status) {
                                Status.LOADING -> {}
                                Status.SUCCESS -> {
                                    withContext(Dispatchers.Main) {
                                        if (it.data != null) {
                                            movieDetail = it.data

                                            movieDetail?.let {
                                                val path =
                                                    AppConstants.LOAD_BACK_DROP_BASE_URL + it.backdrop_path
                                                Glide.with(this@MovieDetailedActivity).load(path)
                                                    .into(binding.imageViewPosture)
                                                binding.textViewOverView.text =
                                                    it.overview.toString()

                                                if (it.release_date.isNullOrEmpty()) {
                                                    val movieReleasedate = Converter.parseDate(
                                                        it.release_date!!,
                                                        dstPattern = "MMM dd, yyyy"
                                                    )
                                                    binding.tvMovieDate.text =
                                                        movieReleasedate.toString()
                                                } else {
                                                    binding.tvMovieDate.text = ""
                                                }
                                                binding.tvMovieDate
                                                moviesGenresAdapter.submitList(it.genres)
                                            }

                                        }
                                    }
                                }

                                Status.ERROR -> {
                                    withContext(Dispatchers.Main) {
                                        shortToast(R.string.something_went_wroung)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.movieGenresRecyclerView.let { recyclerView ->
            moviesGenresAdapter = MoviesGenresAdapter(this)
            recyclerView.layoutManager =
                GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = moviesGenresAdapter
        }
    }

    private fun initViewClicks() {
        binding.run {
            btnGetTicket.setOnClickListener {
                launchActivity<TicketActivity>() {
                    movieDetail?.let { movieDetail ->
                        val hours: Int =
                            movieDetail.runtime!!.div(60) //since both are ints, you get an int

                        val minutes: Int = movieDetail.runtime!! % 60
//                                        binding.extensionLayout.textViewDuration.text = "${hours}h ${minutes}m"
                        putExtra("audiID", movieDetail.id)
                        putExtra("movieID", movieDetail.imdb_id)
                        putExtra("time", "${hours}h ${minutes}m")
                        putExtra("title", movieDetail.title)
                        putExtra("name", "ali")
                        putExtra("email", "ali@gmail.com")
                        putExtra("number", "57585939")

                    }
                }
            }
        }

        binding.btnWatchTrailer.setOnClickListener {
            movieDetail?.let {
                it.id?.let { it1 ->
                    it.title?.let { it2 ->
                        addFragment(it1, it2)
                    }
                }
            } ?: run {
                shortToast(R.string.no_video_data_found)
            }
        }

        binding.include.imageViewBack.setOnClickListener { backPress() }
    }

    fun addFragment(videoID: Int, videoTitle: String) {
        val fragment = WatchVideoFragment.newInstance(videoID, videoTitle)
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: WatchVideoFragment) {
        if (savedState == null) {
            binding.fragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                .setReorderingAllowed(true).addToBackStack("").commit()
        }
    }

    override fun onBackPressed() {
        backPress()
    }

    fun backPress() {
        val count = supportFragmentManager.backStackEntryCount
        if (count != 1) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStackImmediate()
            binding.fragmentContainer.visibility = View.GONE
            binding.navHostFragment.visibility = View.GONE
        }
    }


}