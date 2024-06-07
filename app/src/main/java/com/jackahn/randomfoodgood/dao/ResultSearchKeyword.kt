package com.jackahn.randomfoodgood.dao


// 장소명, 주소, 좌표만 받는 클래스
data class ResultSearchKeyword(
    var documents: List<Place>          // 검색 결과
)
data class Place(
    var place_name: String,             // 장소명
    var address_name: String,           // 지번 주소
    var road_address_name: String,      // 도로명 주소
    var x: String,                      // X 좌표값, longitude
    var y: String,                      // Y 좌표값, latitude
)
