package com.dicoding.picodiploma.nganugramstoryapp.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.nganugramstoryapp.R
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.nganugramstoryapp.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        observeDetail()
    }

    private fun setupView() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.detail_story)
        }
    }

    private fun observeDetail() {
        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        storyId?.let { id -> viewModel.getStoryDetail(id) }

        viewModel.storyDetail.observe(this) { story ->
            binding.apply {
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(ivDetailPhoto)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}