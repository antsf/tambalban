package com.tambal_ban.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputEditText
import com.tambal_ban.R
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.databinding.ActivityWorkshopDetailBinding
import com.tambal_ban.utils.Constants
import com.tambal_ban.utils.GeoUtils
import com.tambal_ban.utils.IntentUtils

class WorkshopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkshopDetailBinding
    private val viewModel: WorkshopDetailViewModel by viewModels()
    private var workshopId: String? = null
    private var currentWorkshop: Workshop? = null
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

        binding.btnAddReview.setOnClickListener { showAddReviewDialog() }
    }

    private fun showAddReviewDialog() {
        if (viewModel.isLoggedIn.value != true) {
            Toast.makeText(this, getString(R.string.login_required_review), Toast.LENGTH_SHORT)
                    .show()
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.dialogRatingBar)
        val etComment = dialogView.findViewById<TextInputEditText>(R.id.etComment)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancel)
        val btnSubmit = dialogView.findViewById<View>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val comment = etComment.text.toString().trim()

            if (rating > 0) {
                workshopId?.let { id ->
                    viewModel.submitReview(id, rating, comment)
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, getString(R.string.rating_required), Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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

        viewModel.reviews.observe(this) { reviews -> reviewAdapter.submitList(reviews) }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide progress bar if needed
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.reviewSubmissionResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, getString(R.string.review_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.review_failed), Toast.LENGTH_SHORT).show()
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
            tvRatingCount.text = getString(R.string.reviews, workshop.ratingCount.toString())

            if (workshop.is24h) {
                tvOpenHours.text = getString(R.string.open_24h)
            } else if (!workshop.openTime.isNullOrEmpty()) {
                tvOpenHours.text = getString(R.string.open_time, workshop.openTime)
            } else {
                tvOpenHours.text = "Jam operasional tidak tersedia"
            }

            workshop.distance?.let { tvDistance.text = GeoUtils.formatDistance(it) }

            // Photo Gallery
            val photoUrls = mutableListOf<String>()
            workshop.photoUrl?.let {
                if (it.isNotEmpty()) {
                    photoUrls.add(it)
                }
            }
            // Only show gallery if there are actual photos
            if (photoUrls.isEmpty()) {
                binding.vpPhotos.visibility = View.GONE
                binding.llDotIndicator.visibility = View.GONE
            } else {
                binding.vpPhotos.visibility = View.VISIBLE
                binding.vpPhotos.adapter = GalleryAdapter(photoUrls)
                setupDotIndicator(photoUrls.size)
                vpPhotos.registerOnPageChangeCallback(
                        object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                updateDots(position)
                            }
                        }
                )
            }
        }
    }

    private fun setupDotIndicator(size: Int) {
        binding.llDotIndicator.removeAllViews()
        if (size <= 1) return

        for (i in 0 until size) {
            val dot =
                    ImageView(this).apply {
                        setImageResource(R.drawable.dot_indicator)
                        val params =
                                LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        .apply { setMargins(8, 0, 8, 0) }
                        layoutParams = params
                    }
            binding.llDotIndicator.addView(dot)
        }
        updateDots(0)
    }

    private fun updateDots(position: Int) {
        val count = binding.llDotIndicator.childCount
        for (i in 0 until count) {
            binding.llDotIndicator.getChildAt(i).isSelected = (i == position)
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
