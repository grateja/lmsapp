package com.vag.lmsapp.room.entities

data class EntityExpensesAggrResult(
    val tag: String?,
    val count: Int,
    val sum: Float,
) {
   override fun toString() : String {
       return "$tag($count)"
   }
}
