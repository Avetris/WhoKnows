package com.avtdev.whoknows.Services

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.core.text.HtmlCompat
import com.avtdev.whoknows.Model.Card
import com.avtdev.whoknows.Model.Question
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants.Companion.ALL_QUESTIONS
import com.avtdev.whoknows.Services.Constants.Companion.COLOURS
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
            val colour = if(suit in listOf("club", "spade")) COLOURS[0] else COLOURS[1]
            for(i in 1..13){
                if(i <= 10){
                    mCardList.add(Card(path = "${suit}_${i}", cardValue = i, suit = suit, colour = colour))
                }else{
                    mCardList.add(Card(path = "${suit}_${COURTS[i - 11]}", cardValue = i, isCourt = true, suit = suit, colour = colour))
                }
            }
        }
    }

    fun nextQuestion(context: Context, card: Card?): Question{
        val auxCardList = if(card != null) mCardList.minus(card) else mCardList
        val nextCard = auxCardList[Random.nextInt(auxCardList.size)]

        val auxQuestionList = if(card == null) QUESTION else ALL_QUESTIONS

        val questionId = auxQuestionList[Random.nextInt(auxQuestionList.size)]
        var fullQuestion = ""
        var leftCorrect = true
        when(questionId){
            R.string.question_pair_odd -> {
                fullQuestion = context.getString(questionId)
                leftCorrect = nextCard.isPair()
            }
            R.string.question_greater_num -> {
                val num = Random.nextInt(11) + 1
                if(num > 10){
                    if(num == 11){
                        fullQuestion = String.format(context.getString(questionId), "J")
                    }else{
                        fullQuestion = String.format(context.getString(questionId), "Q")
                    }
                }else{
                    fullQuestion = String.format(context.getString(questionId), num )
                }
                leftCorrect = nextCard.cardValue >= num
            }
            R.string.question_smaller_num -> {
                val num = Random.nextInt(11) + 2
                if(num > 10){
                    if(num == 11){
                        fullQuestion = String.format(context.getString(questionId), "J")
                    }else if(num == 12){
                        fullQuestion = String.format(context.getString(questionId), "Q")
                    }else{
                        fullQuestion = String.format(context.getString(questionId), "K")
                    }
                }else{
                    fullQuestion = String.format(context.getString(questionId), num )
                }
                leftCorrect = nextCard.cardValue <= num
            }
            R.string.question_equal_num -> {
                val num = Random.nextInt(12) + 1
                if(num > 10){
                    if(num == 11){
                        fullQuestion = String.format(context.getString(questionId), "J")
                    }else if(num == 12){
                        fullQuestion = String.format(context.getString(questionId), "Q")
                    }else{
                        fullQuestion = String.format(context.getString(questionId), "K")
                    }
                }else{
                    fullQuestion = String.format(context.getString(questionId), num )
                }
                leftCorrect = nextCard.cardValue == num
            }
            R.string.question_court -> {
                fullQuestion = context.getString(questionId)
                leftCorrect = nextCard.isCourt
            }
            R.string.question_two_suits -> {
                val suits = getSuits(context)
                leftCorrect = false

                val s1 = suits.random()
                if(suits.indexOf(s1) == SUITS.indexOf(nextCard.suit)){
                    leftCorrect = true
                }
                suits.remove(s1)
                val s2 = suits.random()
                if(suits.indexOf(s2) == SUITS.indexOf(nextCard.suit)){
                    leftCorrect = true
                }
                suits.remove(s2)

                fullQuestion = String.format(context.getString(questionId), s1, s2, suits.get(0), suits.get(1))
            }
            R.string.question_four_suits -> {
                val suits = getSuits(context)

                val s1 = suits.random()
                if(suits.indexOf(s1) == SUITS.indexOf(nextCard.suit)){
                    leftCorrect = false
                }else{
                    leftCorrect = true
                }
                suits.remove(s1)

                fullQuestion = String.format(context.getString(questionId), suits.get(0), suits.get(1), suits.get(2), s1)
            }
            R.string.question_greater -> {
                if(card?.cardValue!! < 13){
                    fullQuestion = context.getString(questionId)
                    leftCorrect = nextCard.cardValue > card.cardValue
                }else{
                    fullQuestion = context.getString(R.string.question_smaller)
                    leftCorrect = nextCard.cardValue < card.cardValue
                }
            }
            R.string.question_smaller -> {
                if(card?.cardValue!! > 1){
                    fullQuestion = context.getString(questionId)
                    leftCorrect = nextCard.cardValue < card.cardValue
                }else{
                    fullQuestion = context.getString(R.string.question_greater)
                    leftCorrect = nextCard.cardValue > card.cardValue
                }
            }
            R.string.question_equal -> {
                fullQuestion = context.getString(questionId)
                leftCorrect = nextCard.cardValue == card?.cardValue
            }
            R.string.question_same_suits -> {
                fullQuestion = context.getString(questionId)
                leftCorrect = nextCard.suit == card?.suit
            }
            R.string.question_color -> {
                fullQuestion = context.getString(
                    questionId,
                    "<span style='color:black'>${context.getString(R.string.black)}</span>",
                    "<span style='color:red'>${context.getString(R.string.red)}</span>")
                leftCorrect = nextCard.colour == COLOURS[0]
            }
        }

        return Question(
            fullQuestion = fullQuestion,
            leftCorrect = leftCorrect,
            card = nextCard
        )
    }

    private fun getSuits(context: Context): ArrayList<CharSequence> {
        val suits = ArrayList<CharSequence>()
        val packageName = context.packageName
        SUITS.forEach{
            val id = context.resources.getIdentifier(it, "string", packageName)
            var color = "black"
            if(id in listOf(R.string.diamond, R.string.heart)){
                color = "red"
            }
            suits.add("<span style='color:$color'>${context.getString(id)}</span>")
        }
        return suits
    }
}