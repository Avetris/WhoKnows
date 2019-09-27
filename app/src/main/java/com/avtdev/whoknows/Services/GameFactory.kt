package com.avtdev.whoknows.Services

import android.content.Context
import com.avtdev.whoknows.Model.Card
import com.avtdev.whoknows.Model.Question
import com.avtdev.whoknows.Services.Constants.Companion.ALL_QUESTIONS
import com.avtdev.whoknows.Services.Constants.Companion.COURTS
import com.avtdev.whoknows.Services.Constants.Companion.QUESTION
import com.avtdev.whoknows.Services.Constants.Companion.SUITS
import kotlin.random.Random

class GameFactory (
    var mGameType: Constants.Companion.GameType = Constants.Companion.GameType.NORMAL
){
    val mCardList = ArrayList<Card>()

    init {
        mCardList.clear()
        for(suit in SUITS){
            for(i in 1..13){
                if(i <= 10){
                    mCardList.add(Card(path = "${suit}_${i}", cardValue = i, suit = suit))
                }else{
                    mCardList.add(Card(path = "${suit}_${COURTS[i - 11]}", cardValue = i, isCourt = true, suit = suit))
                }
            }
        }
    }

    fun nextQuestion(context: Context, card: Card?): Question{
        val auxCardList = if(card != null) mCardList.minus(card) else mCardList
        val nextCard = auxCardList[Random.nextInt(auxCardList.size + 1)]

        val auxQuestionList = if(card == null) QUESTION else ALL_QUESTIONS
        val nextQuestion = Question(
            fullQuestion = context.resources.getString(auxQuestionList[Random.nextInt(auxQuestionList.size + 1)]),
            card = nextCard
        )
        return nextQuestion
    }
}