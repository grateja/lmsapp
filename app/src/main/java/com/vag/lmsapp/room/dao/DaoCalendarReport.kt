package com.vag.lmsapp.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.reports.calendar.DailyResult

@Dao
abstract class DaoCalendarReport {
    @Query("""
        SELECT date, 
            SUM(newCustomerCount) AS newCustomerCount,
            SUM(jobOrderCount) AS jobOrderCount,
            SUM(jobOrderAmount) AS jobOrderAmount,
            SUM(expensesAmount) AS expensesAmount
        FROM (
            -- Job Orders Query
            SELECT strftime('%Y-%m-%d', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS date,
                0 AS newCustomerCount,
                COUNT(*) AS jobOrderCount,
                SUM(discounted_amount) AS jobOrderAmount,
                0 AS expensesAmount
            FROM job_orders
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :monthNumber
                AND deleted = 0
                AND void_date IS NULL
            GROUP BY date

            UNION ALL

            -- Customers Query
            SELECT strftime('%Y-%m-%d', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS date,
                COUNT(*) AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                0 AS expensesAmount
            FROM customers
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :monthNumber
                AND deleted = 0
            GROUP BY date

            UNION ALL

            -- Expenses Query
            SELECT strftime('%Y-%m-%d', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS date,
                0 AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                SUM(amount) AS expensesAmount
            FROM expenses
            WHERE strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :year
                AND strftime('%m', datetime(created_at / 1000, 'unixepoch', 'localtime')) = :monthNumber
                AND deleted = 0
            GROUP BY date
        )
        GROUP BY date
        ORDER BY date
    """)
    abstract suspend fun daily(year: String, monthNumber: String): List<DailyResult>
}