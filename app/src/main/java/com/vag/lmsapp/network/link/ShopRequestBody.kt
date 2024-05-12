package com.vag.lmsapp.network.link

import com.vag.lmsapp.room.entities.EntityShop

data class ShopRequestBody(
    val id: String?,
    val name: String?,
    val address: String?,
    val contact_number: String?
) {
    companion object {
        fun fromShopEntity (shop: EntityShop) : ShopRequestBody {
            return ShopRequestBody(
                shop.id.toString(),
                shop.name,
                shop.address,
                shop.contactNumber
            )
        }
    }
}
