package com.tmdbapi.cowlar.task.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tmdbapi.cowlar.task.utility.AppConstants
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.databinding.MoviesLayoutBinding
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies


class UpComingMoviesAdapter(mContext: Context) :
    ListAdapter<TMDBMovies.Results, UpComingMoviesAdapter.MyViewHolder>(CHARACTER_COMPARATOR) {
    var context = mContext
    var selectedMovie = MutableLiveData<TMDBMovies.Results>()

    inner class MyViewHolder(private val binding: MoviesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: TMDBMovies.Results?) {
            val path = AppConstants.LOAD_BACK_DROP_BASE_URL + (response?.backdrop_path ?: "")
            binding.textViewMovieName.text = response?.title ?: "Movie Title"
            Log.e("TAG", "bind: ${response?.title}")
            Log.e("TAG", "bind: ${response?.overview}")
            Log.e("TAG", "bind: ${path}")
            Log.e("TAG", "bind: ${response?.popularity}")
            Glide.with(context)
                .load(path)
                .centerCrop()
                 .placeholder(R.drawable.no_poster) // Placeholder while loading
                .error(R.drawable.no_poster) // Error placeholder
                .into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        binding.imageViewMoviePosture.setImageDrawable(resource)
                        Log.e("Glide", "Image load successful")
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.imageViewMoviePosture.setImageDrawable(placeholder)
                        Log.e("Glide", "Image load cleared")
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        binding.imageViewMoviePosture.setImageDrawable(errorDrawable)
                        Log.e("Glide", "Image load failed")
                    }
                })

            binding.imageViewMoviePosture.setOnClickListener {
                selectedMovie.postValue(response!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            MoviesLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    companion object {
        private val CHARACTER_COMPARATOR = object : DiffUtil.ItemCallback<TMDBMovies.Results>() {
            override fun areItemsTheSame(
                oldItem: TMDBMovies.Results,
                newItem: TMDBMovies.Results
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: TMDBMovies.Results,
                newItem: TMDBMovies.Results
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}