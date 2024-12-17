package com.dicoding.picodiploma.nganugramstoryapp.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.nganugramstoryapp.utils.Result
import com.dicoding.picodiploma.nganugramstoryapp.view.ViewModelFactory
import com.dicoding.picodiploma.nganugramstoryapp.view.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeLogin()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Email tidak boleh kosong"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password tidak boleh kosong"
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.passwordEditText.error = "Password minimal 8 karakter"
                    return@setOnClickListener
                }
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun observeLogin() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
            }
        }

        lifecycleScope.launch {
            viewModel.loginResult.collect { result ->
                result?.let {
                    when (it) {
                        is Result.Success -> {
                            // Navigate to MainActivity
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                this@LoginActivity,
                                it.error,
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetLoginResult()
                        }
                        is Result.Loading -> {
                            // Loading state handled by isLoading StateFlow
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loginButton.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}