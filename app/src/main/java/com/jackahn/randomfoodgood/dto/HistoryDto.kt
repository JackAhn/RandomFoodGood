package com.jackahn.randomfoodgood.dto

import java.io.Serializable
import java.util.Date

data class HistoryDto(
    var id: Int,
    var userId: Int,
    var foodName: String,
    var foodPlace: String,
    var foodPhone: String,
    var created: String
): Serializable
