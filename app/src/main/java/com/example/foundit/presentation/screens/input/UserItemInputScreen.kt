package com.example.foundit.presentation.screens.input

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
    val lostInputViewModel: LostInputViewModel = hiltViewModel()

    val cardType = navController.currentBackStackEntry?.arguments?.getInt("cardType")
    LaunchedEffect(cardType) {
        if (cardType != null) {
            lostInputViewModel.storeCardType(cardType)
        }
    }

    val context = LocalContext.current

    val description by lostInputViewModel.itemDescription.collectAsState()
    val category by lostInputViewModel.selectedCategory.collectAsState()
    val location by lostInputViewModel.location.collectAsState()

    var topBarTitle by remember { mutableStateOf("") }

    when (cardType) {
        0 -> topBarTitle = "Report Lost Item"
        1 -> topBarTitle = "Report Found Item"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TheTopAppBar(title = topBarTitle, isNavigationIconVisible = false, navController = navController) },
        bottomBar = {
            UserInputBottomNavigationBar(
                cancelOrBackButtonText = "Back",
                nextOrSubmitButtonText = "Post",
                onCancelOrBackClick = { navController.popBackStack() },
                nextButtonEnabled = {
                    description.isNotEmpty() && category.isNotEmpty() && location.isNotEmpty()
                },
                onNextClick = {
                    if (lostInputViewModel.isNetworkAvailableViewmodel(context)) {
                        lostInputViewModel.onSubmitClick { isSuccess, _ ->
                            if (isSuccess) {
                                Toast.makeText(
                                    context,
                                    "Your Item was Registered Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate(NavRoutes.HOME)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Something Went Wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please Connect to the Internet",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

    ) { paddingValues ->
        FoundItemInputScreen(
            modifier = Modifier.padding(paddingValues),
            viewModel = lostInputViewModel,
        )
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_2")
@Composable
fun PreviewUserItemInputScreen() {
    UserItemInputScreen(
        modifier = Modifier,
        navController = rememberNavController()
    )
}
