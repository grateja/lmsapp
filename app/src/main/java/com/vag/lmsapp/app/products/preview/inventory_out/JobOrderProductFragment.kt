package com.vag.lmsapp.app.products.preview.inventory_out

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vag.lmsapp.databinding.FragmentJobOrderProductBinding

class JobOrderProductFragment : Fragment() {
    private lateinit var binding: FragmentJobOrderProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobOrderProductBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }
}