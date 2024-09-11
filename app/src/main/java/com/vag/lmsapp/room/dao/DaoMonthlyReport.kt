package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.reports.calendar.DailyResult
import com.vag.lmsapp.app.reports.monthly_report.MonthPriceCountAggregate
import com.vag.lmsapp.app.reports.monthly_report.MonthlyResult

@Dao
abstract class DaoMonthlyReport {
    @Query("""
        SELECT month, 
            :year AS year,
            SUM(newCustomerCount) AS newCustomerCount,
            SUM(jobOrderCount) AS jobOrderCount,
            SUM(jobOrderAmount) AS jobOrderAmount,
            SUM(expensesAmount) AS expensesAmount
        FROM (
            -- Job Orders Query
            SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS month,
                0 AS newCustomerCount,
                COUNT(*) AS jobOrderCount,
                SUM(discounted_amount) AS jobOrderAmount,
                0 AS expensesAmount
            FROM job_orders
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND deleted = 0
                AND void_date IS NULL
            GROUP BY month

            UNION ALL

            -- Customers Query
            SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS month,
                COUNT(*) AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                0 AS expensesAmount
            FROM customers
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND deleted = 0
            GROUP BY month

            UNION ALL

            -- Expenses Query
            SELECT strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS month,
                0 AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                SUM(amount) AS expensesAmount
            FROM expenses
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND deleted = 0
            GROUP BY month
        )
        GROUP BY month
        ORDER BY month
""")
    abstract suspend fun monthly(year: String): List<MonthlyResult>
}