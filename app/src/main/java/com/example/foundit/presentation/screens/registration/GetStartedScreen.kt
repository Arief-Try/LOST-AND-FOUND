package com.example.foundit.presentation.screens.registration


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foundit.presentation.data.navigation.NavRoutes
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Spacer


@Composable
fun GetStartedScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Keep the animation states
    var isVisible by remember { mutableStateOf(false) }
    var isLogoVisible by remember { mutableStateOf(false) }
    var isDescriptionVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(500)
        isLogoVisible = true
        delay(800)
        isDescriptionVisible = true
        delay(500)
        isButtonVisible = true
    }

    // Inside GetStaredScreen_Kt.txt
    Button(
        onClick = {
            // This tells the app to go to the Login screen directly
            navController.navigate(NavRoutes.LOGIN)
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(55.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF70D4FF))
    ) {
        Text(
            text = "Get Started",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2)) // Light Gray Background
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // ADDED: verticalScroll makes sure the button is never cut off on small phones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000)
                    ) + fadeIn()
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFD9D9D9)),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 40.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Typing Effect with Navy Blue color
                            TypingTextEffect(
                                text = "Lost & Found",
                                textStyle = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 38.sp,
                                    color = Color(0xFF000080) // Navy Blue
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isDescriptionVisible) {
                                Text(
                                    text = "“Connecting Finders and Owners.”",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            if (isButtonVisible) {
                                Button(
                                    onClick = {
                                        // Navigating to LOGIN as requested
                                        navController.navigate(NavRoutes.LOGIN)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(55.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF70D4FF) // Light Blue
                                    )
                                ) {
                                    Text(
                                        text = "Get Started",
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingTextEffect(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    typingDelay: Long = 150L // Reduced delay for a smoother feel
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { _, char ->
            delay(typingDelay)
            displayedText += char
        }
    }

    Text(
        text = displayedText,
        style = textStyle,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
