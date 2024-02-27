package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumActionPermission(val id: Int, val description: String) : Parcelable {
    ALL(1000, "Full access to all features"),
    BASIC(1001, "Perform all basic operations"),
    VIEW_DAILY_REPORTS(1002, "View daily sales reports"),
    CANCEL_JOB_ORDERS(1003, "Void or cancel job orders"),
    MODIFY_JOB_ORDERS(1004, "Remove or update items in job orders"),
    MODIFY_INVENTORY(1005, "Update inventory stocks"),
    MODIFY_SERVICES(1006, "Modify service prices"),
    MODIFY_DISCOUNTS(1007, "Modify discounts"),
    MODIFY_DELIVERIES(1008, "Modify delivery options"),
    MODIFY_MACHINES(1009, "Modify machine configurations"),
    VIEW_DASHBOARD(1010, "View the dashboard"),
    MODIFY_SETTINGS_JOB_ORDERS(1011, "Modify job order settings"),
    MODIFY_SETTINGS_PRINTERS(1012, "Modify printer settings"),
    MODIFY_SETTINGS_IPADDRESS(1013, "Modify IP address settings"),
    MODIFY_USERS(1014, "Modify user accounts"),
    MODIFY_CUSTOMER_DETAILS(1015, "Remove or update items in job orders");

    override fun toString() : String {
        return description
    }

    companion object {
        private fun fromId(id: Int?) : EnumActionPermission? {
            return values().find {
                it.id == id
            }
        }

        private fun fromIds(ids: List<Int>) : List<EnumActionPermission> {
            return ids.map {
                fromId(it)!!
            }
        }

        fun fromIds(ids: String?) : List<EnumActionPermission> {
            return ids?.split(",")?.map{ it.toInt() }?.let {
                fromIds(it)
            } ?: listOf()
        }

        fun toIds(permissions: List<EnumActionPermission>) : String {
            return permissions.joinToString(",") { it.id.toString() }
        }
    }
}