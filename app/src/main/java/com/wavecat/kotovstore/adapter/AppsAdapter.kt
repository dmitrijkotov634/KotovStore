package com.wavecat.kotovstore.adapter

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wavecat.kotovstore.R
import com.wavecat.kotovstore.databinding.AppItemBinding
import com.wavecat.kotovstore.models.App

class AppsAdapter(
    private val packageManager: PackageManager,
    private var apps: List<App>,
    private val listener: (App) -> Unit
) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = AppItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.app_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]

        holder.binding.appName.text = app.name
        holder.binding.appCategory.text = app.category

        holder.binding.root.setOnClickListener { listener(apps[position]) }
        holder.binding.install.setOnClickListener { listener(apps[position]) }

        val context = holder.binding.root.context

        try {
            val info = packageManager.getPackageInfo(
                app.packageName,
                PackageManager.GET_GIDS
            )

            holder.binding.install.text = context.getString(R.string.update)

            if (app.versionCode <= PackageInfoCompat.getLongVersionCode(info))
                holder.binding.install.visibility = View.GONE

        } catch (e: PackageManager.NameNotFoundException) {
            holder.binding.install.text = context.getString(R.string.install)
        }

        Glide
            .with(context)
            .load(app.icon)
            .into(holder.binding.appIcon)
    }

    override fun getItemCount() = apps.size
}