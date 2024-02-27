package com.vag.lmsapp.util.databindings

import android.view.View
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.vag.lmsapp.R
import com.vag.lmsapp.model.EnumWashType

//class MeasureUnitDataBinding {
    @BindingAdapter("app:selectedWashType")
    fun setWashType(radioGroup: RadioGroup, washType: EnumWashType?) {
        val selectedId = when (washType) {
            EnumWashType.HOT -> R.id.radio_wash_type_hot
            EnumWashType.WARM -> R.id.radio_wash_type_warm
            EnumWashType.COLD -> R.id.radio_wash_type_cold
            EnumWashType.DELICATE -> R.id.radio_wash_type_delicate
            EnumWashType.SUPER_WASH -> R.id.radio_wash_type_super
            else -> View.NO_ID
        }
        if (radioGroup.checkedRadioButtonId != selectedId) {
            radioGroup.check(selectedId)
        }
    }

    @InverseBindingAdapter(attribute = "app:selectedWashType", event = "android:checkedButtonAttrChanged")
    fun getWashType(radioGroup: RadioGroup): EnumWashType? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_wash_type_hot -> EnumWashType.HOT
            R.id.radio_wash_type_warm -> EnumWashType.WARM
            R.id.radio_wash_type_cold -> EnumWashType.COLD
            R.id.radio_wash_type_delicate -> EnumWashType.DELICATE
            R.id.radio_wash_type_super -> EnumWashType.SUPER_WASH
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