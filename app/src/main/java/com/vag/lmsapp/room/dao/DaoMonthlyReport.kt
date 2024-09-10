package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.reports.monthly_report.MonthPriceCountAggregate

@Dao
abstract class DaoMonthlyReport {
    @Query("""
        SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch')) AS month,
            COUNT(*) AS count,
            SUM(subtotal) AS price
        FROM job_orders
        WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch')) = :year
        GROUP BY month
        ORDER BY month
    """)
    abstract suspend fun jobOrders(year: String): List<MonthPriceCountAggregate>

    @Query("""
        SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch')) AS month,
            COUNT(*) AS count,
            0 AS price
        FROM customers
        WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch')) = :year
        GROUP BY month
        ORDER BY month
    """)
    abstract suspend fun customers(year: String): List<MonthPriceCountAggregate>

    @Query("""
        SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch')) AS month,
            COUNT(*) AS count,
            SUM(amount) AS price
        FROM expenses
        WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch')) = :year
        GROUP BY month
        ORDER BY month
    """)
    abstract suspend fun expenses(year: String): List<MonthPriceCountAggregate>
}