package com.avtdev.whoknows.Model

class Question (
    val fullQuestion: String,
    val card: Card
) {
    var question1: String?
    var question2: String?

    init {
        val questions = fullQuestion.split(" || ")
        question1 = questions[0]
        question2 = questions[1]
    }

    fun isCorrect(leftButton: Boolean): Boolean{
        return true
    }
}