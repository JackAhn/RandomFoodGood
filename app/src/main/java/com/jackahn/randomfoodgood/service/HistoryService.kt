package com.jackahn.randomfoodgood.service

import android.provider.ContactsContract.Data
import com.jackahn.randomfoodgood.dao.History
import com.jackahn.randomfoodgood.dao.User
import com.jackahn.randomfoodgood.dto.HistoryDto
import com.jackahn.randomfoodgood.dto.LoginDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface HistoryService {
    @POST("api/history")
    fun addHistory(@Body histoy: HistoryDto): Call<History>

    @GET("api/history/{userId}")
    fun getHistory(@Path("userId")id: Int): Call<List<History>>

    @DELETE("api/history/{userId}")
    fun deleteHistory(@Path("userId") userId: Long): Call<Int>
}