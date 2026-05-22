package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

@Composable
fun OnboardingApp(viewModel: OnboardingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    AnimatedContent(
        targetState = state.currentStep,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "Onboarding Transition",
        modifier = Modifier.fillMaxSize().background(BgPrimary)
    ) { step ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxHeight().border(1.dp, BorderDefault)) {
                when (step) {
                    OnboardingStep.Welcome -> WelcomeScreen(viewModel)
                    OnboardingStep.QuizIntent -> QuizIntentScreen(viewModel)
                    OnboardingStep.QuizMood -> QuizMoodScreen(viewModel)
                    OnboardingStep.QuizTherapy -> QuizTherapyScreen(viewModel)
                    OnboardingStep.FirstConversation -> FirstConversationScreen(viewModel)
                    OnboardingStep.PrivacyChoice -> PrivacyChoiceScreen(viewModel)
                    OnboardingStep.MagicLink -> MagicLinkScreen(viewModel)
                    OnboardingStep.Paywall -> PaywallScreen(viewModel)
                    OnboardingStep.Confirmation -> ConfirmationScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun TopBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPrimary)
            .border(1.dp, BorderDefault)
            .padding(16.dp)
    ) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, color = AccentPrimary)
    }
}

@Composable
fun WelcomeScreen(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar("KOMPAS")
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(AccentPrimary)
                .border(1.dp, BorderDefault),
            contentAlignment = Alignment.Center
        ) {
            Text("HERO IMAGE \n[DIRECTION]", color = BgPrimary, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(16.dp)) {
            Text("EEN VEILIGE PLEK OM TE PRATEN OVER WAT ER IS.", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("GEEN DWANG OM TE VERANDEREN. GEEN TOXIC POSITIVITEIT. GEWOON EEN GESPREK, WANNEER JE HET NODIG HEBT.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault)) {
            FeatureRow("AI-GESPREKSPARTNER OP BASIS VAN CLAUDE")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("24 GEVALIDEERDE ZELFTESTEN (PHQ-9, GAD-7)")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("PRIVACY-FIRST: ANONIEM STARTEN MOGELIJK")
        }

        ActionBlock(text = "BEGIN GESPREK", onClick = { viewModel.setStep(OnboardingStep.QuizIntent) })
        
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("PRIVACY EN DATA-GEBRUIK", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.clickable { })
        }
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(modifier = Modifier.size(8.dp).background(AccentPrimary))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text.uppercase(), style = MaterialTheme.typography.labelLarge, color = TextSecondary)
    }
}

@Composable
fun ActionBlock(text: String, onClick: () -> Unit, isOutline: Boolean = false, enabled: Boolean = true) {
    val bgColor = if (!enabled) BgSecondary else if (isOutline) BgPrimary else AccentPrimary
    val textColor = if (!enabled) TextSecondary else if (isOutline) AccentPrimary else BgPrimary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDefault)
            .background(bgColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text.uppercase(), style = MaterialTheme.typography.titleLarge, color = textColor)
    }
}

@Composable
fun QuizLayout(
    progress: String,
    question: String,
    subtext: String? = null,
    onSkip: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar("INTAKE • $progress")
        
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
            Text(question.uppercase(), style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            if (subtext != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(subtext.uppercase(), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
        
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("LIEVER OVERSLAAN", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.clickable { onSkip() }.padding(8.dp))
        }
    }
}

@Composable
fun SelectionCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDefault)
            .background(if (isSelected) BgElevated else BgPrimary)
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(20.dp).background(if (isSelected) AccentPrimary else BgPrimary).border(1.dp, if(isSelected) AccentPrimary else BorderDefault)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text.uppercase(), style = MaterialTheme.typography.titleLarge, color = if (isSelected) TextPrimary else TextSecondary)
    }
}

@Composable
fun QuizIntentScreen(viewModel: OnboardingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val options = listOf(
        "Gewoon praten over hoe ik me voel",
        "Een moeilijke situatie verwerken",
        "Begrijpen waarom ik me zo voel",
        "Hulp bij iets waar ik mee worstel",
        "Checken hoe het echt met me gaat",
        "Anders, of weet ik nog niet"
    )

    QuizLayout(
        progress = "1/3",
        question = "Waar hoop je op vandaag?",
        onSkip = { viewModel.setStep(OnboardingStep.QuizMood) }
    ) {
        options.forEach { option ->
            val isSelected = state.selectedIntentions.contains(option)
            SelectionCard(
                text = option,
                isSelected = isSelected,
                onClick = { viewModel.toggleIntention(option) }
            )
        }
        if (state.selectedIntentions.isNotEmpty()) {
            ActionBlock(text = "VOLGENDE", onClick = { viewModel.setStep(OnboardingStep.QuizMood) })
        }
    }
}

@Composable
fun QuizMoodScreen(viewModel: OnboardingViewModel) {
    val options = listOf(
        "Overwegend rustig, met af en toe piekmomenten",
        "Wisselend – goede en moeilijke dagen door elkaar",
        "Vaak onrustig of gespannen",
        "Overwegend zwaar of uitgeput",
        "Wil ik liever niet zeggen"
    )

    QuizLayout(
        progress = "2/3",
        question = "Hoe zou je je gemoedsrust de laatste weken omschrijven?",
        subtext = "Dit helpt om het gesprek af te stemmen op waar je nu staat.",
        onSkip = { viewModel.setStep(OnboardingStep.QuizTherapy) }
    ) {
        options.forEach { option ->
            SelectionCard(
                text = option,
                isSelected = false,
                onClick = {
                    viewModel.setMood(option)
                    viewModel.setStep(OnboardingStep.QuizTherapy)
                }
            )
        }
    }
}

@Composable
fun QuizTherapyScreen(viewModel: OnboardingViewModel) {
    val options = listOf(
        "Ja, ik ga momenteel",
        "Ja, in het verleden",
        "Nee, maar ik overweeg het",
        "Nee, en dat is nu niet aan de orde",
        "Liever niet zeggen"
    )

    QuizLayout(
        progress = "3/3",
        question = "Heb je ervaring met therapie of professionele hulp?",
        subtext = "Kompas is geen vervanging voor therapie, maar kan ondersteunen.",
        onSkip = { viewModel.setStep(OnboardingStep.FirstConversation) }
    ) {
        options.forEach { option ->
            SelectionCard(
                text = option,
                isSelected = false,
                onClick = {
                    viewModel.setTherapyExperience(option)
                    viewModel.setStep(OnboardingStep.FirstConversation)
                }
            )
        }
    }
}

@Composable
fun ChatBubble(text: String, isAi: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(if (isAi) BgElevated else AccentPrimary)
                .border(1.dp, if (isAi) BorderDefault else AccentPrimary)
                .padding(16.dp)
        ) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isAi) TextPrimary else BgPrimary
            )
        }
    }
}

@Composable
fun FirstConversationScreen(viewModel: OnboardingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var messageCount by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    
    val intentionText = if (state.selectedIntentions.isNotEmpty()) state.selectedIntentions.first().lowercase() else "praten"
    val initialMessage = "HOI. IK BEN HIER OM TE LUISTEREN NAAR WAT ER BIJ JE LEEFT.\n\nJE HEBT AANGEGEVEN DAT JE WILT $intentionText.\n\nGEEN AGENDA, GEEN OORDEEL. GEWOON RUIMTE OM TE PRATEN.\n\nWAAR WIL JE BEGINNEN?"

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar("GESPREK")
        
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))
                ChatBubble(initialMessage, isAi = true)
                
                if (messageCount > 0) {
                    ChatBubble("DIT IS EEN GESIMULEERD GESPREK OM DE INTERFACE TE TONEN.", isAi = false)
                }
                
                if (messageCount >= 2) {
                    ChatBubble("IK MERK DAT DIT JE BEZIGHOUDT. WIL JE HIER VERDER OVER PRATEN, OF LIEVER EVEN KIJKEN WAT KOMPAS VERDER VOOR JE KAN DOEN? JE KUNT ALTIJD LATER VERDERGAAN.", isAi = true)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        if (messageCount < 2) {
            Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).background(BgPrimary).padding(16.dp)) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("TYP EEN BERICHT...", color = TextSecondary, style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedContainerColor = BgSecondary,
                        unfocusedContainerColor = BgSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    trailingIcon = {
                        IconButton(onClick = { 
                            if (inputText.isNotBlank()) {
                                messageCount++
                                inputText = ""
                            } else {
                                messageCount++
                            }
                        }) {
                            Text("↑", fontSize = 24.sp, color = AccentPrimary)
                        }
                    }
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                ActionBlock(text = "BEKIJK WAT KOMPAS BIEDT", onClick = { viewModel.setStep(OnboardingStep.PrivacyChoice) })
                ActionBlock(text = "LATER VERDERGAAN", onClick = { messageCount++ }, isOutline = true)
            }
        }
    }
}

@Composable
fun PrivacyChoiceScreen(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar("VOORTGANG")
        
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
            Text("HOE WIL JE VERDERGAAN?", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("KOMPAS WERKT OP BEIDE MANIEREN. JOUW KEUZE.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }

        // Anonymous Option
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault)) {
            Box(modifier = Modifier.fillMaxWidth().background(BgElevated).padding(16.dp)) {
                Text("ANONIEM BLIJVEN", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("GESPREKKEN BLIJVEN OP DIT APPARAAT")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("GEEN EMAIL OF ACCOUNT NODIG")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("MAXIMALE PRIVACY")
            ActionBlock(text = "ANONIEM VERDERGAAN", isOutline = true, onClick = { 
                viewModel.setAccountType("anonymous")
                viewModel.setStep(OnboardingStep.Paywall) 
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Option
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault)) {
            Box(modifier = Modifier.fillMaxWidth().background(AccentPrimary).padding(16.dp)) {
                Text("ACCOUNT AANMAKEN", style = MaterialTheme.typography.titleLarge, color = BgPrimary)
            }
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("GESPREKKEN OP ALLE APPARATEN")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("CROSS-SESSION GEHEUGEN (PLUS FEATURE)")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("PDF EXPORT NAAR JE THERAPEUT")
            ActionBlock(text = "MAAK ACCOUNT AAN", onClick = { 
                viewModel.setAccountType("account")
                viewModel.setStep(OnboardingStep.MagicLink) 
            })
        }

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("MEER OVER PRIVACY EN DATA →", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.clickable { })
        }
    }
}

@Composable
fun MagicLinkScreen(viewModel: OnboardingViewModel) {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar(if(!emailSent) "AUTH" else "CHECK INBOX")

        if (!emailSent) {
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
                Text("VOER JE E-MAIL IN", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("WE STUREN EEN LINK OM IN TE LOGGEN. GEEN WACHTWOORD NODIG.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }

            Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("JOUW@EMAIL.BE", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(16.dp)) {
                Checkbox(checked = true, onCheckedChange = { })
                Text("IK GA AKKOORD MET DE VOORWAARDEN", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            }

            ActionBlock(text = "STUUR INLOGLINK", enabled = email.isNotBlank(), onClick = { 
                viewModel.setEmail(email)
                emailSent = true 
            })

            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("TOCH LIEVER ANONIEM BLIJVEN", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.clickable { 
                    viewModel.setAccountType("anonymous")
                    viewModel.setStep(OnboardingStep.Paywall) 
                })
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
                Text("LINK VERZONDEN", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("KLIK OP DE LINK IN DE EMAIL NAAR\n$email\nOM DOOR TE GAAN.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
            ActionBlock(text = "SIMULEER KLIK OP LINK", onClick = { viewModel.setStep(OnboardingStep.Paywall) })
        }
    }
}

@Composable
fun PaywallScreen(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar("PLUS ABONNEMENT")
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(AccentPrimary)
                .border(1.dp, BorderDefault),
            contentAlignment = Alignment.Center
        ) {
            Text("HERO IMAGE \n[UNLOCK NEW PATH]", color = BgPrimary, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
            Text("PROBEER KOMPAS PLUS\n14 DAGEN GRATIS", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("GEEN BETALING VANDAAG. ANNULEER OP ELK MOMENT.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault)) {
            FeatureRow("ONBEPERKTE GESPREKKEN MET AI")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("CROSS-SESSION GEHEUGEN")
            Divider(color = BorderDefault, thickness = 1.dp)
            FeatureRow("PDF-EXPORT VOOR JE THERAPEUT")
        }

        // Timeline visual mapped to text table structure
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).background(BgSecondary).padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("VANDAAG", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                Text("TRIAL START (GRATIS)", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DAG 13", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                Text("HERINNERING (EMAIL)", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DAG 14", style = MaterialTheme.typography.labelLarge, color = AccentPrimary)
                Text("EERSTE BETALING (€12,99)", style = MaterialTheme.typography.labelLarge, color = AccentPrimary)
            }
        }

        ActionBlock(text = "START 14 DAGEN GRATIS", onClick = { viewModel.setStep(OnboardingStep.Confirmation) })

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("LIEVER ANONIEM VERDER ZONDER PLUS-FUNCTIES", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.clickable { viewModel.setStep(OnboardingStep.Confirmation) }.padding(8.dp))
        }
    }
}

@Composable
fun ConfirmationScreen(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        TopBar("READY")
        
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f).background(BgElevated).border(1.dp, BorderDefault), contentAlignment = Alignment.Center) {
            Text("SYSTEM READY\nTRIAL ACTIVE", style = MaterialTheme.typography.headlineLarge, color = AccentPrimary, textAlign = TextAlign.Center)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault).padding(20.dp)) {
            Text("JE BENT KLAAR OM TE BEGINNEN", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("JE GESPREK VAN DAARNET STAAT NOG KLAAR. GA VERDER WAAR JE WAS, OF BEGIN IETS NIEUWS.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }

        Column(modifier = Modifier.fillMaxWidth().border(1.dp, BorderDefault)) {
            SelectionCard(text = "VERDER MET GESPREK", isSelected = true, onClick = { /* open app */ })
            SelectionCard(text = "VERKEN DE TESTS", isSelected = false, onClick = { /* open app */ })
            SelectionCard(text = "INSTELLINGEN AANPASSEN", isSelected = false, onClick = { /* open app */ })
        }

        ActionBlock(text = "OPEN KOMPAS", onClick = { /* Done! */ })
    }
}
