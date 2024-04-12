package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.room.entities.*
import java.time.LocalDate

@Dao
abstract class DaoJobOrderPickupDelivery {
    @Query("SELECT" +
            "     vehicle," +
            "     COUNT(*) as count" +
            " FROM job_order_delivery_charges" +
            " WHERE (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom" +
            "     OR ( :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime')" +
            "          BETWEEN :dateFrom AND :dateTo))" +
            "     AND (deleted = 0)" +
            "     AND (void = 0)" +
            " GROUP BY vehicle")
    abstract fun getDashboard(dateFrom: LocalDate, dateTo: LocalDate?) : LiveData<List<EntityJobOrderPickupDeliveryAggrResult>>
}