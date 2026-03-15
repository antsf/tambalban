package com.tambal_ban.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.Review
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.data.repository.ReviewRepository
import com.tambal_ban.data.repository.WorkshopRepository
import kotlinx.coroutines.launch

class WorkshopDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val workshopRepository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val reviewRepository: ReviewRepository = (application as TambalBanApp).reviewRepository
    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _workshop = MutableLiveData<Workshop?>()
    val workshop: LiveData<Workshop?> = _workshop

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _reviewSubmissionResult = MutableLiveData<Result<Unit>>()
    val reviewSubmissionResult: LiveData<Result<Unit>> = _reviewSubmissionResult

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun loadWorkshop(workshopId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workshopResult = workshopRepository.getWorkshopById(workshopId)
                _workshop.value = workshopResult

                val reviewsResult = reviewRepository.getReviews(workshopId)
                if (reviewsResult.isSuccess) {
                    _reviews.value = reviewsResult.getOrDefault(emptyList())
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitReview(workshopId: String, rating: Int, comment: String) {
        if (!authRepository.isLoggedIn()) {
            _error.value = "You must be logged in to submit a review"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.getUserId()
            val review = Review(
                workshopId = workshopId,
                userId = userId,
                rating = rating,
                comment = comment
            )
            val result = reviewRepository.submitReview(review)
            _reviewSubmissionResult.value = result
            if (result.isSuccess) {
                loadWorkshop(workshopId) // Refresh reviews
            }
            _isLoading.value = false
        }
    }

    fun checkAuth() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }
}
