package com.dicoding.picodiploma.nganugramstoryapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.nganugramstoryapp.R
import com.dicoding.picodiploma.nganugramstoryapp.data.LoadingPagingAdapter
import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.nganugramstoryapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.nganugramstoryapp.view.ViewModelFactory
import com.dicoding.picodiploma.nganugramstoryapp.view.add.AddStoryActivity
import com.dicoding.picodiploma.nganugramstoryapp.view.detail.DetailActivity
import com.dicoding.picodiploma.nganugramstoryapp.view.location.LocationActivity
import com.dicoding.picodiploma.nganugramstoryapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        setupFab()
        observePagingStories()
        observeSession()
    }

    private fun setupView() {
        title = getString(R.string.app_name)
        supportActionBar?.show()
    }

    private fun setupRecyclerView() {
        adapter = MainAdapter()
        adapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                Intent(this@MainActivity, DetailActivity::class.java).also { intent ->
                    intent.putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                    startActivity(intent)
                }
            }
        })

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter.withLoadStateFooter(
                footer = LoadingPagingAdapter { this@MainActivity.adapter.retry() }
            )
        }
    }

    private fun setupFab() {
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observePagingStories() {
        lifecycleScope.launch {
            viewModel.pagingStories.observe(this@MainActivity) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                navigateToWelcomeActivity()
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
