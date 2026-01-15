package com.example.foundit.presentation.screens

// Make sure to import your new AuthViewModel
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foundit.auth.AuthViewModel
import com.example.foundit.presentation.data.navigation.NavRoutes
import com.example.foundit.presentation.navigation.NavigationBar
import com.example.foundit.presentation.screens.documentation.PrivacyPolicyScreen
import com.example.foundit.presentation.screens.documentation.TermsOfServiceScreen
import com.example.foundit.presentation.screens.home.HomeScreen
import com.example.foundit.presentation.screens.input.UserItemInputScreen
import com.example.foundit.presentation.screens.notification.NotificationBaseViewModel
import com.example.foundit.presentation.screens.notification.NotificationScreen
import com.example.foundit.presentation.screens.profile.ProfileScreen
import com.example.foundit.presentation.screens.profile.ProfileViewModel
import com.example.foundit.presentation.screens.profile.components.EditProfileScreen
import com.example.foundit.presentation.screens.progress.ItemDetailsScreen
import com.example.foundit.presentation.screens.progress.ItemListScreen
import com.example.foundit.presentation.screens.progress.components.MatchedCardFullScreen
import com.example.foundit.presentation.screens.progress.components.ProgressCardFullScreen
import com.example.foundit.presentation.screens.registration.ForgotPasswordScreen
import com.example.foundit.presentation.screens.registration.GetStartedScreen
import com.example.foundit.presentation.screens.registration.login.LoginScreen
import com.example.foundit.presentation.screens.registration.login.LoginViewModel
import com.example.foundit.presentation.screens.registration.signup.SignUpScreen
import com.example.foundit.presentation.screens.registration.signup.SignUpViewModel
import com.example.foundit.presentation.screens.settings.SettingsScreen
import com.example.foundit.presentation.screens.settings.components.clickable.about.AboutScreen
import com.example.foundit.presentation.screens.settings.components.clickable.about.AcknowledgementScreen
import com.example.foundit.presentation.screens.settings.components.clickable.about.DeveloperInfoScreen
import com.example.foundit.presentation.screens.settings.components.clickable.account_center.AccountCenterScreen
import com.example.foundit.presentation.screens.settings.components.clickable.account_center.ChangeEmailScreen
import com.example.foundit.presentation.screens.settings.components.clickable.account_center.ChangePasswordScreen
import com.example.foundit.presentation.screens.settings.components.clickable.account_center.ChangePhoneNumberScreen
import com.example.foundit.presentation.screens.settings.components.clickable.account_center.DeleteAccountScreen
import com.example.foundit.presentation.screens.settings.components.clickable.appearance.AppearanceScreen
import com.example.foundit.presentation.screens.settings.components.clickable.feedback.FeedbackScreen
import com.example.foundit.presentation.screens.settings.components.clickable.help_and_Support.ContactSupportScreen
import com.example.foundit.presentation.screens.settings.components.clickable.help_and_Support.HelpAndSupportScreen
import com.example.foundit.presentation.screens.settings.components.clickable.help_and_Support.ReportBugScreen
import com.example.foundit.presentation.screens.settings.components.clickable.security.SecurityScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.foundit.presentation.splash.SplashScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val currentNavRoute = currentRoute(navController)
    val context = LocalContext.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val notificationBaseViewModel: NotificationBaseViewModel = hiltViewModel()

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val token = account?.idToken

            if (token != null) {
                Log.d("AUTH", "Google Token obtained successfully")
                authViewModel.signInWithSupabase(token) { success ->
                    if (success) {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Supabase Handshake Failed. Check Logcat for AUTH_ERROR.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e("AUTH", "Google Token was NULL. Is your Web Client ID correct in AuthModule?")
                Toast.makeText(context, "Error: Google Token is Null", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("AUTH", "Google Sign-In failed code: ${e.statusCode}")
            Toast.makeText(context, "Google Error: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentNavRoute)) {
                NavigationBar(modifier = modifier, navController = navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SPLASH,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.SPLASH) {
                SplashScreen(
                    navController = navController,
                    forwardNavigation = NavRoutes.GET_STARTED
                )
            }

            composable(NavRoutes.GET_STARTED) {
                GetStartedScreen(
                    modifier = modifier,
                    navController = navController
                )
            }

            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    modifier = modifier,
                    navController = navController,
                    loginViewModel = loginViewModel,
                    onGoogleSignInClick = {
                        try {
                            googleLauncher.launch(authViewModel.getSignInIntent())
                        } catch (e: Exception) {
                            Toast.makeText(context, "Intent Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            composable(NavRoutes.SIGN_UP) {
                SignUpScreen(
                    modifier = modifier,
                    navController = navController,
                    signUpViewModel = signUpViewModel,
                    onGoogleSignInClick = {
                        googleLauncher.launch(authViewModel.getSignInIntent())
                    }
                )
            }

            composable(NavRoutes.HOME) {
                HomeScreen(
                    modifier = modifier,
                    viewModel = profileViewModel,
                    navController = navController,
                    lostButtonClick = "${NavRoutes.USER_ITEM_INPUT_SCREEN}/0",
                    foundButtonClick = "${NavRoutes.USER_ITEM_INPUT_SCREEN}/1",
                )
            }


            composable(
                route = NavRoutes.USER_ITEM_INPUT_SCREEN + "/{cardType}",
                arguments = listOf(navArgument("cardType") { type = NavType.IntType })
            ) { 
                UserItemInputScreen(
                    modifier = modifier,
                    navController = navController
                )
            }

            composable(NavRoutes.PROGRESS) {
                ItemListScreen(navController = navController)
            }

            composable(
                NavRoutes.ITEM_DETAILS + "/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) {
                val itemId = it.arguments?.getString("itemId")
                ItemDetailsScreen(itemId = itemId.toString())
            }

            composable(
                NavRoutes.PROGRESS_CARD_FULL_SCREEN + "/{cardId}",
                arguments = listOf(navArgument("cardId") { type = NavType.StringType })
            ) {
                val cardId = it.arguments?.getString("cardId")
                ProgressCardFullScreen(modifier = modifier, cardId = cardId.toString(), navController = navController)
            }

            composable(
                NavRoutes.MATCHED_CARD_FULL_SCREEN + "/{cardId}",
                arguments = listOf(navArgument("cardId") { type = NavType.StringType })
            ) {
                val cardId = it.arguments?.getString("cardId")
                MatchedCardFullScreen(modifier = modifier, cardId = cardId.toString(), navController = navController)
            }

            composable(NavRoutes.NOTIFICATIONS) {
                NotificationScreen(
                    modifier = modifier,
                    navController = navController,
                    viewModel = notificationBaseViewModel
                )
            }

            composable(NavRoutes.PROFILE) {
                ProfileScreen(modifier, navController, profileViewModel)
            }

            composable(NavRoutes.ACCOUNT_CENTER) {
                AccountCenterScreen(modifier, navController)
            }

            composable(NavRoutes.APPEARANCE) {
                AppearanceScreen(modifier = modifier, navController = navController, onThemeChange = { })
            }

            composable(NavRoutes.SECURITY) {
                SecurityScreen(modifier, navController)
            }

            composable(NavRoutes.HELP_AND_SUPPORT) {
                HelpAndSupportScreen(modifier, navController)
            }

            composable(NavRoutes.FEEDBACK) {
                FeedbackScreen(modifier, navController)
            }

            composable(NavRoutes.ABOUT) {
                AboutScreen(modifier, navController)
            }

            composable(NavRoutes.EDIT_PROFILE) {
                EditProfileScreen(modifier, navController, profileViewModel)
            }


            composable(NavRoutes.DELETE_ACCOUNT) {
                DeleteAccountScreen(navController = navController, modifier = modifier)
            }

            composable(NavRoutes.REPORT_A_BUG) {
                ReportBugScreen(modifier, navController)
            }

            composable(NavRoutes.CONTACT_SUPPORT) {
                ContactSupportScreen(modifier, navController)
            }

            composable(NavRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.TERMS_OF_SERVICE) {
                TermsOfServiceScreen(
                    modifier = modifier,
                    navController = navController
                )
            }

            composable(NavRoutes.ACKNOWLEDGMENTS) {
                AcknowledgementScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.DEVELOPER_INFO) {
                DeveloperInfoScreen(modifier = modifier, navController = navController)
            }


            composable(NavRoutes.CHANGE_PASSWORD) {
                ChangePasswordScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.CHANGE_EMAIL) {
                ChangeEmailScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.CHANGE_PHONE_NUMBER) {
                ChangePhoneNumberScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(modifier = modifier, navController = navController)
            }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(modifier = modifier, navController = navController)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        NavRoutes.HOME,
        NavRoutes.PROGRESS,
        NavRoutes.NOTIFICATIONS,
        NavRoutes.PROFILE -> true
        else -> false
    }
}
