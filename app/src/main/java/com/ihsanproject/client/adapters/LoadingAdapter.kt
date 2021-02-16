package com.ihsanproject.client.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ihsanproject.client.R
import com.ihsanproject.client.extensions.inflate
import kotlinx.android.synthetic.main.recyclerview_item_loading_state.view.*

class LoadingAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingAdapter.LoadingStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_loading_state, false)
        return LoadingStateViewHolder(inflatedView, retry)
    }

    class LoadingStateViewHolder(itemView: View, retry: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val errorMessage: TextView = itemView.errorMessage
        private val progressBar: ProgressBar = itemView.progress_bar
        private val btnRetry: Button = itemView.btnRetry

        init {
            btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bindState(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                errorMessage.text = loadState.error.localizedMessage
            }
            progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.INVISIBLE
            errorMessage.visibility = if (loadState !is LoadState.Loading) View.VISIBLE else View.INVISIBLE
            btnRetry.visibility = if (loadState !is LoadState.Loading) View.VISIBLE else View.INVISIBLE

        }

    }
}