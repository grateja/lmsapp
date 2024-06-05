package com.vag.lmsapp.network.requests_body

import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.entities.EntityExpense
import com.vag.lmsapp.room.entities.EntityInventoryLog
import com.vag.lmsapp.room.entities.EntityJobOrder
import com.vag.lmsapp.room.entities.EntityJobOrderDeliveryCharge
import com.vag.lmsapp.room.entities.EntityJobOrderDiscount
import com.vag.lmsapp.room.entities.EntityJobOrderExtras
import com.vag.lmsapp.room.entities.EntityJobOrderPayment
import com.vag.lmsapp.room.entities.EntityJobOrderProduct
import com.vag.lmsapp.room.entities.EntityJobOrderService
import com.vag.lmsapp.room.entities.EntityMachineRemarks
import com.vag.lmsapp.room.entities.EntityMachineUsage
import com.vag.lmsapp.room.entities.EntityShop

data class BacklogBody(
    val shop: EntityShop,
    val customers: List<EntityCustomer>?,
    val payments: List<EntityJobOrderPayment>?,
    val jobOrders: List<EntityJobOrder>?,
    val jobOrderServices: List<EntityJobOrderService>?,
    val jobOrderProducts: List<EntityJobOrderProduct>?,
    val jobOrderExtras: List<EntityJobOrderExtras>?,
    val jobOrderDeliveryCharges: List<EntityJobOrderDeliveryCharge>?,
    val jobOrderDiscounts: List<EntityJobOrderDiscount>?,
    val machineUsages: List<EntityMachineUsage>?,
    val machineRemarks: List<EntityMachineRemarks>?,
    val expenses: List<EntityExpense>?,
    val inventoryLog: List<EntityInventoryLog>?
)