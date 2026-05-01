package com.tambal_ban.workshop.ui
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tambal_ban.R
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.databinding.ActivityWorkshopDetailBinding
import com.tambal_ban.core.utils.Constants
import com.tambal_ban.core.utils.IntentUtils

class WorkshopDetailActivity : com.tambal_ban.core.ui.BaseActivity() {

    private lateinit var binding: ActivityWorkshopDetailBinding
    private val viewModel: WorkshopDetailViewModel by viewModels()
    private var workshopId: String? = null
    private var currentWorkshop: Workshop? = null
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

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
            currentWorkshop?.phone?.let { phone -> IntentUtils.dialPhoneNumber(this, phone) }
                    ?: run {
                        Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show()
                    }
        }

        binding.btnNavigate.setOnClickListener {
            currentWorkshop?.let { workshop ->
                IntentUtils.openNavigation(
                        this,
                        workshop.latitude,
                        workshop.longitude,
                        workshop.name
                )
            }
        }

        binding.btnReport.setOnClickListener {
            Toast.makeText(this, "Report feature coming soon", Toast.LENGTH_SHORT).show()
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
        viewModel.workshop.observe(this) { workshop ->
            if (workshop != null) {
                bindWorkshopData(workshop)
            }
        }

        viewModel.reviews.observe(this) { reviews ->
            reviewAdapter.submitList(reviews)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide progress bar if needed
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.reviewSubmissionResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindWorkshopData(workshop: Workshop) {
        currentWorkshop = workshop
        binding.apply {
            tvWorkshopName.text = workshop.name
            tvAddress.text = workshop.address ?: "No address available"
            tvPhone.text = workshop.phone ?: "No phone number"
            tvRating.text = String.format("%.1f", workshop.ratingAvg)
            tvRatingCount.text = "(${workshop.ratingCount} reviews)"

            if (workshop.is24h) {
                tvOpenHours.text = getString(R.string.open_24h)
            } else if (!workshop.openTime.isNullOrEmpty()) {
                tvOpenHours.text = getString(R.string.open_time, workshop.openTime)
            } else {
                tvOpenHours.text = "Hours not specified"
            }

            workshop.distance?.let { tvDistance.text = String.format("%.1f km", it) }
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
