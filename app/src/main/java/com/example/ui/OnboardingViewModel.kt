package com.example.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class OnboardingStep {
    Welcome,
    QuizIntent,
    QuizMood,
    QuizTherapy,
    FirstConversation,
    PrivacyChoice,
    MagicLink,
    Paywall,
    Confirmation
}

data class OnboardingState(
    val selectedIntentions: List<String> = emptyList(),
    val currentMood: String? = null,
    val therapyExperience: String? = null,
    val firstConversationCompleted: Boolean = false,
    val accountType: String? = null,
    val email: String? = null,
    val currentStep: OnboardingStep = OnboardingStep.Welcome
)

class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun setStep(step: OnboardingStep) {
        _state.update { it.copy(currentStep = step) }
    }

    fun toggleIntention(intention: String) {
        _state.update {
            val list = it.selectedIntentions.toMutableList()
            if (list.contains(intention)) {
                list.remove(intention)
            } else {
                list.add(intention)
            }
            it.copy(selectedIntentions = list)
        }
    }

    fun setMood(mood: String) {
        _state.update { it.copy(currentMood = mood) }
    }

    fun setTherapyExperience(exp: String) {
        _state.update { it.copy(therapyExperience = exp) }
    }

    fun setAccountType(type: String) {
        _state.update { it.copy(accountType = type) }
    }

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }
}
