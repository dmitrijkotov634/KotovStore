package com.wavecat.kotovstore.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.WorkInfo
import androidx.work.WorkQuery
import com.bumptech.glide.Glide
import com.wavecat.kotovstore.InstallWorker
import com.wavecat.kotovstore.MainViewModel
import com.wavecat.kotovstore.R
import com.wavecat.kotovstore.adapter.ScreenshotsAdapter
import com.wavecat.kotovstore.databinding.PageFragmentBinding

class PageFragment : Fragment() {

    private var _binding: PageFragmentBinding? = null

    private val binding get() = _binding!!

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = PageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.currentApp.observe(viewLifecycleOwner) { app ->
            binding.pageName.text = app.name
            binding.pageAuthor.text = app.author

            Glide
                .with(requireActivity())
                .load(app.icon)
                .into(binding.pageIcon)

            model.workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromTags(listOf(app.packageName))
                    .addStates(listOf(WorkInfo.State.RUNNING))
                    .build()
            )
                .observe(viewLifecycleOwner) work@{
                    for (workInfo in it) {
                        binding.progress.visibility = View.VISIBLE
                        binding.pageIcon.alpha = 0.85f

                        binding.pageInstall.text =
                            binding.root.context.getString(R.string.cancel)

                        binding.progress.progress = workInfo.progress.getInt(
                            InstallWorker.PROGRESS,
                            0
                        )

                        binding.pageInstall.setOnClickListener {
                            model.workManager.cancelWorkById(workInfo.id)
                        }

                        return@work
                    }

                    binding.progress.visibility = View.INVISIBLE
                    binding.pageIcon.alpha = 1f

                    try {
                        val packageInfo = requireContext().packageManager.getPackageInfo(
                            app.packageName,
                            PackageManager.GET_GIDS
                        )

                        if (app.versionCode > PackageInfoCompat.getLongVersionCode(
                                packageInfo
                            )
                        ) {
                            binding.pageInstall.text =
                                binding.root.context.getString(R.string.update)
                            binding.pageInstall.setOnClickListener { model.installApp() }
                        } else {
                            binding.pageInstall.text = binding.root.context.getString(R.string.open)
                            binding.pageInstall.setOnClickListener {
                                startActivity(
                                    requireContext().packageManager.getLaunchIntentForPackage(
                                        app.packageName
                                    )
                                )
                            }
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        binding.pageInstall.text =
                            binding.root.context.getString(R.string.install)
                        binding.pageInstall.setOnClickListener { model.installApp() }
                    }
                }
        }

        model.currentInfo.observe(viewLifecycleOwner) { info ->
            binding.description.text = info.description
            binding.screenshots.adapter = ScreenshotsAdapter(info.screenshots)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}