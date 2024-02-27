package com.vag.lmsapp.util.databindings

import android.view.View
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.vag.lmsapp.R
import com.vag.lmsapp.model.EnumProductType

//class MeasureUnitDataBinding {
    @BindingAdapter("app:selectedProductType")
    fun setProductType(radioGroup: RadioGroup, productType: EnumProductType?) {
        val selectedId = when (productType) {
            EnumProductType.DETERGENT -> R.id.radio_product_type_detergent
            EnumProductType.FAB_CON -> R.id.radio_product_type_fab_con
            EnumProductType.OTHER -> R.id.radio_product_type_other
            else -> View.NO_ID
        }
        if (radioGroup.checkedRadioButtonId != selectedId) {
            radioGroup.check(selectedId)
        }
    }

    @InverseBindingAdapter(attribute = "app:selectedProductType", event = "android:checkedButtonAttrChanged")
    fun getProductType(radioGroup: RadioGroup): EnumProductType? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_product_type_detergent -> EnumProductType.DETERGENT
            R.id.radio_product_type_fab_con -> EnumProductType.FAB_CON
            R.id.radio_product_type_other -> EnumProductType.OTHER
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