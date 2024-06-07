package com.jackahn.randomfoodgood.service

import com.jackahn.randomfoodgood.dao.ResultSearchKeyword
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoService {
    @GET("v2/local/search/category.json")    // Keyword.json의 정보를 받아옴
    fun getSearchKeyword(
        @Header("Authorization") key: String,     // 카카오 API 인증키
        @Query("category_group_code") category_group_code: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: String, // 검색을 원하는 질의어
    ): Call<ResultSearchKeyword>    // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김
}