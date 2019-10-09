package com.avtdev.whoknows.Services

import com.avtdev.whoknows.R

class Constants {
    companion object{
        enum class GameType{
            NORMAL,
            ACCUMULATIVE,
            INVERSE,
            INVERSE_ACCUMULATIVE,
            RANDOM_CARD
        }

        enum class Preferences{
            NAME,
            LANGUAGE,
            ADS_TIME,
            LAST_DATE
        }

        val SUITS = listOf("club", "heart", "diamond", "spade")
        val COLOURS = listOf("black", "red")
        val COURTS = listOf("jack", "queen", "king")
        val QUESTION: List<Int> = listOf(
            R.string.question_pair_odd,
            R.string.question_greater_num,
            R.string.question_smaller_num,
            R.string.question_equal_num,
            R.string.question_court,
            R.string.question_two_suits,
            R.string.question_four_suits,
            R.string.question_color
        )
        val ALL_QUESTIONS: List<Int> = QUESTION + listOf(
            R.string.question_greater,
            R.string.question_smaller,
            R.string.question_equal,
            R.string.question_same_suits
        )

        val INTERSTITIAL_AD_TIMES = 10
    }
}