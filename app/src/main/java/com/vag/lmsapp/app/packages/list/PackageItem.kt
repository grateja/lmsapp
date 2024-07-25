package com.vag.lmsapp.app.packages.list

import android.os.Parcelable
import com.vag.lmsapp.app.packages.EnumPackageItemType
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PackageItem(
    val packageItemType: EnumPackageItemType,
    val itemId: UUID,
    val name: String,
    val description: String,
    var price: Float,
    var quantity: Int,
    var deleted: Boolean,
) : Parcelable