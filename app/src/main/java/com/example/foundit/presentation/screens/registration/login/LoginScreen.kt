package com.example.foundit.presentation.screens.registration.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foundit.presentation.screens.home.components.AppIcon
import com.example.foundit.presentation.screens.registration.components.ClickablePrivacyAndTermsText
import com.example.foundit.presentation.screens.registration.components.google.ContinueWithGoogleCard

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onGoogleSignInClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppIcon(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        ContinueWithGoogleCard(
            modifier = modifier,
            onClick = onGoogleSignInClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ClickablePrivacyAndTermsText(
            modifier = modifier,
            navController = navController
        )
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_6_pro")
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController(), onGoogleSignInClick = {})
}
