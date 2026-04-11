package com.tambal_ban.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tambal_ban.databinding.FragmentLiveStatusBinding

class LiveStatusDrawer : BottomSheetDialogFragment() {

    private var _binding: FragmentLiveStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LiveStatusDrawer"
        fun newInstance() = LiveStatusDrawer()
    }
}
