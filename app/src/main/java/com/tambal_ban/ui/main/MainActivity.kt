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
import androidx.core.view.GravityCompat
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.ads.AdMobManager
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.databinding.ActivityMainBinding
import com.tambal_ban.ui.auth.LoginActivity
import com.tambal_ban.ui.auth.RegisterActivity
import com.tambal_ban.ui.detail.WorkshopDetailActivity
import com.tambal_ban.ui.list.AllWorkshopsActivity
import com.tambal_ban.ui.profile.ProfileActivity
import com.tambal_ban.utils.Constants
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

        // T034: Use Material 3 Color for Status Bar (already handled by theme, but good for dynamic
        // changes)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        adMobManager = (application as TambalBanApp).adMobManager

        setupDrawer()
        setupMap()
        setupButtons()
        setupObservers()
        checkLocationPermission()
    }

    private fun setupDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already here
                }
                R.id.nav_all_workshops -> {
                    startActivity(Intent(this, AllWorkshopsActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawers()
            true
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

    private fun setupButtons() {
        // Profile icon button - opens navigation drawer
        binding.btnProfile.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnMyLocation.setOnClickListener { centerOnMyLocation() }

        // T036: Search logic will be triggered by etSearch keyboard actions or text changes
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchByName(query)
            }
            true
        }
    }

    private fun setupObservers() {
        viewModel.workshops.observe(this) { workshops -> updateMapMarkers(workshops) }

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
