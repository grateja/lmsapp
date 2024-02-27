package com.vag.lmsapp.model

import com.vag.lmsapp.R

enum class EnumDeliveryVehicle(val id: Int, val value: String, val description: String, val icon: Int) {
    TRIKE_PEDAL(1, "Trike Pedal", "Up to 50KG, Below 800 meters distance", R.drawable.icon_delivery_trike_pedal),
    TRIKE_ELECTRIC(2,"Trike Electric", "Up to 100KG, Below 2KM distance", R.drawable.icon_delivery_trike_electric),
    MOTORCYCLE(3, "Motorcycle", "Up to 20KG, Below 5KM distance", R.drawable.icon_delivery_motorcycle),
    TRICYCLE(4, "Tricycle", "Up to 100KG, Below 5KM distance", R.drawable.icon_delivery_tricycle),
    SEDAN(5,"Sedan", "Up to 200KG, Good for delicate garments and long distance", R.drawable.icon_delivery_sedan),
    MPV(6,"MPV", "Up to 300KG, Good for long distance", R.drawable.icon_delivery_mpv),
    SMALL_VAN(7,"Small Van", "Up to 600KG, Good for long distance and bulk delivery", R.drawable.icon_delivery_small_van);

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromId(id: Int?) : EnumDeliveryVehicle? {
            return entries.find {
                it.id == id
            }
        }
    }
}