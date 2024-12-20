package com.dicoding.picodiploma.nganugramstoryapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.nganugramstoryapp.databinding.FooterPagingBinding

class LoadingPagingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingPagingAdapter.LoadingPagingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingPagingViewHolder {
        val binding = FooterPagingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingPagingViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingPagingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingPagingViewHolder(private val binding: FooterPagingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButtonPaging.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMessage.text = loadState.error.localizedMessage
            }
            binding.progressBarFooter.isVisible = loadState is LoadState.Loading
            binding.retryButtonPaging.isVisible = loadState is LoadState.Error
            binding.errorMessage.isVisible = loadState is LoadState.Error
        }
    }
}
