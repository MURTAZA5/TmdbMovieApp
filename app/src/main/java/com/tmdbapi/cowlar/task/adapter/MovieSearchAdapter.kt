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
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult

class MovieSearchAdapter(mContext : Context) : ListAdapter<MovieSearchResult.MovieDetails, MovieSearchAdapter.MyViewHolder>(CHARACTER_COMPARATOR) {
    var context = mContext
    var selectedMovie = MutableLiveData<MovieSearchResult.MovieDetails>()

    inner class MyViewHolder(private val binding: MoviesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: MovieSearchResult.MovieDetails?) {
            val path = AppConstants.LOAD_BACK_DROP_BASE_URL + (response?.poster_path ?: "")
            binding.textViewMovieName.text = response?.title ?: "Movie Title"

            Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.no_thumb) // Placeholder while loading
                .error(R.drawable.no_thumb) // Error placeholder
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
        private val CHARACTER_COMPARATOR =
            object : DiffUtil.ItemCallback<MovieSearchResult.MovieDetails>() {
                override fun areItemsTheSame(
                    oldItem: MovieSearchResult.MovieDetails,
                    newItem: MovieSearchResult.MovieDetails
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: MovieSearchResult.MovieDetails,
                    newItem: MovieSearchResult.MovieDetails
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
