package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.app.reports.calendar.DailyResult
import com.vag.lmsapp.app.reports.monthly_report.MonthPriceCountAggregate
import com.vag.lmsapp.app.reports.monthly_report.MonthlyResult
import com.vag.lmsapp.app.reports.yearly_report.YearlyResult

@Dao
abstract class DaoYearlyReport {
    @Query("""
        SELECT year,
            SUM(newCustomerCount) AS newCustomerCount,
            SUM(jobOrderCount) AS jobOrderCount,
            SUM(jobOrderAmount) AS jobOrderAmount,
            SUM(expensesAmount) AS expensesAmount
        FROM (
            -- Job Orders Query
            SELECT strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS year,
                0 AS newCustomerCount,
                COUNT(*) AS jobOrderCount,
                SUM(discounted_amount) AS jobOrderAmount,
                0 AS expensesAmount
            FROM job_orders
            WHERE deleted = 0
                AND void_date IS NULL
            GROUP BY year

            UNION ALL

            -- Customers Query
            SELECT strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS year,
                COUNT(*) AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                0 AS expensesAmount
            FROM customers
            WHERE deleted = 0
            GROUP BY year

            UNION ALL

            -- Expenses Query
            SELECT strftime('%Y', datetime(created_at / 1000, 'unixepoch', 'localtime')) AS year,
                0 AS newCustomerCount,
                0 AS jobOrderCount,
                0 AS jobOrderAmount,
                SUM(amount) AS expensesAmount
            FROM expenses
            WHERE deleted = 0
            GROUP BY year
        )
        GROUP BY year
        ORDER BY year
""")
    abstract fun yearly(): LiveData<List<YearlyResult>>
}