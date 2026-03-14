package com.tambal_ban.ui.add

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.data.repository.WorkshopRepository
import com.tambal_ban.databinding.ActivityAddWorkshopBinding
import com.tambal_ban.utils.Constants
import java.util.UUID
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class AddWorkshopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkshopBinding
    private lateinit var repository: WorkshopRepository
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as TambalBanApp).workshopRepository

        setupToolbar()
        setupMap()
        setupListeners()
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

    private fun submitWorkshop() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val point = selectedPoint ?: return

        val workshop =
                Workshop(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        latitude = point.latitude,
                        longitude = point.longitude,
                        phone = if (phone.isEmpty()) null else phone,
                        address = if (address.isEmpty()) null else address,
                        openTime = null,
                        closeTime = null,
                        ratingAvg = 0.0,
                        ratingCount = 0,
                        source = "user_contribution"
                )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                val success = repository.submitWorkshop(workshop)
                if (success) {
                    Toast.makeText(
                                    this@AddWorkshopActivity,
                                    "Workshop submitted successfully!",
                                    Toast.LENGTH_LONG
                            )
                            .show()
                    finish()
                } else {
                    Toast.makeText(
                                    this@AddWorkshopActivity,
                                    "Submission failed",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddWorkshopActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
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
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
