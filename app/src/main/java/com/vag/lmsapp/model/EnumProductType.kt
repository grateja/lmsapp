package com.vag.lmsapp.model

import com.vag.lmsapp.R

enum class EnumProductType(val id: Int, val value: String, val icon: Int) {
    DETERGENT(1, "Detergent", R.drawable.icon_detergent),
    FAB_CON(2, "Fab-Con", R.drawable.icon_fabcon),
    OTHER(3, "Other", R.drawable.icon_other_products);

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumProductType? {
            return entries.find {
                it.id == id
            }
        }
        fun fromName(name: String?) : EnumProductType? {
            return entries.find {
                it.value.uppercase() == name?.uppercase()
            }
        }
    }
}