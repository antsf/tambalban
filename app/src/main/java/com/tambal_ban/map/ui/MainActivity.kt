package com.tambal_ban.map.ui
import com.tambal_ban.map.ui.* 
import com.tambal_ban.map.viewmodel.* 

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tambal_ban.R
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.databinding.ActivityMainBinding
import com.tambal_ban.core.utils.Constants
import com.tambal_ban.core.utils.MapUtils
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * T009: MainActivity with unified state management (Loading, Empty, Error, Content).
 */
class MainActivity : com.tambal_ban.core.ui.BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private lateinit var workshopAdapter: NearbyWorkshopAdapter
    private lateinit var suggestionAdapter: SearchSuggestionAdapter
    private var searchFocusMarker: Marker? = null
    private var hasCenteredOnUser = false

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk mencari tambal ban terdekat", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid
        org.osmdroid.config.Configuration.getInstance().load(
            this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        setupMap()
        setupBottomSheet()
        setupSearch()
        setupObservers()
        checkLocationPermission()
        
        // Setup Retry Button
        binding.viewErrorState.btnRetry.setOnClickListener {
            val query = binding.searchOverlay.etSearch.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchWorkshops(query)
            } else {
                viewModel.userLocation.value?.let {
                    viewModel.fetchNearbyWorkshops(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchOverlay.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s?.toString() ?: ""
                viewModel.onSearchTextChanged(query)
                if (query.isEmpty()) {
                    binding.searchOverlay.suggestionsCard.visibility = View.GONE
                    searchFocusMarker?.let { 
                        binding.mapView.overlays.remove(it)
                        searchFocusMarker = null
                        binding.mapView.invalidate()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.searchOverlay.ivUserAvatar.setOnClickListener {
            val intent = if ((application as com.tambal_ban.TambalBanApp).authRepository.isLoggedIn()) {
                android.content.Intent(this, com.tambal_ban.auth.ui.ProfileActivity::class.java)
            } else {
                android.content.Intent(this, com.tambal_ban.auth.ui.LoginActivity::class.java)
            }
            startActivity(intent)
        }

        binding.searchOverlay.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                
                binding.searchOverlay.etSearch.clearFocus()
                binding.searchOverlay.suggestionsCard.visibility = View.GONE
                viewModel.searchWorkshops(v.text.toString())
                true
            } else {
                false
            }
        }

        suggestionAdapter = SearchSuggestionAdapter { workshop ->
            binding.searchOverlay.etSearch.setText(workshop.name)
            binding.searchOverlay.etSearch.clearFocus()
            binding.searchOverlay.suggestionsCard.visibility = View.GONE
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchOverlay.etSearch.windowToken, 0)
            
            binding.mapView.controller.animateTo(GeoPoint(workshop.latitude, workshop.longitude))
            addSearchFocusMarker(workshop)
        }

        binding.searchOverlay.rvSuggestions.adapter = suggestionAdapter
    }

    private fun setupBottomSheet() {
        val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(binding.bottomSheet)
        behavior.peekHeight = (resources.displayMetrics.density * 280).toInt()
        behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED

        workshopAdapter = NearbyWorkshopAdapter { workshop ->
            binding.mapView.controller.animateTo(GeoPoint(workshop.latitude, workshop.longitude))
            addSearchFocusMarker(workshop)
        }

        binding.rvWorkshopsNearby.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
            adapter = workshopAdapter
        }

        binding.btnViewAll.setOnClickListener {
            val intent = android.content.Intent(this, com.tambal_ban.workshop.ui.WorkshopListActivity::class.java).apply {
                val location = viewModel.userLocation.value
                putExtra("LAT", location?.latitude ?: com.tambal_ban.core.utils.Constants.DEFAULT_LATITUDE)
                putExtra("LON", location?.longitude ?: com.tambal_ban.core.utils.Constants.DEFAULT_LONGITUDE)
            }
            startActivity(intent)
        }
    }

    private fun setupMap() {
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            overlayManager.tilesOverlay.setColorFilter(MapUtils.getColorFilter())
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))
        }

        binding.fabMyLocation.setOnClickListener {
            myLocationOverlay?.myLocation?.let { point ->
                binding.mapView.controller.setCenter(point)
                binding.mapView.controller.setZoom(Constants.DEFAULT_ZOOM)
            }
        }
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops ->
            updateMarkers(workshops)
            workshopAdapter.submitList(workshops)
            updateUIStates()
        }

        viewModel.searchSuggestions.observe(this) { suggestions ->
            if (suggestions.isNotEmpty() && binding.searchOverlay.etSearch.hasFocus()) {
                suggestionAdapter.submitList(suggestions)
                binding.searchOverlay.suggestionsCard.visibility = View.VISIBLE
            } else {
                binding.searchOverlay.suggestionsCard.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) { updateUIStates() }
        viewModel.error.observe(this) { updateUIStates() }

        viewModel.getLocationLiveData().observe(this) { location ->
            viewModel.onLocationUpdated(location)
            if (location != null) {
                binding.fabMyLocation.visibility = View.VISIBLE
                if (!hasCenteredOnUser) {
                    binding.mapView.controller.setCenter(GeoPoint(location.latitude, location.longitude))
                    binding.mapView.controller.setZoom(Constants.DEFAULT_ZOOM)
                    hasCenteredOnUser = true
                }
            }
        }

        viewModel.sheetTitle.observe(this) { title ->
            binding.tvSheetTitle.text = title
        }
    }

    private fun updateUIStates() {
        val isLoading = viewModel.isLoading.value ?: false
        val error = viewModel.error.value
        val workshops = viewModel.workshops.value ?: emptyList()
        val isSearching = binding.searchOverlay.etSearch.text.isNotEmpty()

        // 1. Loading State
        if (isLoading) {
            binding.shimmerView.startShimmer()
            binding.shimmerView.visibility = View.VISIBLE
            binding.rvWorkshopsNearby.visibility = View.GONE
            binding.viewEmptyState.root.visibility = View.GONE
            binding.viewErrorState.root.visibility = View.GONE
            return
        }
        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE

        // 2. Error State
        if (error != null) {
            binding.rvWorkshopsNearby.visibility = View.GONE
            binding.viewEmptyState.root.visibility = View.GONE
            binding.viewErrorState.root.visibility = View.VISIBLE
            return
        }
        binding.viewErrorState.root.visibility = View.GONE

        // 3. Empty State
        if (workshops.isEmpty()) {
            binding.rvWorkshopsNearby.visibility = View.GONE
            binding.viewEmptyState.root.visibility = View.VISIBLE
        } else {
            binding.rvWorkshopsNearby.visibility = View.VISIBLE
            binding.viewEmptyState.root.visibility = View.GONE
        }

        // 4. Global Sheet Visibility
        val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(binding.bottomSheet)
        if (workshops.isEmpty() && !isSearching && !isLoading) {
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
        } else if (behavior.state == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN) {
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun checkLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(permissions)
        }
    }

    private fun enableMyLocation() {
        if (myLocationOverlay != null) return
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.mapView).apply { enableMyLocation() }
        binding.mapView.overlays.add(myLocationOverlay)
        viewModel.startLocationUpdates()
    }

    private fun addSearchFocusMarker(workshop: Workshop) {
        searchFocusMarker?.let { binding.mapView.overlays.remove(it) }
        searchFocusMarker = Marker(binding.mapView).apply {
            position = GeoPoint(workshop.latitude, workshop.longitude)
            icon = createLabeledMarker(workshop.name)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            setOnMarkerClickListener { _, _ ->
                binding.mapView.controller.animateTo(position)
                val intent = android.content.Intent(this@MainActivity, com.tambal_ban.workshop.ui.WorkshopDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_WORKSHOP_ID, workshop.id)
                }
                startActivity(intent)
                true
            }
        }
        binding.mapView.overlays.add(searchFocusMarker)
        binding.mapView.invalidate()
    }

    private fun updateMarkers(workshops: List<Workshop>) {
        binding.mapView.overlays.filterIsInstance<Marker>().forEach { 
            if (it != searchFocusMarker) binding.mapView.overlays.remove(it)
        }
        workshops.forEach { workshop ->
            if (workshop.id == searchFocusMarker?.title) return@forEach
            Marker(binding.mapView).apply {
                position = GeoPoint(workshop.latitude, workshop.longitude)
                icon = createLabeledMarker(workshop.name)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                setOnMarkerClickListener { _, _ ->
                    binding.mapView.controller.animateTo(position)
                    val index = workshopAdapter.currentList.indexOfFirst { it.id == workshop.id }
                    if (index != -1) binding.rvWorkshopsNearby.smoothScrollToPosition(index)
                    
                    val intent = android.content.Intent(this@MainActivity, com.tambal_ban.workshop.ui.WorkshopDetailActivity::class.java).apply {
                        putExtra(Constants.EXTRA_WORKSHOP_ID, workshop.id)
                    }
                    startActivity(intent)
                    true
                }
                binding.mapView.overlays.add(this)
            }
        }
        binding.mapView.invalidate()
    }

    private fun createLabeledMarker(name: String): BitmapDrawable {
        val view = LayoutInflater.from(this).inflate(R.layout.view_marker_labeled, null)
        view.findViewById<TextView>(R.id.tvMarkerName).text = name
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); viewModel.stopLocationUpdates() }
}
