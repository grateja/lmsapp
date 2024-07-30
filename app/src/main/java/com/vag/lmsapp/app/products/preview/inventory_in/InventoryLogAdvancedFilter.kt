package com.vag.lmsapp.app.products.preview.inventory_in

import android.os.Parcelable
import com.vag.lmsapp.model.BaseFilterParams
import java.util.UUID

data class InventoryLogAdvancedFilter(
    var productId: UUID? = null
): Parcelable, BaseFilterParams()