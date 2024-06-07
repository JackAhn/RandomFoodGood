package com.jackahn.randomfoodgood.dao

import java.io.Serializable

data class PlaceResult(
    var place_name: String,             // 장소명
    var road_address_name: String,      // 도로명 주소
    var distance: String, // 거리
    var phone: String, // 전화번호
    var place_url: String // 상세 정보 링크
): Serializable
