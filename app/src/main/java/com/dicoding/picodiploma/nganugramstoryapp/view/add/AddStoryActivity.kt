package com.dicoding.picodiploma.nganugramstoryapp.view.add

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.nganugramstoryapp.view.ViewModelFactory
import com.dicoding.picodiploma.nganugramstoryapp.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar // Menambahkan Snackbar untuk menampilkan error message
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()  // Mengatur klik listener untuk button
        observeViewModel() // Mengatur observer untuk ViewModel
    }

    // Mengatur listener untuk button
    private fun setupListeners() {
        binding.addGallery.setOnClickListener {
            chooseGallery() // Membuka galeri
        }
        binding.addCamera.setOnClickListener {
            chooseCamera()  // Membuka kamera
        }
        binding.buttonAdd.setOnClickListener {
            addStory() // Menambahkan story
            animateButton(binding.buttonAdd) // Menambahkan animasi ketika tombol Add diklik
        }
    }

    // Fungsi untuk menambahkan cerita (story)
    private fun addStory() {
        val description = binding.edAddDescription.text.toString().trim() // Mengambil deskripsi dari EditText
        if (description.isNotEmpty()) {
            currentImageUri?.let { uri ->
                val file = uriToFile(uri, this).reduceFileImage() // Mengubah URI ke File
                lifecycleScope.launch {
                    viewModel.addStory(file, description) // Kirim data ke ViewModel
                }
            } ?: showError("Please select an image first") // Validasi gambar belum dipilih
        } else {
            showError("Description cannot be empty") // Menampilkan error jika deskripsi kosong
            binding.edAddDescription.error = "Description cannot be empty" // Menampilkan error di EditText
        }
    }

    // Fungsi untuk membuka galeri
    private fun chooseGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // ActivityResultLauncher untuk galeri
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            currentImageUri = uri
            binding.addImage.setImageURI(uri) // Menampilkan gambar yang dipilih
        } else {
            showError("No image selected") // Error jika tidak ada gambar
        }
    }

    // Fungsi untuk membuka kamera
    private fun chooseCamera() {
        currentImageUri = getImageUri(this) // Membuat URI untuk gambar kamera
        currentImageUri?.let { uri ->
            launcherCamera.launch(uri) // Membuka kamera
        } ?: showError("Failed to create URI for camera")
    }

    // ActivityResultLauncher untuk kamera
    private val launcherCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            binding.addImage.setImageURI(currentImageUri) // Menampilkan gambar yang diambil
        } else {
            currentImageUri = null
            showError("Failed to take picture")
        }
    }

    // Mengamati ViewModel
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(this) { error ->
            error?.let { showError(it) }
        }
        viewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                startActivity(Intent(this, MainActivity::class.java)) // Kembali ke MainActivity jika sukses
                finish()
            }
        }
    }

    private fun showError(message: String) {
        Log.e("AddStoryActivity", message)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show() // Menampilkan snackbar untuk feedback
    }

    private fun animateButton(button: View) {
        // Membuat animasi scale-up (memperbesar ukuran tombol)
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f)

        val fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f)

        // Mengatur durasi animasi
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn)
            duration = 500 // Durasi animasi 300 ms
        }

        animatorSet.start()
    }
}
