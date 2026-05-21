package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LpiQuestions

enum class AppScreen { Welcome, Test, Result }

@Composable
fun AppNavigation(viewModel: TestViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.Welcome) }
    val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()
    
    LaunchedEffect(isFinished) {
        if (isFinished) {
            currentScreen = AppScreen.Result
        }
    }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "ScreenTransition"
    ) { screen ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxHeight()
            ) {
                when (screen) {
                    AppScreen.Welcome -> WelcomeScreen { currentScreen = AppScreen.Test }
                    AppScreen.Test -> QuestionScreen(viewModel)
                    AppScreen.Result -> ResultScreen(viewModel) {
                        viewModel.resetTest()
                        currentScreen = AppScreen.Welcome
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    var agreed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welkom bij de Zelfevaluatie.",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Deze vragenlijst is bedoeld om inzicht te geven in bepaalde kenmerken gerelateerd aan borderline persoonlijkheidsproblematiek. Het kost ongeveer 5 tot 10 minuten om in te vullen.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Let op: Dit is géén diagnose.",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Deze test is uitsluitend een zelfevaluatie-instrument en biedt geen medische of psychologische diagnose. Raadpleeg bij klachten altijd een gediplomeerde zorgverlener of huisarts.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = agreed,
                        onCheckedChange = { agreed = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ik begrijp dat dit geen vervanging is voor professioneel advies.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .clickable { agreed = !agreed }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        Button(
            onClick = onStartClick,
            enabled = agreed,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Start de test", fontSize = 16.sp)
        }
    }
}

@Composable
fun QuestionScreen(viewModel: TestViewModel) {
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val totalQuestions = LpiQuestions.questions.size
    val currentQuestion = LpiQuestions.questions[currentIndex]
    
    // Provide a way to scroll if screen is too small
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Progress
        LinearProgressIndicator(
            progress = { currentIndex / totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Vraag ${currentIndex + 1} van $totalQuestions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = currentQuestion,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            LpiQuestions.answerOptions.forEach { (text, score) ->
                OutlinedButton(
                    onClick = { viewModel.answerQuestion(score) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text, fontSize = 16.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { viewModel.previousQuestion() },
                enabled = currentIndex > 0
            ) {
                Text("Vorige vraag")
            }
        }
    }
}

@Composable
fun ResultScreen(viewModel: TestViewModel, onRestart: () -> Unit) {
    val totalScore = viewModel.calculateTotalScore()
    // Based on the PDF: cutoff is 126 and more.
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jouw Resultaat",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$totalScore",
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        val explanationText = if (totalScore >= 126) {
            "Je score is relatief hoog vergeleken met de norm. " +
            "Dit betekent niet dat je een diagnose hebt, maar het kan zinvol zijn om je ervaringen " +
            "te bespreken met een professional, zoals je huisarts of een psycholoog, als je hier hinder van ondervindt of je je zorgen maakt."
        } else {
            "Je score valt beneden de algemene grenswaarde die in dit instrument gehanteerd wordt. " +
            "Houd er echter rekening mee dat als je ergens mee worstelt of klachten ervaart, " +
            "het altijd verstandig is om hierover te praten met een zorgverlener."
        }

        Text(
            text = explanationText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
             Column(modifier = Modifier.padding(16.dp)) {
                 Text(
                     text = "Heb je direct hulp nodig? Praat erover.",
                     style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                     color = MaterialTheme.colorScheme.onErrorContainer,
                     modifier = Modifier.padding(bottom = 8.dp)
                 )
                 Text(
                     text = "Zit je in een crisissituatie of heb je gedachten aan zelfmoord?\n" +
                     "- In Nederland: Bel 113 of gratis 0800-0113 (of chat via 113.nl)\n" +
                     "- In Vlaanderen: Bel 1813 (of chat via zelfmoord1813.be)",
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onErrorContainer
                 )
             }
        }

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Terug naar home", fontSize = 16.sp)
        }
    }
}
