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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect the real data from the ViewModel
    val fullName by profileViewModel.userFullName.collectAsState()
    val email by profileViewModel.userEmail.collectAsState()
    val avatarUrl by profileViewModel.profilePictureUrl.collectAsState()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Convert Uri to ByteArray and upload
            scope.launch {
                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    profileViewModel.uploadProfilePicture(bytes)
                }
                inputStream?.close()
            }
        }
    }

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
        },
        onEditPictureClick = {
            imagePickerLauncher.launch("image/*")
        }
    )
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier,
    profileFullName: String,
    profilePictureUrl: String?,
    email: String,
    onLogoutClick: () -> Unit,
    onEditPictureClick: () -> Unit
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
                        Box(contentAlignment = Alignment.BottomEnd) {
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
                            
                            // Edit Icon Button
                            SmallFloatingActionButton(
                                onClick = onEditPictureClick,
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp).offset(x = 4.dp, y = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Picture",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Change Profile Picture",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
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