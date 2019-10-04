package com.avtdev.whoknows.Model

import com.avtdev.whoknows.Services.Constants
import java.lang.Exception
import kotlin.random.Random

class Question (
    val fullQuestion: String,
    val card: Card,
    var leftCorrect: Boolean
) {
    var question1: String?
    var question2: String?

    init {
        try {
            val questions = fullQuestion.split(" || ")
            if (Random.nextBoolean()) {
                question1 = questions[0]
                question2 = questions[1]
            } else {
                question1 = questions[1]
                question2 = questions[0]
                leftCorrect = !leftCorrect
            }
        }catch (ex: Exception){
            println(fullQuestion)
            throw ex
        }
    }

    fun isCorrect(leftButton: Boolean): Boolean{
        return this.leftCorrect == leftButton
    }
}