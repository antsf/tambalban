package com.tambal_ban.workshop.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.databinding.ActivityWorkshopListBinding
import com.tambal_ban.workshop.viewmodel.WorkshopListViewModel

class WorkshopListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkshopListBinding
    private val viewModel: WorkshopListViewModel by viewModels()
    private lateinit var adapter: WorkshopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        // Default: fetch workshops for current location (mocked or from intent)
        val lat = intent.getDoubleExtra("LAT", -6.2000)
        val lon = intent.getDoubleExtra("LON", 106.8166)
        viewModel.fetchNearbyWorkshops(lat, lon, 5000)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = WorkshopListAdapter { workshop ->
            val intent = Intent(this, WorkshopDetailActivity::class.java).apply {
                putExtra("WORKSHOP_ID", workshop.id)
            }
            startActivity(intent)
        }
        binding.rvWorkshops.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops ->
            if (workshops.isEmpty()) {
                binding.rvWorkshops.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.rvWorkshops.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                adapter.submitList(workshops)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
                binding.rvWorkshops.visibility = View.GONE
                binding.emptyState.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
