package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vag.lmsapp.app.dashboard.data.JobOrderCounts
import com.vag.lmsapp.app.gallery.picture_preview.PhotoItem
import com.vag.lmsapp.app.joborders.list.JobOrderListItem
import com.vag.lmsapp.app.joborders.list.JobOrderQueryResult
import com.vag.lmsapp.app.joborders.list.JobOrderResultSummary
import com.vag.lmsapp.app.joborders.payment.JobOrderPaymentMinimal
import com.vag.lmsapp.model.EnumJoFilterBy
import com.vag.lmsapp.model.EnumPaymentStatus
import com.vag.lmsapp.model.JobOrderAdvancedFilter
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.util.EnumSortDirection
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Dao
interface DaoJobOrder {
    @Upsert
    fun insertJobOrder(entityJobOrder: EntityJobOrder)

    @Upsert
    fun insertJobOrderService(entityJobOrderService: List<EntityJobOrderService>)

    @Upsert
    fun insertJobOrderProduct(entityJobOrderProduct: List<EntityJobOrderProduct>)

    @Upsert
    fun insertJobOrderExtras(entityJobOrderExtras: List<EntityJobOrderExtras>)

    @Upsert
    fun insertDeliveryCharge(entityJobOrderDeliveryCharge: EntityJobOrderDeliveryCharge)

    @Upsert
    fun insertDiscount(entityJobOrderDiscount: EntityJobOrderDiscount)

    @Delete
    fun deleteProducts(products: List<EntityJobOrderProduct>)

    @Delete
    fun deleteServices(services: List<EntityJobOrderService>)

    @Query("UPDATE products SET current_stock = current_stock - (COALESCE((SELECT (:newQuantity - quantity) FROM job_order_products WHERE id = :joProductId), :newQuantity) * unit_per_serve) WHERE id = :productId")
    fun updateQuantity(productId: String?, joProductId: String?, newQuantity: Float)

    @Transaction
    suspend fun save(entityJobOrderWithItems: EntityJobOrderWithItems) {
        insertJobOrder(entityJobOrderWithItems.jobOrder)

        entityJobOrderWithItems.services?.let {
            insertJobOrderService(it)
        }
        entityJobOrderWithItems.extras?.let {
            insertJobOrderExtras(it)
        }
        entityJobOrderWithItems.deliveryCharge?.let {
            insertDeliveryCharge(it)
        }
        entityJobOrderWithItems.discount?.let {
            insertDiscount(it)
        }
        entityJobOrderWithItems.products?.let {
            it.forEach { jop ->
                updateQuantity(jop.productId.toString(), jop.id.toString(), jop.quantity)
            }
            insertJobOrderProduct(it)
        }
    }

    @Transaction
    @Query("SELECT * FROM job_orders WHERE id = :id")
    suspend fun getJobOrderWithItems(id: UUID) : EntityJobOrderWithItems?

    @Query("SELECT * FROM job_orders WHERE id = :id")
    suspend fun get(id: UUID?) : EntityJobOrder?

    @Query("DELETE FROM job_orders WHERE id = :id")
    suspend fun delete(id: UUID)

    @Query("SELECT job_order_number FROM job_orders ORDER BY job_order_number DESC")
    suspend fun getLastJobOrderNumber() : String?

    @Query("SELECT * FROM job_orders WHERE id = :id")
    fun getJobOrderWithItemsAsLiveData(id: UUID): LiveData<EntityJobOrderWithItems?>


//    @Update
//    suspend fun voidJobOrder(jobOrder: EntityJobOrder)

    @Transaction
    @Query("SELECT * FROM job_orders WHERE customer_id = :customerId AND payment_id IS NULL AND DATE(`created_at`/1000, 'unixepoch', 'localtime') = DATE('now', 'localtime') AND deleted_at IS NULL AND void_date IS NULL LIMIT 1")
    suspend fun getCurrentJobOrder(customerId: UUID?): EntityJobOrderWithItems?

    @Query("SELECT * FROM job_orders WHERE customer_id = :customerId AND payment_id IS NULL AND deleted_at IS NULL AND void_date IS NULL")
    fun getAllUnpaidByCustomerId(customerId: UUID?): LiveData<List<JobOrderPaymentMinimal>>

//    @Query("SELECT * FROM job_orders WHERE customer_id = :customerId AND payment_id IS NULL AND deleted_at IS NULL AND void_date IS NULL AND (:jobOrderId IS NULL OR (id IS NOT NULL AND id <> :jobOrderId))")
    @Query("SELECT * FROM job_orders WHERE customer_id = :customerId AND payment_id IS NULL AND deleted_at IS NULL AND void_date IS NULL")
    suspend fun getUnpaidByCustomerId(customerId: UUID): List<JobOrderPaymentMinimal>

    @Query("SELECT jo.id, jo.job_order_number, jo.discounted_amount, jo.payment_id, jo.customer_id, jo.created_at, cu.name, cu.crn, pa.created_at as date_paid, pa.cashless_provider, " +
            " CASE " +
            "    WHEN jo.payment_id IS NOT NULL OR date(jo.created_at / 1000, 'unixepoch', 'localtime') != date('now', 'localtime') THEN 1" +
            "    ELSE 0" +
            " END AS locked" +
            " FROM job_orders jo " +
            " JOIN customers cu ON jo.customer_id = cu.id LEFT JOIN job_order_payments pa ON jo.payment_id = pa.id " +
            " WHERE " +
            " (cu.name LIKE '%' || :keyword || '%'" +
            "       OR jo.job_order_number LIKE '%' || :keyword || '%'" +
            "       OR cu.crn LIKE '%' || :keyword || '%') " +
            " AND ((:paymentStatus = 0 AND pa.created_at IS NOT NULL) OR" +
            "      (:paymentStatus = 1 AND pa.created_at IS NULL) OR" +
            "      (:paymentStatus = 2))" +
            " AND (jo.deleted_at IS NULL " +
            " AND (:nonVoidOnly = 1 AND jo.void_date IS NULL OR :nonVoidOnly = 0 AND jo.void_date IS NOT NULL)) " +
            " AND (:customerId IS NULL OR cu.id = :customerId)" +
            " AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
            " (:filterBy = 'created' AND :dateFrom IS NOT NULL AND :dateTo IS NULL AND date(jo.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
            " (:filterBy = 'created' AND :dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo) OR" +
            " (:filterBy = 'paid' AND :dateFrom IS NOT NULL AND :dateTo IS NULL AND date(pa.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR" +
            " (:filterBy = 'paid' AND :dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(pa.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) " +
            " ORDER BY " +
            " CASE WHEN :orderBy = 'Date Created' AND :sortDirection = 'ASC' THEN jo.created_at END ASC, " +
            " CASE WHEN :orderBy = 'Date Paid' AND :sortDirection = 'ASC' THEN pa.created_at END ASC, " +
            " CASE WHEN :orderBy = 'Customer Name' AND :sortDirection = 'ASC' THEN cu.name END ASC, " +
            " CASE WHEN :orderBy = 'Job Order Number' AND :sortDirection = 'ASC' THEN jo.job_order_number END ASC, " +
            " CASE WHEN :orderBy = 'Date Created' AND :sortDirection = 'DESC' THEN jo.created_at END DESC, " +
            " CASE WHEN :orderBy = 'Date Paid' AND :sortDirection = 'DESC' THEN pa.created_at END DESC, " +
            " CASE WHEN :orderBy = 'Customer Name' AND :sortDirection = 'DESC' THEN cu.name END DESC, " +
            " CASE WHEN :orderBy = 'Job Order Number' AND :sortDirection = 'DESC' THEN jo.job_order_number END DESC " +
            " LIMIT 20 OFFSET :offset")
    fun load(keyword: String?, orderBy: String?, sortDirection: EnumSortDirection?, offset: Int, paymentStatus: EnumPaymentStatus?, customerId: UUID?, filterBy: EnumJoFilterBy?, nonVoidOnly: Boolean, dateFrom: LocalDate?, dateTo: LocalDate?): List<JobOrderListItem>

    @Query("SELECT " +
            "SUM(CASE WHEN pa.id IS NOT NULL THEN 1 ELSE 0 END) AS paidCount, " +
            "SUM(CASE WHEN pa.id IS NULL THEN 1 ELSE 0 END) AS unpaidCount, " +
            "COUNT(jo.id) AS totalResultCount, " +
            "SUM(CASE WHEN pa.id IS NOT NULL THEN jo.discounted_amount ELSE 0 END) AS paidSum, " +
            "SUM(CASE WHEN pa.id IS NULL THEN jo.discounted_amount ELSE 0 END) AS unpaidSum, " +
            "SUM(jo.discounted_amount) AS totalSum " +
            " FROM job_orders jo JOIN customers cu ON jo.customer_id = cu.id LEFT JOIN job_order_payments pa ON jo.payment_id = pa.id" +
            " WHERE " +
            " (cu.name LIKE '%' || :keyword || '%'" +
            "       OR jo.job_order_number LIKE '%' || :keyword || '%'" +
            "       OR cu.crn LIKE '%' || :keyword || '%') " +
            " AND (:nonVoidOnly = 1 AND jo.void_date IS NULL OR :nonVoidOnly = 0 AND jo.void_date IS NOT NULL) " +
            " AND ((:paymentStatus = 0 AND jo.payment_id IS NOT NULL) OR" +
            "      (:paymentStatus = 1 AND jo.payment_id IS NULL) OR" +
            "      (:paymentStatus = 2))" +
            " AND (jo.deleted_at IS NULL) " +
            " AND (:customerId IS NULL OR cu.id = :customerId)" +
            " AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
            " (:filterBy = 'created' AND :dateFrom IS NOT NULL AND :dateTo IS NULL AND date(jo.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR " +
            " (:filterBy = 'created' AND :dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo) OR" +
            " (:filterBy = 'paid' AND :dateFrom IS NOT NULL AND :dateTo IS NULL AND date(pa.created_at / 1000, 'unixepoch', 'localtime') = :dateFrom) OR" +
            " (:filterBy = 'paid' AND :dateFrom IS NOT NULL AND :dateTo IS NOT NULL AND date(pa.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) "
    )
    fun count(keyword: String?, paymentStatus: EnumPaymentStatus?, customerId: UUID?, filterBy: EnumJoFilterBy?, nonVoidOnly: Boolean, dateFrom: LocalDate?, dateTo: LocalDate?): JobOrderResultSummary?

    @Transaction
    suspend fun queryResult(keyword: String?, af: JobOrderAdvancedFilter, offset: Int, customerId: UUID?, nonVoidOnly: Boolean) : JobOrderQueryResult {
        return JobOrderQueryResult(
            load(keyword, af.orderBy, af.sortDirection, offset, af.paymentStatus, customerId, af.filterBy, nonVoidOnly, af.dateFilter?.dateFrom, af.dateFilter?.dateTo),
            count(keyword, af.paymentStatus, customerId, af.filterBy, nonVoidOnly, af.dateFilter?.dateFrom, af.dateFilter?.dateTo)
        )
    }

    @Query("UPDATE job_orders SET void_by = :userId, void_remarks = :remarks, void_date = :voidDate WHERE id = :jobOrderId")
    suspend fun voidJobOrder(jobOrderId: UUID, userId: UUID?, remarks: String?, voidDate: Instant? = Instant.now())
//
//    @Query("UPDATE job_order_payments SET void_by = :userId, void_remarks = :remarks, void_date = :voidDate WHERE id = :paymentId")
//    suspend fun voidPayment(paymentId: UUID?, userId: UUID?, remarks: String?, voidDate: Instant? = Instant.now())

    @Query("UPDATE job_order_services SET void = 1 WHERE job_order_id = :jobOrderId")
    suspend fun voidServices(jobOrderId: UUID)

    @Query("UPDATE job_order_products SET void = 1 WHERE job_order_id = :jobOrderId")
    suspend fun voidProducts(jobOrderId: UUID)

    @Query("UPDATE job_order_extras SET void = 1 WHERE job_order_id = :jobOrderId")
    suspend fun voidExtras(jobOrderId: UUID)

    @Query("UPDATE job_order_delivery_charges SET void = 1 WHERE id = :jobOrderId")
    suspend fun voidDeliveryCharges(jobOrderId: UUID)

    @Query("UPDATE job_order_discounts SET void = 1 WHERE id = :jobOrderId")
    suspend fun voidDiscounts(jobOrderId: UUID)

    @Query("UPDATE products SET current_stock = (current_stock + (:quantity * unit_per_serve)) WHERE id = :productId")
    suspend fun returnProduct(productId: UUID, quantity: Float)


    @Transaction
    suspend fun cancelJobOrder(jobOrderWithItems: EntityJobOrderWithItems, jobOrderVoid: EntityJobOrderVoid) {
        val jobOrderId = jobOrderWithItems.jobOrder.id
        val paymentId = jobOrderWithItems.paymentWithUser?.payment?.id

        voidJobOrder(jobOrderId, jobOrderVoid.voidByUserId, jobOrderVoid.remarks)
//        voidPayment(paymentId, jobOrderVoid.voidByUserId, jobOrderVoid.remarks)
        jobOrderWithItems.products?.onEach {
            returnProduct(it.productId, it.quantity)
        }
        voidServices(jobOrderId)
        voidProducts(jobOrderId)
        voidExtras(jobOrderId)
        voidDeliveryCharges(jobOrderId)
        voidDiscounts(jobOrderId)
    }

    @Query("SELECT * FROM job_order_pictures WHERE job_order_id = :jobOrderId ORDER BY created_at DESC")
    fun getPictures(jobOrderId: UUID?): LiveData<List<EntityJobOrderPictures>>

    @Query("SELECT * FROM job_order_pictures WHERE job_order_id = :jobOrderId ORDER BY created_at DESC")
    fun getPicturesAsLiveData(jobOrderId: UUID?): LiveData<List<PhotoItem>>

    @Upsert
    suspend fun attachPicture(jobOrderPictures: EntityJobOrderPictures)

    @Upsert
    suspend fun attachPictures(jobOrderPictures: List<EntityJobOrderPictures>)

    @Query("DELETE FROM job_order_pictures WHERE id = :uriId")
    suspend fun removePicture(uriId: UUID)

    @Query("SELECT" +
            " SUM(CASE WHEN payment_id IS NOT NULL THEN 1 ELSE 0 END) AS paid_count," +
            " SUM(CASE WHEN payment_id IS NULL THEN 1 ELSE 0 END) AS unpaid_count " +
            "     FROM job_orders WHERE (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom " +
            "         OR (:dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') BETWEEN :dateFrom AND :dateTo)) AND deleted_at IS NULL AND void_date IS NULL")
    fun getDashboardJobOrders(dateFrom: LocalDate, dateTo: LocalDate?): LiveData<JobOrderCounts>


    @Query("SELECT jo.id, jo.job_order_number, jo.discounted_amount, jo.payment_id, jo.customer_id, jo.created_at, cu.name, cu.crn, pa.created_at as date_paid, pa.cashless_provider, " +
            " CASE " +
            "    WHEN jo.payment_id IS NOT NULL OR date(jo.created_at / 1000, 'unixepoch', 'localtime') != date('now', 'localtime') THEN 1" +
            "    ELSE 0" +
            " END AS locked" +
            " FROM job_orders jo " +
            " JOIN customers cu ON jo.customer_id = cu.id LEFT JOIN job_order_payments pa ON jo.payment_id = pa.id " +
            " WHERE " +
            " cu.id = :customerId AND pa.id IS NULL AND jo.deleted_at IS NULL AND jo.void_date IS NULL AND pa.deleted_at IS NULL")
    fun getUnpaidJobOrdersAsLiveData(customerId: UUID): LiveData<List<JobOrderListItem>>
}