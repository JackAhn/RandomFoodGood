package com.jackahn.randomfoodgood.dto

import com.jackahn.randomfoodgood.dao.PlaceResult

interface OnDataPass {
    fun onDataPass(data: ArrayList<PlaceResult>)
}