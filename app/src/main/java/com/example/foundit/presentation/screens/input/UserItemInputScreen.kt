package com.example.foundit.presentation.screens.input

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foundit.presentation.common.TheTopAppBar
import com.example.foundit.presentation.data.navigation.NavRoutes
import com.example.foundit.presentation.screens.input.common.components.UserInputBottomNavigationBar
import com.example.foundit.presentation.screens.input.found.FoundItemInputScreen
import com.example.foundit.presentation.screens.input.lost.LostInputViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UserItemInputScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val lostInputViewModel: LostInputViewModel = hiltViewModel()

    // Collect states from ViewModel
    val isSuccess by lostInputViewModel.isSuccess.collectAsState()
    val isLoading by lostInputViewModel.isLoading.collectAsState()
    val description by lostInputViewModel.itemDescription.collectAsState()
    val category by lostInputViewModel.selectedCategory.collectAsState()
    val location by lostInputViewModel.location.collectAsState()
    val phoneNumber by lostInputViewModel.phoneNumber.collectAsState() // Added observation
    val selectedImageUri by lostInputViewModel.selectedImageUri.collectAsState()

    // 1. Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        lostInputViewModel.onImageSelected(uri, context)
    }

    // 2. Observer for Success/Failure
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Item Posted Successfully!", Toast.LENGTH_LONG).show()
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.HOME) { inclusive = true }
            }
        }
    }

    val cardType = navController.currentBackStackEntry?.arguments?.getInt("cardType") ?: 0
    val typeLabel = if (cardType == 0) "lost" else "found"
    val topBarTitle = if (cardType == 0) "Report Lost Item" else "Report Found Item"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TheTopAppBar(title = topBarTitle, isNavigationIconVisible = true, navController = navController)
        },
        bottomBar = {
            UserInputBottomNavigationBar(
                cancelOrBackButtonText = "Back",
                // Dynamically changes text when uploading
                nextOrSubmitButtonText = if (isLoading) "Posting..." else "Post",
                onCancelOrBackClick = { navController.popBackStack() },
                nextButtonEnabled = {
                    // Validation: All main fields must be filled, and not currently loading
                    description.isNotEmpty() &&
                            category.isNotEmpty() &&
                            location.isNotEmpty() &&
                            phoneNumber.isNotEmpty() &&
                            !isLoading
                },
                onNextClick = {
                    if (lostInputViewModel.isNetworkAvailableViewmodel(context)) {
                        // This triggers the insertItem function in your ViewModel
                        lostInputViewModel.insertItem(itemType = typeLabel)
                    } else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { paddingValues ->
        FoundItemInputScreen(
            modifier = Modifier.padding(paddingValues),
            viewModel = lostInputViewModel,
            onImageClick = { imagePickerLauncher.launch("image/*") }
        )
    }
}