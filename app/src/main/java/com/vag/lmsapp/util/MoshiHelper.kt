package com.vag.lmsapp.util

import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vag.lmsapp.adapters.ArrayListIntAdapter
import com.vag.lmsapp.adapters.InstantAdapter
import com.vag.lmsapp.adapters.UUIDAdapter
import com.vag.lmsapp.app.lms_live.register.ShopLinkQrCode
import com.vag.lmsapp.room.entities.EntityJobOrder
import com.vag.lmsapp.room.entities.EntityJobOrderWithItems
import java.util.concurrent.Executors
import javax.inject.Inject

class MoshiHelper

@Inject
constructor(private val moshi: Moshi) {
    private val backgroundExecutor = Executors.newSingleThreadExecutor()

    fun decodeShopLinkQrCode(qrCodeString: String): ShopLinkQrCode? {
        val adapter: JsonAdapter<ShopLinkQrCode> = moshi.adapter(ShopLinkQrCode::class.java)
        return try {
            adapter.fromJson(qrCodeString)
        } catch (e: Exception) {
            // Handle parsing exceptions (e.g., invalid JSON format)
            null
        }
    }

    fun encodeJobOrder(jobOrder: EntityJobOrderWithItems?): String {
        val adapter: JsonAdapter<EntityJobOrderWithItems> = moshi.adapter(EntityJobOrderWithItems::class.java)
        return adapter.toJson(jobOrder)
    }

    fun encodeJobOrderLiveData(jobOrder: EntityJobOrderWithItems?): MutableLiveData<String> {
        val liveData = MutableLiveData<String>()
        backgroundExecutor.submit {
            val adapter: JsonAdapter<EntityJobOrderWithItems> = moshi.adapter(EntityJobOrderWithItems::class.java)
            val jsonString: String? = try {
                adapter.toJson(jobOrder)
            } catch (e: Exception) {
                // Handle exceptions during serialization (e.g., null values)
                null
            }

            jsonString?.let {
                liveData.postValue(it)
            }
        }
        return liveData
    }
}
