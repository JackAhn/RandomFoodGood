package com.jackahn.randomfoodgood.dao

import java.io.Serializable

data class User(
    var id: Int? = null,
    var socialId: Int? = null,
    var userId: String? = null,
    var userName: String? = null
): Serializable
