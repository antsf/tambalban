package com.tambal_ban.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.data.repository.WorkshopRepository
import com.tambal_ban.databinding.ActivityWorkshopDetailBinding
import com.tambal_ban.utils.Constants
import com.tambal_ban.utils.IntentUtils
import kotlinx.coroutines.launch

class WorkshopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkshopDetailBinding
    private lateinit var repository: WorkshopRepository
    private var workshopId: String? = null
    private var currentWorkshop: Workshop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as TambalBanApp).workshopRepository
        workshopId = intent.getStringExtra(Constants.EXTRA_WORKSHOP_ID)

        setupToolbar()
        setupListeners()
        loadWorkshopDetails()
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

    private fun loadWorkshopDetails() {
        val id = workshopId ?: return

        lifecycleScope.launch {
            try {
                val workshop = repository.getWorkshopById(id)
                if (workshop != null) {
                    bindWorkshopData(workshop)
                } else {
                    Toast.makeText(
                                    this@WorkshopDetailActivity,
                                    "Workshop not found",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                                this@WorkshopDetailActivity,
                                "Error loading details",
                                Toast.LENGTH_SHORT
                        )
                        .show()
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
