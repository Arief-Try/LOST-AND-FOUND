package com.example.foundit.presentation.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foundit.R
import com.example.foundit.presentation.screens.home.components.Greetings
import com.example.foundit.presentation.screens.home.components.MainCard
import com.example.foundit.presentation.screens.profile.ProfileViewModel
import com.example.foundit.ui.theme.MainGreen
import com.example.foundit.ui.theme.MainRed

// UI-Only Composable
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    greetingPrefix: String,
    profileName: String?,
    navController: NavHostController,
    lostButtonClick: String,
    foundButtonClick: String,
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 20.dp, end = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
            ) {
                Text(
                    text = "LOST & FOUND",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                // Re-enable Greetings if you want the "Hi, Name" text to show
                Greetings(
                    greetingPrefix = greetingPrefix,
                    profileName = profileName
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp)
                    .verticalScroll(rememberScrollState(), true),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Lost Card
                MainCard(
                    modifier = Modifier,
                    cardHeading = R.string.lost_card_heading,
                    cardTitle = R.string.lost_card_sub_title,
                    cardColor = MainRed,
                    navController = navController,
                    forwardNavigation = lostButtonClick,
                )

                // Found Card
                MainCard(
                    modifier = Modifier,
                    cardHeading = R.string.found_card_heading,
                    cardTitle = R.string.found_card_sub_title,
                    cardColor = MainGreen,
                    navController = navController,
                    forwardNavigation = foundButtonClick
                )
            }
        }
    }
}

// ViewModel Composable
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    navController: NavHostController,
    lostButtonClick: String,
    foundButtonClick: String,
) {
    val context = LocalContext.current

    // COLLECT THE NEW STATE HERE
    val fullName by viewModel.userFullName.collectAsState()
    val greetingPrefix = stringResource(id = R.string.greeting_prefix)

    // Trigger profile load when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadGoogleProfile()
    }

    HomeScreenContent(
        modifier = modifier,
        greetingPrefix = greetingPrefix,
        profileName = fullName, // Pass the single full name string
        navController = navController,
        lostButtonClick = lostButtonClick,
        foundButtonClick = foundButtonClick
    )

    BackHandler(
        enabled = true,
        onBack = { (context as Activity).finish() }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_2")
fun PreviewHomeScreen() {
    HomeScreenContent(
        modifier = Modifier,
        greetingPrefix = "HI",
        profileName = "Musaib Shabir",
        navController = NavHostController(LocalContext.current),
        lostButtonClick = "",
        foundButtonClick = ""
    )
}