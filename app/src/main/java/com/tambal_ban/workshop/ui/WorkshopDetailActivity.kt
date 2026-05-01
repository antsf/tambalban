package com.tambal_ban.workshop.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tambal_ban.R
import com.tambal_ban.core.ui.BaseActivity
import com.tambal_ban.core.utils.Constants
import com.tambal_ban.core.utils.IntentUtils
import com.tambal_ban.databinding.ActivityWorkshopDetailBinding
import com.tambal_ban.workshop.data.WorkshopDetailUIState
import com.tambal_ban.workshop.viewmodel.WorkshopDetailViewModel

class WorkshopDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkshopDetailBinding
    private val viewModel: WorkshopDetailViewModel by viewModels()
    private var workshopId: String? = null
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workshopId = intent.getStringExtra(Constants.EXTRA_WORKSHOP_ID)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        setupObservers()

        workshopId?.let { viewModel.loadWorkshop(it) }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupListeners() {
        binding.btnCall.setOnClickListener {
            viewModel.uiState.value?.phoneNumber?.let { phone -> 
                IntentUtils.dialPhoneNumber(this, phone) 
            } ?: run {
                Toast.makeText(this, "Nomor telepon tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNavigate.setOnClickListener {
            viewModel.workshop.value?.let { workshop ->
                IntentUtils.openNavigation(
                    this,
                    workshop.latitude,
                    workshop.longitude,
                    workshop.name
                )
            }
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(this@WorkshopDetailActivity)
            adapter = reviewAdapter
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { uiState ->
            bindUIState(uiState)
        }

        viewModel.reviews.observe(this) { reviews ->
            reviewAdapter.submitList(reviews)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.reviewSubmissionResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Ulasan terkirim!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal mengirim ulasan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindUIState(uiState: WorkshopDetailUIState) {
        binding.apply {
            collapsingToolbar.title = getString(R.string.workshop_detail)
            tvWorkshopName.text = uiState.name
            tvFullAddress.text = uiState.fullAddress
            tvPhoneNumber.text = uiState.phoneNumber
            tvBusinessHours.text = uiState.businessHours
            tvRatingText.text = "${uiState.ratingAvg} ${uiState.reviewCountText}"
            ratingBar.rating = uiState.ratingAvg.toFloatOrNull() ?: 0f
            
            tvStatusBadge.text = uiState.statusText.ifEmpty { "BUKA SEKARANG" }
            val colorStateList = androidx.core.content.ContextCompat.getColorStateList(this@WorkshopDetailActivity, uiState.statusColorRes)
            tvStatusBadge.backgroundTintList = colorStateList
            tvStatusBadge.visibility = android.view.View.VISIBLE

            ivWorkshopImage.load(uiState.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder_workshop)
                error(R.drawable.bg_placeholder_workshop)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
