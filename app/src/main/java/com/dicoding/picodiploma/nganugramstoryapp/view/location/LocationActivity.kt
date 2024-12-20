package com.dicoding.picodiploma.nganugramstoryapp.view.location

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.nganugramstoryapp.R
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityLocationBinding
import com.dicoding.picodiploma.nganugramstoryapp.view.ViewModelFactory
import com.dicoding.picodiploma.nganugramstoryapp.view.main.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        private const val TAG = "LocationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        observeMapsData()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeMapsData() {
        lifecycleScope.launch {
            viewModel.getStoriesWithLocation()
        }

        viewModel.resultMaps.observe(this) { listStories ->
            if (listStories.isNullOrEmpty()) {
                Toast.makeText(this, "Tidak ada data lokasi.", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Tidak ada data lokasi untuk ditampilkan.")
                resetMapCamera()
            } else {
                addMarkersToMap(listStories)
                focusFirstLocation(listStories)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: $it")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        setupMapControls()
        getMyLocation()

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_maps)
            )
            if (!success) {
                Log.e(TAG, "Gagal memuat gaya peta.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Gaya peta tidak ditemukan. Error: ", exception)
        }
    }

    private fun setupMapControls() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun addMarkersToMap(listStories: List<ListStoryItem>) {
        listStories.forEach { story ->
            Log.d("LocationActivity", "Adding marker: ${story.name} at ${story.lat}, ${story.lon}")
            val latLng = LatLng(
                story.lat ?: 0.0,
                story.lon ?: 0.0
            )
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(story.name)
                .snippet(story.description)

            mMap.addMarker(markerOptions)
        }
    }

    private fun focusFirstLocation(listStories: List<ListStoryItem>) {
        val firstStory = listStories.first()
        val firstLatLng = LatLng(
            firstStory.lat ?: 0.0,
            firstStory.lon ?: 0.0
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f))
    }

    private fun resetMapCamera() {
        val defaultLatLng = LatLng(-2.5489, 118.0149) // Indonesia (Posisi default)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 4f))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
