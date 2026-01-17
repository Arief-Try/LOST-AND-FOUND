package com.example.foundit.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foundit.presentation.data.navigation.NavRoutes
import io.github.jan.supabase.auth.auth // IMPORTANT: Needed for signOut()
import kotlinx.coroutines.launch // IMPORTANT: Fixes 'launch' error

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    // Collect the real data from the ViewModel
    val fullName by viewModel.userFullName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val avatarUrl by viewModel.profilePictureUrl.collectAsState()

    // Create a coroutine scope for the logout function
    val scope = rememberCoroutineScope()

    ProfileScreenContent(
        modifier = modifier,
        profileFullName = fullName,
        profilePictureUrl = avatarUrl,
        email = email,
        onLogoutClick = {
            // Use the supabase client instance already inside your viewModel
            scope.launch {
                try {
                    viewModel.logout() // We use a logout function in ViewModel (defined below)
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (e: Exception) {
                    // Handle logout error if necessary
                }
            }
        }
    )
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier,
    profileFullName: String,
    profilePictureUrl: String?,
    email: String,
    onLogoutClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (!profilePictureUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = "Google Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Placeholder",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Information Items
            ProfileInfoItem(icon = Icons.Default.Person, label = "FULL NAME", value = profileFullName)
            ProfileInfoItem(icon = Icons.Default.Email, label = "E-MAIL", value = email)

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}