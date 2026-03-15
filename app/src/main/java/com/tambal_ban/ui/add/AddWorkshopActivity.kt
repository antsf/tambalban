package com.tambal_ban.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.databinding.ActivityAddWorkshopBinding
import com.tambal_ban.ui.auth.LoginActivity
import com.tambal_ban.utils.Constants
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class AddWorkshopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkshopBinding
    private val viewModel: AddWorkshopViewModel by viewModels()
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMap()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Workshop"
    }

    private fun setupMap() {
        binding.mapView.apply {
            setMultiTouchControls(true)
            controller.setZoom(Constants.DEFAULT_ZOOM)
            controller.setCenter(GeoPoint(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))
        }

        val eventsOverlay =
                MapEventsOverlay(
                        object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let { updateSelectedLocation(it) }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean = false
                        }
                )
        binding.mapView.overlays.add(eventsOverlay)
    }

    private fun updateSelectedLocation(point: GeoPoint) {
        selectedPoint = point
        binding.tvSelectedLocation.apply {
            visibility = View.VISIBLE
            text = buildString {
        append("Lat: ")
        append("%.4f".format(point.latitude))
        append(", Lng: ")
        append("%.4f".format(point.longitude))
    }
        }

        if (marker == null) {
            marker =
                    Marker(binding.mapView).apply {
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
            binding.mapView.overlays.add(marker)
        }
        marker?.position = point
        binding.mapView.invalidate()
    }

    private fun setupListeners() {
        binding.btnUseCurrentLocation.setOnClickListener {
            // In a real app we'd fetch current location, for now center on default
            updateSelectedLocation(
                    GeoPoint(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE)
            )
        }

        binding.btnCancel.setOnClickListener { finish() }

        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                submitWorkshop()
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            return false
        }
        binding.tilName.error = null

        if (selectedPoint == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun setupObservers() {
        viewModel.submissionResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Workshop submitted successfully!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !isLoading
        }

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                Toast.makeText(this, "Please log in to submit a workshop", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun submitWorkshop() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val point = selectedPoint ?: return

        viewModel.submitWorkshop(name, address, point.latitude, point.longitude, phone)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        viewModel.checkAuth()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
