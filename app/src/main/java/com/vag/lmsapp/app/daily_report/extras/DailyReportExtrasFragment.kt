//package com.vag.lmsapp.app.daily_report.extras
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.Observer
//import com.vag.lmsapp.R
//import com.vag.lmsapp.adapters.Adapter
//import com.vag.lmsapp.app.daily_report.DailyReportViewModel
//import com.vag.lmsapp.databinding.FragmentDailyReportExtrasBinding
//
//class DailyReportExtrasFragment : Fragment() {
//    private lateinit var binding: FragmentDailyReportExtrasBinding
//    private val viewModel: DailyReportViewModel by activityViewModels()
//
//    private val extrasAdapter = Adapter<String>(R.layout.recycler_item_simple_item)
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentDailyReportExtrasBinding.inflate(
//            inflater, container, false
//        )
//
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
//        binding.recyclerViewExtras.adapter = extrasAdapter
//
//        viewModel.extras.observe(viewLifecycleOwner, Observer {
//            extrasAdapter.setData(it.map {it.toString()})
//        })
//
//        viewModel.extrasSum.observe(viewLifecycleOwner, Observer {
//            binding.textTopCaption.text = requireContext().resources.getQuantityString(R.plurals.extras, it, it)
//        })
//
//        return binding.root
//    }
//}