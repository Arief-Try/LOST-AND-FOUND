package com.example.foundit.presentation.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foundit.auth.AuthViewModel
import com.example.foundit.presentation.data.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    // Collect the real data from the ViewModel
    val fullName by profileViewModel.userFullName.collectAsState()
    val email by profileViewModel.userEmail.collectAsState()
    val avatarUrl by profileViewModel.profilePictureUrl.collectAsState()

    // REMOVED: imagePickerLauncher logic and LocalContext/CoroutineScope if no longer used

    ProfileScreenContent(
        modifier = modifier,
        profileFullName = fullName,
        profilePictureUrl = avatarUrl,
        email = email,
        onLogoutClick = {
            authViewModel.logout { success ->
                if (success) {
                    navController.navigate(NavRoutes.SIGN_UP) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
        // REMOVED: onEditPictureClick parameter
    )
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier,
    profileFullName: String,
    profilePictureUrl: String?,
    email: String,
    onLogoutClick: () -> Unit
    // REMOVED: onEditPictureClick parameter
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
                    .height(240.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Removed the Box wrap that held the FAB and the SmallFloatingActionButton
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = profilePictureUrl,
                                contentDescription = "Profile Picture",
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

                        // REMOVED: Spacer and "Change Profile Picture" Text
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