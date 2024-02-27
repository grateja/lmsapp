package com.vag.lmsapp.util.databindings

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.vag.lmsapp.model.EnumJoFilterBy
import com.vag.lmsapp.model.EnumMeasureUnit
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.util.EnumSortDirection

@BindingAdapter("app:text")
fun setText(spinner: Spinner, text: String?) {
    for (i in 0 until spinner.adapter.count) {
        if(spinner.adapter.getItem(i).toString() == text) {
            spinner.setSelection(i)
            return
        }
    }
}

@InverseBindingAdapter(
    attribute = "app:text",
    event = "android:selectedItemPositionAttrChanged"
)
fun getText(spinner: Spinner): String? {
    return spinner.selectedItem.toString()
}

@BindingAdapter("app:sortDirection")
fun setSortDirection(spinner: Spinner, sortDirection: EnumSortDirection?) {
    sortDirection?.itemIndex?.let {
        spinner.setSelection(it)
    }
}

@InverseBindingAdapter(
    attribute = "app:sortDirection",
    event = "android:selectedItemPositionAttrChanged"
)
fun getSortDirection(spinner: Spinner): EnumSortDirection? {
    return EnumSortDirection.values().find {
        it.itemIndex == spinner.selectedItemPosition
    }
}

@BindingAdapter("app:joFilterBy")
fun setJoFilterBy(spinner: Spinner, joFilterBy: EnumJoFilterBy?) {
    joFilterBy?.itemIndex?.let {
        spinner.setSelection(it)
    }
}

@InverseBindingAdapter(
    attribute = "app:joFilterBy",
    event = "android:selectedItemPositionAttrChanged"
)
fun getJoFilterBy(spinner: Spinner): EnumJoFilterBy? {
    return EnumJoFilterBy.values().find {
        it.itemIndex == spinner.selectedItemPosition
    }
}



@BindingAdapter("app:measureUnit")
fun setMeasureUnit(spinner: Spinner, measureUnit: EnumMeasureUnit?) {
    for (i in 0 until spinner.adapter.count) {
        if(spinner.adapter.getItem(i).toString() == measureUnit?.value) {
            spinner.setSelection(i)
            return
        }
    }
}

@InverseBindingAdapter(
    attribute = "app:measureUnit",
    event = "android:selectedItemPositionAttrChanged"
)
fun getMeasureUnit(spinner: Spinner): EnumMeasureUnit? {
    return EnumMeasureUnit.values().find {
        it.value == spinner.selectedItem.toString()
    }
}

@BindingAdapter("app:paymentStatus")
fun setPaymentStatus(spinner: Spinner, paymentStatus: EnumPaymentStatus?) {
    for (i in 0 until spinner.adapter.count) {
        if(spinner.adapter.getItem(i).toString() == paymentStatus?.prompt) {
            spinner.setSelection(i)
            return
        }
    }
}

@InverseBindingAdapter(
    attribute = "app:paymentStatus",
    event = "android:selectedItemPositionAttrChanged"
)
fun getPaymentStatus(spinner: Spinner): EnumPaymentStatus? {
    return EnumPaymentStatus.values().find {
        it.prompt == spinner.selectedItem.toString()
    }
}