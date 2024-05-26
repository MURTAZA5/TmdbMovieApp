package com.tmdbapi.cowlar.task.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.codecollapse.tmdbmovies.models.datamodels.MovieDetail
import com.codecollapse.tmdbmovies.models.datasources.utils.Resource
import com.codecollapse.tmdbmovies.models.repositories.StartUpRepository
import com.codecollapse.tmdbmovies.ui.viewmodel.StartupViewModel
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StartupViewModelTest {

    @get:Rule
    val instantTaskExecutorRule =
        InstantTaskExecutorRule() // Forces LiveData updates on main thread

    @MockK
    private lateinit var startUpRepository: StartUpRepository

    private lateinit var viewModel: StartupViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = StartupViewModel(startUpRepository)
    }

    @Test
    fun `getMovieDetails returns movie details from repository`() = runBlockingTest {
        val movieId = 1
        val movieLanguage = "en-US"
        val flowMovieDetail = flow {
            emit(Resource.success(
                MovieDetail(id = 1, backdrop_path = "")))
        }
        coEvery {
            startUpRepository.getMovieDetails(
                movieId,
                movieLanguage
            )
        } returns flowMovieDetail

        val result = viewModel.getMovieDetails(movieId, movieLanguage).first()
        assertEquals(result.data, flowMovieDetail.first().data)
    }

    @Test
    fun getUpComingMovies() = runBlockingTest {
        val mockFlow = flow {
            emit(Resource.success(listOf(TMDBMovies.Results(id = 1, backdrop_path = "Test Movie"))))
        }
        coEvery { startUpRepository.getUpComingMovies() } returns mockFlow

        val result = viewModel.getUpComingMovies().first()

        assertEquals(result.data, mockFlow.first().data)
    }
}
