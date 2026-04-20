package com.tambal_ban.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.tambal_ban.R
import com.tambal_ban.databinding.ActivityProfileBinding
import com.tambal_ban.viewmodel.ProfileViewModel
import androidx.core.net.toUri

/**
 * US2: Profile Activity displaying user information.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
        
        // Pre-fill email from local storage immediately
        viewModel.userEmail?.let {
            binding.tvProfileEmail.text = it
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data whenever returning to this screen
        viewModel.getProfile()
    }

    private fun setupObservers() {
        viewModel.profile.observe(this) { profile ->
            if (profile != null) {
                binding.tvProfileName.text = if (profile.fullName.isNullOrEmpty()) "-" else profile.fullName
                binding.tvProfileEmail.text = if (profile.email.isNullOrEmpty()) "-" else profile.email
                
                val formattedPhone = formatPhoneNumber(profile.phone)
                binding.chipPhone.text = if (formattedPhone.isNullOrEmpty()) "-" else formattedPhone
                
                if (!profile.avatarUrl.isNullOrEmpty()) {
                    binding.avatarView.loadAvatar(profile.avatarUrl)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // In a real app, show a progress bar
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoggedOut.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnRateUs.setOnClickListener {
            openPlayStore()
        }

        binding.btnShareApp.setOnClickListener {
            shareApp()
        }

        binding.avatarView.setOnEditClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun openPlayStore() {
        val appId = "com.tambal_ban"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId"))
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$appId".toUri()))
//        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tambal Ban")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Temukan tambal ban terdekat dengan mudah menggunakan Tambal Ban: https://play.google.com/store/apps/details?id=com.tambal_ban")
        startActivity(Intent.createChooser(shareIntent, "Bagikan melalui"))
    }

    private fun formatPhoneNumber(phone: String?): String {
        if (phone == null) return ""
        return when {
            phone.startsWith("+62") -> "0" + phone.substring(3)
            phone.startsWith("62") -> "0" + phone.substring(2)
            else -> phone
        }
    }
}
