package com.tmdbapi.cowlar.task.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmdbapi.cowlar.task.utility.CommonFunctions.launchActivity
import com.codecollapse.tmdbmovies.models.datasources.utils.Status
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.tmdbapi.cowlar.task.adapter.UpComingMoviesAdapter
import com.tmdbapi.cowlar.task.databinding.FragmentHomeBinding
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import com.tmdbapi.cowlar.task.ui.activities.MovieDetailedActivity
import com.tmdbapi.cowlar.task.ui.activities.SearchingActivity
import com.tmdbapi.cowlar.task.utility.NetworkConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val TAG = "HomeFragment"
    private var param1: String? = null
    private var param2: String? = null
    private val startupViewModel: StartupViewModel by viewModels()
    lateinit var binding:FragmentHomeBinding
    private lateinit var upComingMoviesAdapter: UpComingMoviesAdapter

    private var isDataSync=false
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
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickEvents()
        initDataAdapter()
        initUpcomingApiLaunch()
        initAdapterEventResponse()
        initNetworkConnection()

    }

    private fun initClickEvents() {
        binding.ivSearching.setOnClickListener {
            requireContext().launchActivity<SearchingActivity> {  }
        }
    }
    private fun initNetworkConnection() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner) { isConnected ->
           if (isDataSync && isConnected){
               initUpcomingApiLaunch()
           }
        }
    }
    private fun initAdapterEventResponse() {
        upComingMoviesAdapter.selectedMovie.observe(viewLifecycleOwner, Observer {
            requireContext().launchActivity<MovieDetailedActivity>() {
                putExtra("movieId", it.id)
            }
        })

    }

    private fun initUpcomingApiLaunch() {
        lifecycleScope.launch(Dispatchers.IO) {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
                startupViewModel.getUpComingMovies().collect{
                    when (it.status) {
                        Status.LOADING -> {
                            Log.e(TAG, "Loading ....: ")
                        }
                        Status.SUCCESS -> {
                            isDataSync=false
                            withContext(Dispatchers.Main){
                                if (!it.data.isNullOrEmpty()) {
                                    upComingMoviesAdapter.submitList(it.data as  ArrayList<TMDBMovies.Results>)
                                }
                            }

                        }
                        Status.ERROR -> {
                            isDataSync=true
                            Log.e(TAG, "Error: ${it.message}")
                        }
                    }
                }
            }
//        }
    }

    private fun initDataAdapter() {
        binding.upComingRecyclerView.let { recyclerView ->
            upComingMoviesAdapter = UpComingMoviesAdapter(requireContext())
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            recyclerView.adapter = upComingMoviesAdapter
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}