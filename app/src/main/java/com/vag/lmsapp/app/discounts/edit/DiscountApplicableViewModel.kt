package com.vag.lmsapp.app.discounts.edit

import com.vag.lmsapp.model.EnumDiscountApplicable

data class DiscountApplicableViewModel(
    val applicable: EnumDiscountApplicable,
    var selected: Boolean
)