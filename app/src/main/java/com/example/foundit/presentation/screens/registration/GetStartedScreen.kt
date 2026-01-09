package com.example.foundit.presentation.screens.registration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foundit.presentation.data.navigation.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun GetStartedScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Animation states
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

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 800)
                )
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8E8E8)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF70D4FF))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title with typing effect
                        if (isLogoVisible) {
                            TypingTextEffect(
                                text = "Lost & Found",
                                textStyle = TextStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 36.sp,
                                    color = Color(0xFF000080), // Navy Blue
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Subtitle
                        if (isDescriptionVisible) {
                            Text(
                                text = "\"Connecting Finders\nand Owners.\"",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Get Started Button
                        if (isButtonVisible) {
                            Button(
                                onClick = {
                                    navController.navigate(NavRoutes.LOGIN)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF70D4FF)
                                )
                            ) {
                                Text(
                                    text = "Get Started",
                                    fontSize = 16.sp,
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


@Composable
fun TypingTextEffect(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    typingDelay: Long = 150L
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