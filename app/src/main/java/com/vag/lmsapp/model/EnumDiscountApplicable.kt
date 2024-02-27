package com.vag.lmsapp.model

enum class EnumDiscountApplicable(val value: String, val id: Int) {
    TOTAL_AMOUNT("Total Amount", 1),
    WASH_DRY_SERVICES("Wash & Dry Services", 2),
    PRODUCTS_CHEMICALS("Products & Chemicals", 3),
    EXTRAS("Extras", 4),
    DELIVERY("Pickup/Delivery", 5);

    override fun toString() : String {
        return value
    }

    companion object {
        private fun fromId(id: Int?) : EnumDiscountApplicable? {
            return entries.find {
                it.id == id
            }
        }

        private fun fromIds(ids: List<Int>) : List<EnumDiscountApplicable> {
            return ids.map {
                fromId(it)!!
            }
        }

        fun fromIds(ids: String?) : List<EnumDiscountApplicable> {
            return ids?.split(",")?.map{ it.toInt() }?.let {
                fromIds(it)
            } ?: listOf()
        }

        fun toIds(discounts: List<EnumDiscountApplicable>) : String {
            return discounts.joinToString(",") { it.id.toString() }
        }
    }
}