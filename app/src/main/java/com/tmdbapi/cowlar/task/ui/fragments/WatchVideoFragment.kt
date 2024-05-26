package com.tmdbapi.cowlar.task.ui.fragments

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tmdbapi.cowlar.task.utility.AppConstants
import com.tmdbapi.cowlar.task.utility.CommonFunctions.shortToast
import com.codecollapse.tmdbmovies.models.datasources.utils.Status
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.databinding.FragmentWatchVideoBinding
import com.tmdbapi.cowlar.task.datamodels.VideoWatchListDTO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "video_id"
private const val ARG_PARAM2 = "video_title"

/**
 * A simple [Fragment] subclass.
 * Use the [WatchVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class WatchVideoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var video_id: Int? = null
    private var  video_title: String? = null
    lateinit var binding: FragmentWatchVideoBinding
    private val startupViewModel: StartupViewModel by viewModels()
    var movieDetail: List<VideoWatchListDTO.VideoDTO>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            video_id = it.getInt(ARG_PARAM1)
             video_title = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWatchVideoBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMovieId()
        initClickEvents()
    }
    private fun getMovieId() {
        video_id?.let { video__id ->
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    startupViewModel.getMovieTrailerDetails(video__id).collect {
                        withContext(Dispatchers.Main) {
                            when (it.status) {
                                Status.LOADING -> {
                                 binding.pbLoading.visibility=View.VISIBLE
                                }

                                Status.SUCCESS -> {
                                    if (it.data != null && it.data.isNotEmpty()) {
                                        movieDetail = it.data
                                        movieDetail?.let {
                                            val videoApiUrl =
                                                AppConstants.MOVIE_TRAILER_BASE_URL + it.first().key
                                            val videoApiSettings=extractVideoId(videoApiUrl)
                                            initPlayer(videoApiSettings)

                                        }?:run {
                                            binding.pbLoading.visibility=View.GONE
                                            requireActivity().shortToast(R.string.something_went_wroung)
                                        }
                                    }else{
                                        binding.pbLoading.visibility=View.GONE
                                        requireActivity().shortToast(R.string.something_went_wroung)
                                    }
                                }

                                Status.ERROR -> {
                                    binding.pbLoading.visibility=View.GONE
                                    requireActivity().shortToast(R.string.something_went_wroung)
                                    requireActivity().onBackPressed()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun initClickEvents() {
        binding?.run {
            tvTitle.setText(video_title.toString()  )
            btnDone.setOnClickListener {
                 requireActivity().onBackPressed()
            }

        }
    }

    private fun initPlayer(url:String) {
        lifecycle.addObserver(binding.youtubePlayer)
        binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(url, 0f)
                youTubePlayer.play()
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                if (state == PlayerConstants.PlayerState.ENDED) {
                    requireActivity().onBackPressed()
                }else if (state ==PlayerConstants.PlayerState.BUFFERING){
                    binding.pbLoading.visibility=View.GONE

                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                binding.pbLoading.visibility=View.GONE
                requireActivity().shortToast(R.string.something_went_wroung)
                requireActivity().onBackPressed()
                // Handle player errors if needed
            }
        })
        lifecycle.addObserver( binding.youtubePlayer)
    }

    private fun extractVideoId(youtubeUrl: String): String {
        val uri = Uri.parse(youtubeUrl)
        return uri.getQueryParameter("v") ?: ""
    }
    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(binding.youtubePlayer)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val constraintLayout = binding.root as ConstraintLayout // Assuming the root view is a ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        // Check the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            constraintSet.constrainWidth(binding.youtubePlayer.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(binding.youtubePlayer.id, ConstraintSet.MATCH_CONSTRAINT)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            constraintSet.constrainWidth(binding.youtubePlayer.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(binding.youtubePlayer.id, ConstraintSet.WRAP_CONTENT)
        }

        constraintSet.applyTo(constraintLayout)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WatchVideoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
            WatchVideoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}