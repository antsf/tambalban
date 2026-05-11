package com.tambal_ban.workshop.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.tambal_ban.core.ui.BaseActivity
import com.tambal_ban.databinding.ActivityWorkshopListBinding
import com.tambal_ban.workshop.viewmodel.WorkshopListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WorkshopListActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkshopListBinding
    private val viewModel: WorkshopListViewModel by viewModels()
    private lateinit var adapter: WorkshopListAdapter
    private lateinit var adMobManager: com.tambal_ban.core.ads.AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        adMobManager = (application as com.tambal_ban.TambalBanApp).adMobManager

        setupToolbar()
        setupSearch()
        setupRecyclerView()
        setupObservers()

        // Default: fetch all workshops
        viewModel.fetchAllWorkshops()

        // Load banner ad
        adMobManager.loadBannerAd(binding.adContainer)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                viewModel.searchWorkshops(query)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            private var searchJob: kotlinx.coroutines.Job? = null
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    viewModel.searchWorkshops(s?.toString() ?: "")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = WorkshopListAdapter(
            onWorkshopClick = { workshop ->
                val intent = Intent(this, WorkshopDetailActivity::class.java).apply {
                    putExtra(com.tambal_ban.core.utils.Constants.EXTRA_WORKSHOP_ID, workshop.id)
                }
                startActivity(intent)
            },
            adMobManager = adMobManager
        )
        binding.rvWorkshops.adapter = adapter

        binding.rvWorkshops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    viewModel.loadMore()
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops ->
            if (workshops.isEmpty() && viewModel.isLoading.value == false) {
                binding.rvWorkshops.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.rvWorkshops.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                adapter.submitList(workshops)
                val adSlotCount = workshops.size / 5
                for (slotIndex in 0 until adSlotCount) {
                    adMobManager.loadNativeAd { nativeAd ->
                        adapter.updateNativeAd(slotIndex, nativeAd)
                    }
                }
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

        viewModel.isMoreLoading.observe(this) { isMoreLoading ->
            // Optionally show a small loading indicator at the bottom
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adMobManager.resumeBannerAd()
    }

    override fun onPause() {
        super.onPause()
        adMobManager.pauseBannerAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.destroyNativeAds()
        adMobManager.destroyBannerAd()
    }
}
