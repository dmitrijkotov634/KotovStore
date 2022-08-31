package com.wavecat.kotovstore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wavecat.kotovstore.MainViewModel
import com.wavecat.kotovstore.R
import com.wavecat.kotovstore.adapter.AppsAdapter
import com.wavecat.kotovstore.databinding.AppsFragmentBinding


class AppsFragment : Fragment() {

    private var _binding: AppsFragmentBinding? = null

    private val binding get() = _binding!!

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AppsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pm = requireContext().packageManager

        model.apps.observe(viewLifecycleOwner) { apps ->
            binding.list.adapter = AppsAdapter(pm, apps.filter {
                it.minSdk <= android.os.Build.VERSION.SDK_INT
            }) { app ->
                model.selectApp(app)
            }

            binding.swipeRefreshLayout.isRefreshing = false
        }

        model.currentApp.observe(viewLifecycleOwner) {
            if (it != null)
                findNavController().navigate(R.id.PageFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            model.loadApps()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}