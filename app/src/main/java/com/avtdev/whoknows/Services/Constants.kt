package com.avtdev.whoknows.Services

import com.avtdev.whoknows.R

class Constants {
    companion object{
        enum class GameType{
            NORMAL,
            ACCUMULATIVE,
            INVERSE,
            INVERSE_ACCUMULATIVE
        }

        enum class Preferences{
            NAME,
            LANGUAGE,
            ADS_TIME,
            LAST_DATE
        }

        val SUITS = listOf("curl", "heart", "diamond", "spade")
        val COURTS = listOf("jack", "queen", "king")
        val QUESTION: List<Int> = listOf(
            R.string.question_pair_odd,
            R.string.question_greater_num,
            R.string.question_smaller_num,
            R.string.question_equal,
            R.string.question_multiple,
            R.string.question_divisible,
            R.string.question_court,
            R.string.question_two_suits,
            R.string.question_four_suits
        )
        val ALL_QUESTIONS: List<Int> = QUESTION + listOf(
            R.string.question_greater,
            R.string.question_smaller,
            R.string.question_equal
        )
    }
}