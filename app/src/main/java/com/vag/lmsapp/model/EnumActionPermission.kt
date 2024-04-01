package com.vag.lmsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EnumActionPermission(val id: Int, val description: String) : Parcelable {
    ALL(1000, "Has full access to all features"),
    BASIC(1001, "Can perform all basic operations"),
    VIEW_DAILY_REPORTS(1002, "Can view daily sales reports"),
    DELETE_JOB_ORDERS(1003, "Can delete job orders"),
    MODIFY_JOB_ORDERS(1004, "Can remove or update items in job orders"),
    MODIFY_INVENTORY(1005, "Can update inventory stocks"),
    MODIFY_SERVICES(1006, "Can modify service prices"),
    MODIFY_DISCOUNTS(1007, "Can modify discounts"),
    MODIFY_DELIVERIES(1008, "Can modify delivery options"),
    MODIFY_MACHINES(1009, "Can modify machine configurations"),
    VIEW_DASHBOARD(1010, "Can view the dashboard"),
    MODIFY_SETTINGS_JOB_ORDERS(1011, "Can modify job order settings"),
    MODIFY_SETTINGS_PRINTERS(1012, "Can modify printer settings"),
    MODIFY_SETTINGS_IPADDRESS(1013, "Can modify IP address settings"),
    MODIFY_USERS(1014, "Can modify user accounts"),
    MODIFY_CUSTOMER_DETAILS(1015, "Can remove or update items in job orders"),
    MODIFY_SETTINGS_SHOP_DETAILS(1016, "Can modify shop details. (Shop name, address, contact number)");

    override fun toString() : String {
        return description
    }

    companion object {
        private fun fromId(id: Int?) : EnumActionPermission? {
            return entries.find {
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