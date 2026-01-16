package com.example.foundit.presentation.screens.registration.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foundit.presentation.data.navigation.NavRoutes

@Composable
fun ClickablePrivacyAndTermsText(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val annotatedString = buildAnnotatedString {
        append("By continuing, you agree to Lost & Found’s ")
        pushStringAnnotation(
            tag = "Terms of Service",
            annotation = "Terms of Service"
        )
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Terms of Service")
        }
        pop()
        append(" and acknowledge that you’ve read our ")
        pushStringAnnotation(
            tag = "Privacy Policy",
            annotation = "Privacy Policy"
        )
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Privacy Policy")
        }
        pop()
        append(".\nNotice at collection.")
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
        onClick = {
            annotatedString.getStringAnnotations(it, it)
                .firstOrNull()?.let { annotation ->
                    when (annotation.item) {
                        "Terms of Service" -> navController.navigate(NavRoutes.TERMS_OF_SERVICE)
                        "Privacy Policy" -> navController.navigate(NavRoutes.PRIVACY_POLICY)
                    }
                }
        }
    )
}