package com.vag.lmsapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.vag.lmsapp.room.entities.EntityJobOrderService
import com.vag.lmsapp.room.entities.EntityJobOrderServiceAggrResult
import java.time.LocalDate

@Dao
abstract class DaoJobOrderService : BaseDao<EntityJobOrderService> {
    @Query("SELECT" +
            "     service_name," +
            "     SUM(quantity) as count," +
            "     svc_machine_type," +
            "     svc_wash_type" +
            " FROM job_order_services" +
            " WHERE (date(created_at / 1000, 'unixepoch', 'localtime') = :dateFrom " +
            "     OR ( :dateTo IS NOT NULL AND date(created_at / 1000, 'unixepoch', 'localtime') " +
            "          BETWEEN :dateFrom AND :dateTo ))" +
            "     AND ( deleted = 0)" +
            "     AND (void = 0)" +
            " GROUP BY service_name, svc_wash_type, svc_machine_type")
    abstract fun getDashboard(dateFrom: LocalDate, dateTo: LocalDate?) : LiveData<List<EntityJobOrderServiceAggrResult>>
}