package com.vag.lmsapp.app.joborders.create.packages.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vag.lmsapp.R
import com.vag.lmsapp.adapters.Adapter
import com.vag.lmsapp.app.packages.PackageItem
import com.vag.lmsapp.databinding.FragmentJobOrderPackagePreviewBottomSheetBinding
import com.vag.lmsapp.fragments.BaseModalFragment
import com.vag.lmsapp.util.CrudActivity.Companion.ENTITY_ID
import com.vag.lmsapp.util.toUUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class JobOrderPackagePreviewBottomSheetFragment : BaseModalFragment() {
    private lateinit var binding: FragmentJobOrderPackagePreviewBottomSheetBinding
    private val viewModel: JobOrderPackagePreviewViewModel by viewModels()
    private val adapter = Adapter<PackageItem>(R.layout.recycler_item_package_item)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobOrderPackagePreviewBottomSheetBinding.inflate(
            inflater, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.inclPackageLegend.recyclerViewItems.adapter = adapter

        viewModel.jobOrderPackage.observe(viewLifecycleOwner, Observer {
            binding.inclPackageLegend.viewModel = it
            it?.simpleList()?.let {
                adapter.setData(it)
            }
        })

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        arguments?.getString(ENTITY_ID).toUUID()?.let {
            viewModel.setId(it)
        }

        return binding.root
    }

    companion object {
        fun newInstance(packageId: UUID) : JobOrderPackagePreviewBottomSheetFragment {
            return JobOrderPackagePreviewBottomSheetFragment().apply {
                this.arguments = Bundle().apply {
                    putString(ENTITY_ID, packageId.toString())
                }
            }
        }
    }
}