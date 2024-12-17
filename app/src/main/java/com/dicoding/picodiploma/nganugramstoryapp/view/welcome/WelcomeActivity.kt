package com.dicoding.picodiploma.nganugramstoryapp.view.welcome

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityWelcomeBinding
import com.dicoding.picodiploma.nganugramstoryapp.view.login.LoginActivity
import com.dicoding.picodiploma.nganugramstoryapp.view.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        animateImage()  // Menambahkan animasi saat activity dimulai
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun animateImage() {
        // Ambil referensi ke ImageView
        val imageView = binding.imageView

        // Membuat animasi translationX, mulai dari -500 (di luar layar) ke 0 (posisi normal)
        val translationX = ObjectAnimator.ofFloat(imageView, "translationX", -500f, 0f)
        translationX.duration = 1200 // Durasi animasi 1 detik
        translationX.interpolator = OvershootInterpolator() // Menambahkan interpolator untuk efek overshoot

        // Jalankan animasi
        translationX.start()
    }
}
