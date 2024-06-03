package com.jackahn.randomfoodgood.util

import com.jackahn.randomfoodgood.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {
    var userUtil = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserService::class.java)
}