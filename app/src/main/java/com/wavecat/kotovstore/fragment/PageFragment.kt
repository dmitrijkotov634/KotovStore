package com.wavecat.kotovstore.fragment

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
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

        model.currentApp.observe(viewLifecycleOwner) { main ->
            if (main == null) return@observe

            binding.pageName.text = main.app.name
            binding.pageAuthor.text = main.app.author

            Glide
                .with(requireActivity())
                .load(main.app.icon)
                .into(binding.pageIcon)

            model.workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromTags(listOf(main.info.apk))
                    .addStates(listOf(WorkInfo.State.RUNNING))
                    .build()
            )
                .observe(viewLifecycleOwner) work@{
                    for (workInfo in it) {
                        binding.progress.visibility = View.VISIBLE
                        binding.pageIcon.alpha = 0.5f

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

                    binding.progress.progress = 0
                    binding.progress.visibility = View.INVISIBLE
                    binding.pageIcon.alpha = 1f

                    try {
                        val packageInfo = requireContext().packageManager.getPackageInfo(
                            main.app.packageName,
                            PackageManager.GET_GIDS
                        )

                        if (main.app.versionCode > PackageInfoCompat.getLongVersionCode(
                                packageInfo
                            )
                        ) {
                            binding.pageInstall.text =
                                binding.root.context.getString(R.string.update)
                            binding.pageInstall.setOnClickListener { model.installApp() }

                            if (main.info.notice != null) {
                                binding.noticeCard.visibility = View.VISIBLE
                                binding.notice.text =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Html.fromHtml(
                                            main.info.notice,
                                            Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        Html.fromHtml(main.info.notice)
                                    }
                            }
                        } else {
                            binding.pageInstall.text = binding.root.context.getString(R.string.open)
                            binding.pageInstall.setOnClickListener {
                                startActivity(
                                    requireContext().packageManager.getLaunchIntentForPackage(
                                        main.app.packageName
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

            binding.description.text = main.info.description
            binding.screenshots.adapter = ScreenshotsAdapter(main.info.screenshots)
        }
    }

    override fun onPause() {
        model.unselectApp()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}