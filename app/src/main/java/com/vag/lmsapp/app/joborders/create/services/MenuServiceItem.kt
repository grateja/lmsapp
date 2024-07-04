package com.vag.lmsapp.app.joborders.create.services

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.R
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.EnumWashType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class MenuServiceItem(
    var joServiceItemId: UUID?,

    @ColumnInfo(name = "id")
    val serviceRefId: UUID,

    val name: String,

    @ColumnInfo(name = "svc_minutes")
    val minutes: Int,

    var price: Float,

    @ColumnInfo("discounted_price")
    var discountedPrice: Float,

    @ColumnInfo(name = "svc_service_type")
    val serviceType: EnumServiceType,

    @ColumnInfo(name = "svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo(name = "svc_wash_type")
    val washType: EnumWashType?,

    var quantity: Int,
    var used: Int,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false,

    @ColumnInfo(name = "hidden")
    var hidden: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    var selected = false
    fun abbr() : String {
//        return if(machineType != null) {
            return machineType.abbr + ' ' + name
//        } else {
//            name
//        }
    }

    fun total(): Float {
        return quantity * discountedPrice
    }

    fun quantityStrAbbr() : String {
        return "($quantity * P$discountedPrice)"
    }

    fun quantityStr() : String {
        return "*$quantity" + if(quantity == 1) {
            " load"
        } else if(quantity == 0) {
            ""
        } else {
            " loads"
        }
    }

    fun description(): String {
        return "$minutes $machineType" + ((washType?.description) ?: "dry")
    }

    fun icon(): Int {
        return washType?.icon ?: R.drawable.icon_service_type_dry
    }
}