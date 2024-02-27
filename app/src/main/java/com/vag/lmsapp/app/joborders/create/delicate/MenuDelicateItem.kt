package com.vag.lmsapp.app.joborders.create.delicate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuDelicateItem(
    val id: String,
    val category: String,
    val name: String,
    var selected: Boolean = false,
    var quantity: Int = 0,
) : Parcelable