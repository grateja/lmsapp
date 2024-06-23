package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.daily_report.PriceCountAggregate
import com.vag.lmsapp.app.daily_report.expenses.DailyReportExpenses
import com.vag.lmsapp.app.daily_report.extras.DailyReportExtras
import com.vag.lmsapp.app.daily_report.job_order.DailyReportJobOrder
import com.vag.lmsapp.app.daily_report.job_order_paid.DailyReportJobOrderPayment
import com.vag.lmsapp.app.daily_report.job_order_paid.DailyReportJobOrderPaymentSummary
import com.vag.lmsapp.app.daily_report.machine_usage.DailyReportMachineUsage
import com.vag.lmsapp.app.daily_report.machine_usage.DailyReportMachineUsageSummary
import com.vag.lmsapp.app.daily_report.pickup_delivery.DailyReportPickupDelivery
import com.vag.lmsapp.app.daily_report.products_chemicals.DailyReportProductsChemicals
import com.vag.lmsapp.app.daily_report.products_chemicals.DailyReportProductsChemicalsSummary
import com.vag.lmsapp.app.daily_report.wash_dry_services.DailyReportWashDryService
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumServiceType
import java.time.LocalDate

@Dao
abstract class DaoDailyReport {
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
            DATE(jo.created_at / 1000, 'unixepoch', 'localtime') = :date
            AND void_by IS NULL AND jo.deleted = 0
    """)
    abstract fun jobOrder(date: LocalDate): LiveData<DailyReportJobOrder>

    @Query("""
        SELECT 
        CASE 
            WHEN e.payment_method = 1 THEN 'CASH'
            WHEN e.payment_method = 2 THEN cashless_provider
        END AS provider,
        COUNT(*) AS count,
        SUM(CASE 
            WHEN e.payment_method = 1 THEN e.amount_due
            WHEN e.payment_method = 2 THEN cashless_amount
        END) AS amount
    FROM job_order_payments e
        WHERE
            DATE(e.created_at / 1000, 'unixepoch', 'localtime') = :date
            AND deleted = 0
    GROUP BY 
        CASE 
            WHEN e.payment_method = 1 THEN 'CASH'
            WHEN e.payment_method = 2 THEN cashless_provider
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
            SUM(discounted_price) as price
        FROM job_order_services
        WHERE 
            (svc_machine_type = 1 OR svc_machine_type = 3)
            AND DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemWashServices(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price) as price
        FROM job_order_services
        WHERE 
            (svc_machine_type = 2 OR svc_machine_type = 4)
            AND DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemDryServices(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price) as price
        FROM job_order_extras
        WHERE 
            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND void = 0 AND deleted = 0
    """)
    abstract fun jobOrderItemExtraServices(date: LocalDate): LiveData<PriceCountAggregate>

    @Query("""
        SELECT
            SUM(quantity) as count,
            SUM(discounted_price) as price
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
            jos.svc_machine_type,
            COUNT(*) as cycles,
            jos.svc_minutes as minutes
        FROM machine_usages mu
            LEFT JOIN machines m ON m.id = mu.machine_id
            LEFT JOIN job_order_services jos ON jos.id = mu.job_order_service_id
        WHERE
            DATE(mu.created_at / 1000, 'unixepoch', 'localtime')  = :date
            AND mu.deleted = 0
        GROUP BY svc_machine_type
    """)
    abstract fun machineUsageSummary(date: LocalDate): LiveData<List<DailyReportMachineUsageSummary>>

//    @Query("""
//        SELECT
//            service_name,
//            SUM(quantity) as count,
//            svc_machine_type,
//            svc_wash_type,
//            SUM(discounted_price) as discounted_price
//        FROM job_order_services
//        WHERE
//            (svc_machine_type = 1 OR svc_machine_type = 3)
//            AND DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND void = 0 AND deleted = 0
//        GROUP BY service_id
//    """)
//    abstract fun washServices(date: LocalDate): LiveData<List<DailyReportWashDryService>>

//    @Query("""
//        SELECT
//            service_name,
//            SUM(quantity) as count,
//            svc_machine_type,
//            svc_wash_type,
//            SUM(discounted_price) as discounted_price
//        FROM job_order_services
//        WHERE
//            (svc_machine_type = 2 OR svc_machine_type = 4)
//            AND DATE(created_at / 1000, 'unixepoch', 'localtime') = :date
//            AND void = 0 AND deleted = 0
//        GROUP BY service_id
//    """)
//    abstract fun dryServices(date: LocalDate): LiveData<List<DailyReportWashDryService>>
//
//    @Query("""
//        SELECT extras_name, SUM(quantity) AS count,
//            SUM(discounted_price) as discounted_price
//        FROM job_order_extras
//        WHERE
//            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND void = 0 AND deleted = 0
//        GROUP BY extras_name
//    """)
//    abstract fun extras(date: LocalDate): LiveData<List<DailyReportExtras>>

//    @Query("""
//        SELECT
//            product_name,
//            SUM(quantity) as count,
//            measure_unit,
//            SUM(discounted_price) as discounted_price
//        FROM job_order_products
//        WHERE
//            product_type = :productType
//            AND DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND void = 0 AND deleted = 0
//        GROUP BY product_id
//    """)
//    abstract fun productsChemicals(date: LocalDate, productType: EnumProductType): LiveData<List<DailyReportProductsChemicals>>

//    @Query("""
//        SELECT
//            SUM(CASE WHEN product_type = 1 THEN quantity ELSE 0 END) AS detergent_count,
//            SUM(CASE WHEN product_type = 2 THEN quantity ELSE 0 END) AS fab_con_count,
//            SUM(CASE WHEN product_type = 3 THEN quantity ELSE 0 END) AS others_count,
//            SUM(quantity) AS total_count
//        FROM job_order_products
//        WHERE
//            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND void = 0 AND deleted = 0
//    """)
//    abstract fun productsChemicalsSummary(date: LocalDate): LiveData<DailyReportProductsChemicalsSummary>

//    @Query("""
//        SELECT
//            vehicle,
//            COUNT(*) AS count,
//            SUM(distance) AS distance,
//            SUM(discounted_price) as discounted_price
//        FROM job_order_delivery_charges
//        WHERE
//            DATE(created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND void = 0 AND deleted = 0
//        GROUP BY vehicle
//    """)
//    abstract fun delivery(date: LocalDate): LiveData<List<DailyReportPickupDelivery>>

//    @Query("""
//        SELECT m.machine_number, m.machine_type, COUNT(*) as count
//        FROM machine_usages mu
//        JOIN machines m ON m.id = mu.machine_id
//        WHERE
//            DATE(mu.created_at / 1000, 'unixepoch', 'localtime')  = :date
//            AND mu.deleted = 0
//        GROUP BY mu.machine_id
//        ORDER BY m.stack_order
//    """)
//    abstract fun machineUsages(date: LocalDate): LiveData<List<DailyReportMachineUsage>>
//
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
}