package com.jackahn.randomfoodgood.service

import android.provider.ContactsContract.Data
import com.jackahn.randomfoodgood.dao.User
import com.jackahn.randomfoodgood.dto.LoginDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {


    @POST("api/user/login")
    fun checkLogin(@Body loginDto: LoginDto): Call<User>

    @POST("api/user")
    fun addUser(@Body user: User): Call<User>
}