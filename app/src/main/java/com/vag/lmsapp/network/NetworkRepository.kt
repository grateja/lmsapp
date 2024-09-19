package com.vag.lmsapp.network

import com.vag.lmsapp.network.api.ApiService
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.responses.InventoryLogSyncIds
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.responses.MachineUsageSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import com.vag.lmsapp.room.dao.DaoSync
import com.vag.lmsapp.room.entities.EntityCustomer
import com.vag.lmsapp.room.entities.EntityExpenseFull
import com.vag.lmsapp.room.entities.EntityInventoryLogFull
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import com.vag.lmsapp.room.entities.EntityMachineUsageFull
import com.vag.lmsapp.room.entities.EntityShop
import com.vag.lmsapp.room.entities.SanctumToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository

@Inject
constructor(
    private val dao: ApiService,
    private val daoSync: DaoSync
) {
    suspend fun link(shop: EntityShop, ownerId: UUID, token: String): Result<SanctumToken> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<SanctumToken> = dao.link(shop, ownerId.toString(), "Bearer $token")
                if (response.isSuccessful) {
                    // Return the successful result
                    Result.success(response.body()!!)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: IOException) {
                // Handle network errors
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: HttpException) {
                // Handle HTTP errors
                Result.failure(Exception("HTTP error: ${e.message}"))
            } catch (e: Exception) {
                // Handle other errors
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendJobOrder(jobOrderWithItems: List<EntityJobOrderWithItems>, shopId: UUID, token: String): Result<JobOrderSyncIds> {
        return withContext(Dispatchers.IO) {
            val response = dao.sendJobOrder(jobOrderWithItems, shopId.toString(), "Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncJobOrders(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendPayment(paymentRequestBody: List<PaymentRequestBody>, shopId: UUID, token: String): Result<PaymentSynIds> {
        return withContext(Dispatchers.IO) {
            val response: Response<PaymentSynIds> = dao.sendPayment(paymentRequestBody, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncPayment(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendBulkPayload(setupRequestBody: SetupRequestBody, shopId: UUID, token: String): Result<SetupSyncIds> {
        return withContext(Dispatchers.IO) {
            val response: Response<SetupSyncIds> = dao.sendSetup(setupRequestBody, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncSetup(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendCustomer(customer: EntityCustomer, shopId: UUID, token: String): Result<List<UUID>> {
        return sendCustomer(listOf(customer), shopId, token)
    }
    suspend fun sendCustomer(customers: List<EntityCustomer>, shopId: UUID, token: String): Result<List<UUID>> {
        return withContext(Dispatchers.IO) {
            val response = dao.sendCustomer(customers, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncCustomers(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendMachineUsage(machineUsageFull: List<EntityMachineUsageFull>, shopId: UUID, token: String): Result<MachineUsageSyncIds> {
        return withContext(Dispatchers.IO) {
            val response = dao.sendMachineActivation(machineUsageFull, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncMachineUsage(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendExpense(expense: List<EntityExpenseFull>, shopId: UUID, token: String): Result<List<UUID>> {
        return withContext(Dispatchers.IO) {
            val response = dao.sendExpense(expense, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncExpenses(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun sendInventoryLog(expense: List<EntityInventoryLogFull>, shopId: UUID, token: String): Result<InventoryLogSyncIds> {
        return withContext(Dispatchers.IO) {
            val response = dao.sendInventoryLog(expense, shopId.toString(), "Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()!!
                daoSync.syncInventoryLog(body)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
            }
        }
    }

    suspend fun getUnSyncJobOrders(limit: Int) = daoSync.getUnSyncJobOrders(limit)
    suspend fun getUnSyncMachineUsage(limit: Int) = daoSync.getUnSyncMachineUsage(limit)
    suspend fun getUnSyncInventoryLog(limit: Int) = daoSync.getUnSyncInventoryLog(limit)
    suspend fun getUnSyncPayment(limit: Int) = daoSync.getUnSyncPayment(limit)
    suspend fun getUnSyncExpenses(limit: Int) = daoSync.getUnSyncExpenses(limit)
    suspend fun getUnSyncCustomers() = daoSync.getUnSyncCustomers()

    suspend fun jobOrderCount() = daoSync.jobOrderCount()
    suspend fun machineUsageCount() = daoSync.machineUsageCount()
    suspend fun inventoryLogCount() = daoSync.inventoryLogCount()
    suspend fun jobPaymentCount() = daoSync.paymentCount()
    suspend fun expensesCount() = daoSync.expensesCount()
    suspend fun customerCount() = daoSync.customerCount()

    fun jobOrderCountAsLiveData() = daoSync.jobOrderCountAsLiveData()
    fun machineUsageCountAsLiveData() = daoSync.machineUsageCountAsLiveData()
    fun inventoryLogCountAsLiveData() = daoSync.inventoryLogCountAsLiveData()
    fun jobPaymentCountAsLiveData() = daoSync.paymentCountAsLiveData()
    fun expensesCountAsLiveData() = daoSync.expensesCountAsLiveData()
    fun customerCountAsLiveData() = daoSync.customerCountAsLiveData()

    fun getAllCounts() = daoSync.getAllCounts()
}