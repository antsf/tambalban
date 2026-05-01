package com.tambal_ban.workshop.viewmodel
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.workshop.data.Review
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.workshop.data.ReviewRepository
import com.tambal_ban.workshop.data.WorkshopRepository
import kotlinx.coroutines.launch

class WorkshopDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val workshopRepository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val reviewRepository: ReviewRepository = (application as TambalBanApp).reviewRepository
    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _workshop = MutableLiveData<Workshop?>()
    val workshop: LiveData<Workshop?> = _workshop

    private val _uiState = MutableLiveData<WorkshopDetailUIState>()
    val uiState: LiveData<WorkshopDetailUIState> = _uiState

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
                workshopResult?.let { mapToUIState(it) }

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

    private fun mapToUIState(workshop: Workshop) {
        val context = getApplication<Application>()
        
        // Simple open/closed logic for now (can be expanded with actual business hours)
        val isOpen = workshop.is24h || true // Placeholder, but ensuring it's never empty
        val statusTextRes = if (isOpen) com.tambal_ban.R.string.status_open_now 
                           else com.tambal_ban.R.string.status_closed
        val statusText = context.getString(statusTextRes)
        val statusColor = if (isOpen) com.tambal_ban.R.color.success 
                         else com.tambal_ban.R.color.error

        val businessHours = if (workshop.is24h) {
            context.getString(com.tambal_ban.R.string.open_24h)
        } else {
            "${workshop.openTime ?: "08:00"} - ${workshop.closeTime ?: "17:00"}"
        }

        _uiState.value = WorkshopDetailUIState(
            id = workshop.id,
            name = workshop.name,
            imageUrl = workshop.imageUrl,
            fullAddress = workshop.address ?: context.getString(com.tambal_ban.R.string.address),
            phoneNumber = workshop.phone ?: context.getString(com.tambal_ban.R.string.phone_number),
            businessHours = businessHours,
            ratingAvg = workshop.ratingAvg.toString(),
            reviewCountText = "(${workshop.ratingCount} ${context.getString(com.tambal_ban.R.string.reviews)})",
            statusText = statusText,
            statusColorRes = statusColor,
            is24h = workshop.is24h
        )
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
