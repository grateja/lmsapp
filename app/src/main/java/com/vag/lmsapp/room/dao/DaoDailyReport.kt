package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.reports.daily_report.PriceCountAggregate
import com.vag.lmsapp.app.reports.daily_report.expenses.DailyReportExpenses
import com.vag.lmsapp.app.reports.daily_report.job_order.DailyReportJobOrder
import com.vag.lmsapp.app.reports.daily_report.job_order_items.DailyReportJobOrderItemDetails
import com.vag.lmsapp.app.reports.daily_report.job_order_paid.DailyReportJobOrderPayment
import com.vag.lmsapp.app.reports.daily_report.job_order_paid.DailyReportJobOrderPaymentSummary
import com.vag.lmsapp.app.reports.daily_report.machine_usage.DailyReportMachineUsageSummary
import com.vag.lmsapp.model.EnumServiceType
import java.time.LocalDate

@Dao
abstract class DaoDailyReport {
//    @Query("""
//        SELECT
//            COUNT(*) as created_count,
//            SUM(discounted_amount) as total_amount,
//            SUM(CASE WHEN DATE(customers.created_at/1000, 'unixepoch', 'localtime') = DATE(jo.created_at/1000, 'unixepoch', 'localtime') THEN 1 ELSE 0 END) as new_customer,
//            SUM(CASE WHEN DATE(customers.created_at/1000, 'unixepoch', 'localtime') <> DATE(jo.created_at/1000, 'unixepoch', 'localtime') THEN 1 ELSE 0 END) as returning_customer,
//            SUM(CASE WHEN discount_in_peso > 0 THEN 1 ELSE 0 END) as discounted_jo_count,
//            SUM(CASE WHEN discount_in_peso > 0 THEN discounted_amount ELSE 0 END) as discounted_jo_amount
//        FROM job_orders jo
//        LEFT JOIN customers ON jo.customer_id = customers.id
//        WHERE
//            DATE(jo.created_at / 1000, 'unixepoch', 'localtime') = :date
//            AND void_by IS NULL AND jo.deleted = 0
//    """)
//    abstract fun jobOrder(date: LocalDate, dateTo: LocalDate): LiveData<DailyReportJobOrder>
    @Query("""
        SELECT 
            COUNT(*) as created_count,
            SUM(discounted_amount) as total_amount,
            SUM(CASE WHEN DATE(customers.created_at/1000, 'unixepoch', 'localtime') = DATE(jo.created_at/1000, 'unixepoch', 'localtime') THEN 1 ELSE 0 END) as new_customer,
            SUM(CASE WHEN DATE(customers.created_at/1000, 'unixepoch', 'localtime') <> DATE(jo.created_at/1000, 'unixepoch', 'localtime') THEN 1 ELSE 0 END) as returning_customer,
            SUM(CASE WHEN discount_in_peso > 0 THEN 1 ELSE 0 END) as discounted_jo_count,
            SUM(CASE WHEN discount_in_peso > 0 THEN discounted_amount ELSE 0 END) as discounted_jo_amount
        FROM job_orders jo
        LEFT JOIN customers ON jo.customer_id = customers.id
        WHERE
            DATE(jo.created_at / 1000, 'unixepoch', 'localtime') BETWEEN :date AND :dateTo
            AND void_by IS NULL AND jo.deleted = 0
    """)
    abstract fun jobOrder(date: LocalDate, dateTo: LocalDate): LiveData<DailyReportJobOrder>

    @Query("""
        SELECT 
        CASE 
            WHEN payment_method = 1 THEN 'CASH'
            WHEN payment_method = 2 THEN cashless_provider
            WHEN payment_method = 3 THEN 'CASH + ' || cashless_provider
        END AS provider,
        COUNT(*) AS count,
        SUM(amount_due) AS amount
    FROM job_order_payments
        WHERE
            DATE(created_at / 1000, 'unixepoch', 'localtime') = :date
            AND deleted = 0
    GROUP BY 
        CASE 
            WHEN payment_method = 1 THEN 'CASH'
            WHEN payment_method = 2 THEN cashless_provider
            WHEN payment_method = 3 THEN 'CASH + ' || cashless_provider
        END
    """)
    abstract fun jobOrderPayment(date: LocalDate): LiveData<List<DailyReportJobOrderPayment>>

    @Query("""
        SELECT 
            SUM(CASE WHEN e.payment_method = 1 THEN 1 ELSE 0 END) AS cash_count,
            SUM(CASE WHEN e.payment_method = 1 THEN e.amount_due ELSE 0 END) AS cash_amount,
            SUM(CASE WHEN e.payment_method = 2 THEN 1 ELSE 0 END) AS cashless_count,
            SUM(CASE WHEN e.payment_method = 2 THEN e.cashless_amount ELSE 0 END) AS cashless_amount,
            SUM(e.amount_due) as total_amount,
            COUNT(*) as total_count
        FROM job_order_payments e
        WHERE
            DATE(e.created_at / 1000, 'unixepoch', 'localtime') = :date
            AND deleted = 0
    """)
    abstract fun jobOrderPaymentSummary(date: LocalDate): LiveData<DailyReportJobOrderPaymentSummary>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price * quantity) as price
        FROM job_order_services
        WHERE 
            svc_service_type = :serviceType
            AND DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemServices(date: LocalDate, serviceType: EnumServiceType): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price * quantity) as price
        FROM job_order_extras
        WHERE 
            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemExtraServices(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price * quantity) as price
        FROM job_order_products
        WHERE 
            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemProducts(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(distance) as count,
            SUM(discounted_price) as price
        FROM job_order_delivery_charges
        WHERE 
            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemDelivery(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT 
            jos.svc_machine_type, jos.svc_service_type,
            COUNT(*) as cycles,
            jos.svc_minutes as minutes
        FROM machine_usages mu
            LEFT JOIN machines m ON m.id = mu.machine_id
            LEFT JOIN job_order_services jos ON jos.id = mu.job_order_service_id
        WHERE
            DATE(mu.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND mu.deleted = 0
        GROUP BY jos.svc_machine_type, jos.svc_service_type 
    """)
    abstract fun machineUsageSummary(date: LocalDate): LiveData<List<DailyReportMachineUsageSummary>>

    @Query("""
        SELECT
            remarks, amount
        FROM expenses
        WHERE
            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND deleted = 0
        ORDER BY remarks
    """)
    abstract fun expenses(date: LocalDate): LiveData<List<DailyReportExpenses>>

    @Query("""
        SELECT
            COUNT(*) AS count,
            SUM(discounted_amount) AS price
        FROM job_orders
        WHERE void_by IS NULL AND deleted = 0 AND payment_id IS NULL
    """)
    abstract fun unpaidJobOrders(): LiveData<PriceCountAggregate>

    @Query("""
        SELECT jo.id AS job_order_id,
            jo.job_order_number,
            cu.name AS customer_name,
            jos.service_name AS item_name,
            jos.quantity,
            jos.discounted_price,
            jo.created_at
        FROM job_order_services jos
            JOIN job_orders jo ON jo.id = jos.job_order_id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE
            jos.svc_service_type = :serviceType
            AND DATE(jo.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND jos.deleted = 0 AND jos.void = 0 
            AND jo.deleted = 0 AND jo.void_by IS NULL
    """)
    abstract suspend fun getJobOrderItemDetailsServices(date: LocalDate, serviceType: EnumServiceType): List<DailyReportJobOrderItemDetails>

    @Query("""
        SELECT jo.id AS job_order_id,
            jo.job_order_number,
            cu.name AS customer_name,
            joe.extras_name AS item_name,
            joe.quantity,
            joe.discounted_price,
            jo.created_at
        FROM job_order_extras joe
            JOIN job_orders jo ON jo.id = joe.job_order_id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE
            DATE(jo.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND joe.deleted = 0 AND joe.void = 0
            AND jo.deleted = 0 AND jo.void_by IS NULL
    """)
    abstract suspend fun getJobOrderItemDetailsExtras(date: LocalDate): List<DailyReportJobOrderItemDetails>

    @Query("""
        SELECT jo.id AS job_order_id,
            jo.job_order_number,
            cu.name AS customer_name,
            jop.product_name AS item_name,
            jop.quantity,
            jop.discounted_price,
            jo.created_at
        FROM job_order_products jop
            JOIN job_orders jo ON jo.id = jop.job_order_id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE
            DATE(jo.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND jop.deleted = 0 AND jop.void = 0
            AND jo.deleted = 0 AND jo.void_by IS NULL
    """)
    abstract suspend fun getJobOrderItemDetailsProducts(date: LocalDate): List<DailyReportJobOrderItemDetails>

    @Query("""
        SELECT jo.id AS job_order_id,
            jo.job_order_number,
            cu.name AS customer_name,
            (CASE
                WHEN jodc.vehicle = 1 THEN 'Trike Pedal'
                WHEN jodc.vehicle = 2 THEN 'Trike Electric'
                WHEN jodc.vehicle = 3 THEN 'Motorcycle'
                WHEN jodc.vehicle = 4 THEN 'Tricycle'
                WHEN jodc.vehicle = 5 THEN 'Sedan'
                WHEN jodc.vehicle = 6 THEN 'MPV'
                WHEN jodc.vehicle = 7 THEN 'Small Van'
                ELSE ''
            END) AS item_name,
            jodc.distance AS quantity,
            jodc.discounted_price,
            jo.created_at
        FROM job_order_delivery_charges jodc
            JOIN job_orders jo ON jo.id = jodc.id
            JOIN customers cu ON cu.id = jo.customer_id
        WHERE
            DATE(jo.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND jodc.deleted = 0 AND jodc.void = 0
            AND jo.deleted = 0 AND jo.void_by IS NULL
    """)
    abstract suspend fun getJobOrderItemDetailsDeliveries(date: LocalDate): List<DailyReportJobOrderItemDetails>
}