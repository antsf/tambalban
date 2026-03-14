package com.tambal_ban.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.ads.AdMobManager
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.databinding.ActivityMainBinding
import com.tambal_ban.ui.add.AddWorkshopActivity
import com.tambal_ban.ui.detail.WorkshopDetailActivity
import com.tambal_ban.utils.Constants
import com.tambal_ban.utils.GeoUtils
import com.tambal_ban.utils.IntentUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var adMobManager: AdMobManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var workshopAdapter: WorkshopAdapter

    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var markerClusterer: RadiusMarkerClusterer? = null

    private val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    permissions ->
                when {
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                        enableMyLocation()
                    }
                    else -> {
                        Toast.makeText(
                                        this,
                                        R.string.location_permission_denied,
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                    }
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets for safe areas
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) {
                v: View,
                windowInsets: WindowInsetsCompat ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                    v.paddingStart,
                    insets.top,
                    v.paddingEnd,
                    insets.bottom
            )
            windowInsets
        }

        adMobManager = (application as TambalBanApp).adMobManager

        setupMap()
        setupBottomSheet()
        setupRecyclerView()
        setupButtons()
        setupObservers()
        checkLocationPermission()

        // T031: Delayed Ad Loading (2 seconds delay to prioritize map rendering)
        lifecycleScope.launch {
            delay(2000)
            adMobManager.loadBannerAd(binding.adContainer)
        }
    }

    private fun setupMap() {
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            controller.setZoom(Constants.DEFAULT_ZOOM)
            controller.setCenter(GeoPoint(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))

            addMapListener(
                    DelayedMapListener(
                            object : MapListener {
                                override fun onScroll(event: ScrollEvent?): Boolean {
                                    loadWorkshopsInViewport()
                                    return true
                                }

                                override fun onZoom(event: ZoomEvent?): Boolean {
                                    loadWorkshopsInViewport()
                                    return true
                                }
                            },
                            500
                    )
            )
        }

        markerClusterer = RadiusMarkerClusterer(this).apply { setIcon(getClusterIcon()) }
        binding.mapView.overlays.add(markerClusterer)
    }

    private fun getClusterIcon(): Bitmap? {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint =
                android.graphics.Paint().apply {
                    color = ContextCompat.getColor(this@MainActivity, R.color.primary)
                    style = android.graphics.Paint.Style.FILL
                    isAntiAlias = true
                }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        return bitmap
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            binding.emergencyFab.show()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (slideOffset > 0) {
                            binding.emergencyFab.hide()
                        } else {
                            binding.emergencyFab.show()
                        }
                    }
                }
        )
    }

    private fun setupRecyclerView() {
        workshopAdapter = WorkshopAdapter { workshop -> openWorkshopDetail(workshop) }
        binding.rvWorkshops.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = workshopAdapter
        }
    }

    private fun setupButtons() {
        binding.emergencyFab.setOnClickListener { viewModel.activateEmergencyMode() }

        binding.btnMyLocation.setOnClickListener { centerOnMyLocation() }

        binding.btnFindNearest.setOnClickListener {
            viewModel.findNearestWorkshops(viewModel.searchRadius.value ?: Constants.RADIUS_3KM)
        }

        binding.fabAddWorkshop.setOnClickListener {
            startActivity(Intent(this, AddWorkshopActivity::class.java))
        }

        binding.chipGroupRadius.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val radius =
                        when (checkedIds.first()) {
                            R.id.chip1km -> Constants.RADIUS_1KM
                            R.id.chip3km -> Constants.RADIUS_3KM
                            R.id.chip5km -> Constants.RADIUS_5KM
                            else -> Constants.RADIUS_3KM
                        }
                viewModel.setSearchRadius(radius)
                loadWorkshopsInViewport()
            }
        }

        binding.btnEmergencyCall.setOnClickListener {
            viewModel.emergencyWorkshop.value?.phone?.let { phone ->
                IntentUtils.dialPhoneNumber(this, phone)
            }
        }

        binding.btnEmergencyNavigate.setOnClickListener {
            viewModel.emergencyWorkshop.value?.let { workshop ->
                IntentUtils.openNavigation(
                        this,
                        workshop.latitude,
                        workshop.longitude,
                        workshop.name
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops ->
            updateMapMarkers(workshops)
            workshopAdapter.submitList(workshops)
        }

        viewModel.nearestWorkshops.observe(this) { workshops ->
            if (workshops.isNotEmpty()) {
                showEmergencyMode(workshops.first())
            }
        }

        viewModel.userLocation.observe(this) { location ->
            location?.let {
                if (myLocationOverlay == null) {
                    enableMyLocation()
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.isEmergencyMode.observe(this) { isEmergency ->
            if (!isEmergency) {
                hideEmergencyMode()
            }
        }

        viewModel.getLocationLiveData().observe(this) { location ->
            viewModel.onLocationUpdated(location)
        }
    }

    private fun checkLocationPermission() {
        val permissions =
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
        if (permissions.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }
        ) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(permissions)
        }
    }

    private fun enableMyLocation() {
        if (myLocationOverlay != null) return

        myLocationOverlay =
                MyLocationNewOverlay(GpsMyLocationProvider(this), binding.mapView).apply {
                    enableMyLocation()
                    enableFollowLocation()
                }
        binding.mapView.overlays.add(myLocationOverlay)

        viewModel.startLocationUpdates()
    }

    private fun centerOnMyLocation() {
        val location = viewModel.userLocation.value
        location?.let { binding.mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude)) }
    }

    private fun loadWorkshopsInViewport() {
        val boundingBox = binding.mapView.projection.boundingBox
        viewModel.loadWorkshopsByBounds(
                minLat = boundingBox.latSouth,
                maxLat = boundingBox.latNorth,
                minLng = boundingBox.lonWest,
                maxLng = boundingBox.lonEast
        )
    }

    private fun updateMapMarkers(workshops: List<Workshop>) {
        markerClusterer?.items?.clear()
        workshops.forEach { workshop ->
            val marker =
                    Marker(binding.mapView).apply {
                        position = GeoPoint(workshop.latitude, workshop.longitude)
                        title = workshop.name
                        snippet = workshop.address
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            openWorkshopDetail(workshop)
                            true
                        }
                    }
            markerClusterer?.add(marker)
        }
        binding.mapView.invalidate()
    }

    private fun showEmergencyMode(workshop: Workshop) {
        viewModel.setEmergencyWorkshop(workshop)
        binding.cardEmergency.apply {
            visibility = View.VISIBLE
            binding.tvEmergencyName.text = workshop.name
            binding.tvEmergencyDistance.text =
                    workshop.distance?.let { GeoUtils.formatDistance(it) } ?: ""
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.emergencyFab.hide()
    }

    private fun hideEmergencyMode() {
        binding.cardEmergency.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.emergencyFab.show()
    }

    private fun openWorkshopDetail(workshop: Workshop) {
        val intent =
                Intent(this, WorkshopDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_WORKSHOP_ID, workshop.id)
                }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        adMobManager.resumeBannerAd()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        adMobManager.pauseBannerAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLocationUpdates()
        adMobManager.destroyBannerAd()
    }
}
