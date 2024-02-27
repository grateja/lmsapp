package com.vag.lmsapp.util.databindings

import android.view.View
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.vag.lmsapp.R
import com.vag.lmsapp.model.EnumMachineType

//class MeasureUnitDataBinding {
    @BindingAdapter("app:selectedMachineType")
    fun setMachineType(radioGroup: RadioGroup, machineType: EnumMachineType?) {
        val selectedId = when (machineType) {
            EnumMachineType.REGULAR_WASHER -> R.id.radio_machine_type_regular_washer
            EnumMachineType.REGULAR_DRYER -> R.id.radio_machine_type_regular_dryer
            EnumMachineType.TITAN_WASHER -> R.id.radio_machine_type_titan_washer
            EnumMachineType.TITAN_DRYER -> R.id.radio_machine_type_titan_dryer
            else -> View.NO_ID
        }
        if (radioGroup.checkedRadioButtonId != selectedId) {
            radioGroup.check(selectedId)
        }
    }

    @InverseBindingAdapter(attribute = "app:selectedMachineType", event = "android:checkedButtonAttrChanged")
    fun getMachineType(radioGroup: RadioGroup): EnumMachineType? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_machine_type_regular_washer -> EnumMachineType.REGULAR_WASHER
            R.id.radio_machine_type_regular_dryer -> EnumMachineType.REGULAR_DRYER
            R.id.radio_machine_type_titan_washer -> EnumMachineType.TITAN_WASHER
            R.id.radio_machine_type_titan_dryer -> EnumMachineType.TITAN_DRYER
            else -> null
        }
    }
//    @BindingAdapter("android:checkedButtonAttrChanged")
//    fun setCheckedButtonListener(radioGroup: RadioGroup, listener: InverseBindingListener?) {
//        radioGroup.setOnCheckedChangeListener { _, _ ->
//            listener?.onChange()
//        }
//    }
//}