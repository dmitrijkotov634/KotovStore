package com.wavecat.kotovstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wavecat.kotovstore.R
import com.wavecat.kotovstore.databinding.ScreenshotItemBinding

class ScreenshotsAdapter(
    private var items: List<String>,
) : RecyclerView.Adapter<ScreenshotsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = ScreenshotItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.screenshot_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide
            .with(holder.binding.root.context)
            .load(items[position])
            .into(holder.binding.root)
    }

    override fun getItemCount() = items.size
}