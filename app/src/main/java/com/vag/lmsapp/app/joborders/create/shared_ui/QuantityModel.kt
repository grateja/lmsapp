package com.vag.lmsapp.app.joborders.create.shared_ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class QuantityModel(
    val id: UUID,
    val name: String,
    var quantity: Int = 0,
    val type: String
) : Parcelable {
    companion object {
        const val TYPE_PACKAGE: String = "package"
        const val TYPE_EXTRAS: String = "extras"
        const val TYPE_SERVICE: String = "service"
        const val TYPE_PRODUCT: String = "product"
    }
}