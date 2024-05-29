package com.vag.lmsapp.network

import com.vag.lmsapp.network.dao.BranchDao
import com.vag.lmsapp.network.link.ShopRequestBody
import com.vag.lmsapp.room.entities.BaseEntity
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
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
    private val dao: BranchDao
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

    suspend fun sendJobOrder(jobOrderWithItems: EntityJobOrderWithItems, shopId: UUID, token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                println("send transaction")
//                val body = SyncBody(jobText)
                val response: Response<String> = dao.sendJobOrder(jobOrderWithItems, shopId.toString(), "Bearer $token")
                println("no error")

                if (response.isSuccessful) {
                    // Return the successful result
                    Result.success(response.body()!!)
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

    suspend fun sendPayment(paymentRequestBody: PaymentRequestBody, shopId: UUID, token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                println("send payment")
//                val body = SyncBody(jobText)
                val response: Response<String> = dao.sendPayment(paymentRequestBody, shopId.toString(), "Bearer $token")
                println("no error")

                if (response.isSuccessful) {
                    // Return the successful result
                    Result.success(response.body()!!)
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

    suspend fun sendBulkPayload(bulkPayload: BulkPayload, shopId: UUID, token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                println("send payment")
//                val body = SyncBody(jobText)
                val response: Response<String> = dao.sendBulkPayload(bulkPayload, shopId.toString(), "Bearer $token")
                println("no error")

                if (response.isSuccessful) {
                    // Return the successful result
                    Result.success(response.body()!!)
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
}