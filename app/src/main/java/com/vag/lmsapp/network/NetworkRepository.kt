package com.vag.lmsapp.network

import com.vag.lmsapp.network.api.ApiService
import com.vag.lmsapp.network.requests_body.PaymentRequestBody
import com.vag.lmsapp.network.requests_body.SetupRequestBody
import com.vag.lmsapp.network.responses.InventoryLogSyncIds
import com.vag.lmsapp.network.responses.JobOrderSyncIds
import com.vag.lmsapp.network.responses.MachineUsageSyncIds
import com.vag.lmsapp.network.responses.PaymentSynIds
import com.vag.lmsapp.network.responses.SetupSyncIds
import com.vag.lmsapp.network.responses.UuidSyncId
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

    suspend fun sendJobOrder(jobOrderWithItems: EntityJobOrderWithItems, shopId: UUID, token: String): Result<JobOrderSyncIds> {
        return withContext(Dispatchers.IO) {
            try {
                println("send transaction")
//                val body = SyncBody(jobText)
                val response: Response<JobOrderSyncIds> = dao.sendJobOrder(jobOrderWithItems, shopId.toString(), "Bearer $token")

                if (response.isSuccessful) {
                    val body = response.body()!!
                    daoSync.syncJobOrder(body)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
//            } catch (e: IOException) {
//                // Handle network errors
//                Result.failure(Exception("Network error: ${e.message}"))
//            } catch (e: HttpException) {
//                // Handle HTTP errors
//                Result.failure(Exception("HTTP error: ${e.message}"))
            } catch (e: Exception) {
                println("error occured")
                e.printStackTrace()
                // Handle other errors
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendPayment(paymentRequestBody: PaymentRequestBody, shopId: UUID, token: String): Result<PaymentSynIds> {
        return withContext(Dispatchers.IO) {
            try {
                println("send payment")
//                val body = SyncBody(jobText)
                val response: Response<PaymentSynIds> = dao.sendPayment(paymentRequestBody, shopId.toString(), "Bearer $token")
                println("no error")

                if (response.isSuccessful) {
                    val body = response.body()!!
                    // Return the successful result
                    daoSync.syncPayment(body)
                    println("payment sync")
                    println(body)
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                println("error occured")
                e.printStackTrace()
                // Handle other errors
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendBulkPayload(setupRequestBody: SetupRequestBody, shopId: UUID, token: String): Result<SetupSyncIds> {
        return withContext(Dispatchers.IO) {
            try {
                println("send payment")
//                val body = SyncBody(jobText)
                val response: Response<SetupSyncIds> = dao.sendBulkPayload(setupRequestBody, shopId.toString(), "Bearer $token")
                println("no error")

                if (response.isSuccessful) {
                    val body = response.body()!!
                    println(body)
                    daoSync.syncSetup(body)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                println("error occured")
                e.printStackTrace()
                // Handle other errors
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendCustomer(customer: EntityCustomer, shopId: UUID, token: String): Result<UuidSyncId> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dao.sendCustomer(customer, shopId.toString(), "Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()!!
                    println(body)
                    daoSync.syncCustomer(body.id)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendMachineUsage(machineUsageFull: EntityMachineUsageFull, shopId: UUID, token: String): Result<MachineUsageSyncIds> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dao.sendMachineActivation(machineUsageFull, shopId.toString(), "Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()!!
                    println(body)
                    daoSync.syncMachineUsage(body)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendExpense(expense: EntityExpenseFull, shopId: UUID, token: String): Result<UuidSyncId> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dao.sendExpense(expense, shopId.toString(), "Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()!!
                    println(body)
                    daoSync.syncExpense(body.id)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }

    suspend fun sendInventoryLog(expense: EntityInventoryLogFull, shopId: UUID, token: String): Result<InventoryLogSyncIds> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dao.sendInventoryLog(expense, shopId.toString(), "Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()!!
                    println(body)
                    daoSync.syncInventoryLog(body)
                    // Return the successful result
                    Result.success(body)
                } else {
                    // Parse the error response if possible
                    // val errorResponse = parseError(response)
                    Result.failure(Exception("Error ${response.code()}: ${response.message() ?: "Unknown error"}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("Unknown error: ${e.message}"))
            }
        }
    }
}