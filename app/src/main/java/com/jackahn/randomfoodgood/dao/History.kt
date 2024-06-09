package com.jackahn.randomfoodgood.dao

import java.io.Serializable
import java.util.Date

data class History(
    var id: Int,
    var userId: Int,
    var foodName: String,
    var foodPlace: String,
    var foodPhone: String,
    var created: Date
): Serializable
