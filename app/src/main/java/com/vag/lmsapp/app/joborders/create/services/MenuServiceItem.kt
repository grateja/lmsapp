package com.vag.lmsapp.app.joborders.create.services

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.UUID

@Parcelize
class MenuServiceItem(
    var joServiceItemId: UUID?,

    @ColumnInfo(name = "id")
    val serviceRefId: UUID,

    val name: String,

    @ColumnInfo(name = "svc_minutes")
    val minutes: Int,

    val price: Float,

    @ColumnInfo(name = "svc_machine_type")
    val machineType: EnumMachineType,

    @ColumnInfo(name = "svc_wash_type")
    val washType: EnumWashType?,

    var quantity: Int,
    var used: Int,

    @ColumnInfo(name = "void")
    var isVoid: Boolean = false,

    @ColumnInfo(name = "deleted_at")
    var deletedAt: Instant? = null,
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
    fun quantityStr() : String {
        return "*$quantity" + if(quantity == 1) {
            " load"
        } else if(quantity == 0) {
            ""
        } else {
            " loads"
        }
    }
}