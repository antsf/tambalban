package com.tambal_ban.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.R
import com.tambal_ban.databinding.ActivityAllWorkshopsBinding
import com.tambal_ban.ui.add.AddWorkshopActivity
import com.tambal_ban.ui.detail.WorkshopDetailActivity
import com.tambal_ban.utils.Constants

class AllWorkshopsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllWorkshopsBinding
    private val viewModel: AllWorkshopsViewModel by viewModels()
    private lateinit var adapter: AllWorkshopsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllWorkshopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = AllWorkshopsAdapter { workshop ->
            val intent =
                    Intent(this, WorkshopDetailActivity::class.java).apply {
                        putExtra(Constants.EXTRA_WORKSHOP_ID, workshop.id)
                    }
            startActivity(intent)
        }
        binding.rvWorkshops.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddWorkshopActivity::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops ->
            adapter.submitList(workshops)
            binding.tvEmpty.visibility = if (workshops.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility =
                    if (isLoading && adapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_name -> {
                viewModel.sortWorkshops("name")
                true
            }
            R.id.sort_distance -> {
                viewModel.sortWorkshops("distance")
                true
            }
            R.id.sort_rating -> {
                viewModel.sortWorkshops("rating")
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
