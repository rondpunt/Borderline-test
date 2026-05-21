package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.LpiQuestions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TestViewModel : ViewModel() {

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _answers = MutableStateFlow(IntArray(LpiQuestions.questions.size) { 0 })
    val answers: StateFlow<IntArray> = _answers.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()
    
    fun answerQuestion(score: Int) {
        val currentIndex = _currentQuestionIndex.value
        _answers.update { currentAnswers ->
            val newAnswers = currentAnswers.copyOf()
            newAnswers[currentIndex] = score
            newAnswers
        }

        if (currentIndex < LpiQuestions.questions.size - 1) {
            _currentQuestionIndex.value = currentIndex + 1
        } else {
            _isFinished.value = true
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun resetTest() {
        _currentQuestionIndex.value = 0
        _answers.value = IntArray(LpiQuestions.questions.size) { 0 }
        _isFinished.value = false
    }

    fun calculateTotalScore(): Int {
        return _answers.value.sum()
    }
}
