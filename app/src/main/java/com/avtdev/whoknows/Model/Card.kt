package com.avtdev.whoknows.Model

import androidx.annotation.IntegerRes

class Card (
    val path: String,
    val cardValue: Int,
    val isCourt: Boolean = false,
    val suit: String,
    val colour: String
){
    fun isPair(): Boolean{
        return cardValue % 2 == 0
    }
}