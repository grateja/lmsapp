//package com.csi.palabakosys.util.databindings
//
//import android.icu.util.MeasureUnit
//import android.view.View
//import android.widget.RadioGroup
//import androidx.databinding.BindingAdapter
//import androidx.databinding.InverseBindingAdapter
//import androidx.databinding.InverseBindingListener
//import com.csi.palabakosys.R
//import com.csi.palabakosys.model.EnumDiscountType
//import com.csi.palabakosys.model.EnumMeasureUnit
//
////class MeasureUnitDataBinding {
//    @BindingAdapter("app:selectedMeasureUnit")
//    fun setMeasureUnit(radioGroup: RadioGroup, measureUnit: EnumMeasureUnit?) {
//        val selectedId = when (measureUnit) {
//            EnumMeasureUnit.PCS -> R.id.radio_measure_unit_pcs
//            EnumMeasureUnit.SACHET -> R.id.radio_measure_unit_sachet
//            EnumMeasureUnit.MILLILITER -> R.id.radio_measure_unit_milliliter
//            EnumMeasureUnit.LITER -> R.id.radio_measure_unit_liter
//            EnumMeasureUnit.LOAD -> R.id.radio_measure_unit_load
//            else -> View.NO_ID
//        }
//        if (radioGroup.checkedRadioButtonId != selectedId) {
//            radioGroup.check(selectedId)
//        }
//    }
//
//    @InverseBindingAdapter(attribute = "app:selectedMeasureUnit", event = "android:checkedButtonAttrChanged")
//    fun getMeasureUnit(radioGroup: RadioGroup): EnumMeasureUnit? {
//        return when (radioGroup.checkedRadioButtonId) {
//            R.id.radio_measure_unit_pcs -> EnumMeasureUnit.PCS
//            R.id.radio_measure_unit_sachet -> EnumMeasureUnit.SACHET
//            R.id.radio_measure_unit_milliliter -> EnumMeasureUnit.MILLILITER
//            R.id.radio_measure_unit_liter -> EnumMeasureUnit.LITER
//            R.id.radio_measure_unit_load -> EnumMeasureUnit.LOAD
//            else -> null
//        }
//    }
////    @BindingAdapter("android:checkedButtonAttrChanged")
////    fun setCheckedButtonListener(radioGroup: RadioGroup, listener: InverseBindingListener?) {
////        radioGroup.setOnCheckedChangeListener { _, _ ->
////            listener?.onChange()
////        }
////    }
////}