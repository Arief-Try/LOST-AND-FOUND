package com.example.foundit.presentation.screens.registration.components.google

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foundit.R
import com.example.foundit.ui.theme.MainGreen
import com.example.foundit.ui.theme.RobotFamily

@Composable
fun ContinueWithGoogleCard(
    modifier: Modifier = Modifier,
    // 1. We removed the ViewModel and Credential parameters
    // 2. We added this simple onClick callback
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            onClick = { onClick() }, // This now triggers the Launcher in MainScreen
            shape = RoundedCornerShape(228.dp),
            border = BorderStroke(width = 1.dp, color = MainGreen),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    // FIXED: Using your existing 'google' drawable
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "CONTINUE WITH GOOGLE",
                    fontSize = 18.sp,
                    letterSpacing = 2.sp,
                    fontFamily = RobotFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.Black // Ensuring text is visible on white
                )
            }
        }
    }
}

