package com.example.foundit.presentation.screens.profile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.foundit.presentation.data.navigation.NavRoutes
import com.example.foundit.presentation.screens.profile.components.ProfileTopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    profileFirstName: String,
    profileLastName: String,
    profilePicture: Uri?,
    email: String,
    contact: String,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    navController: NavController,
) {
    Scaffold(
        topBar = { ProfileTopAppBar(title = "YOUR ACCOUNT", navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicture != null) {
                        Image(
                            painter = rememberAsyncImagePainter(profilePicture),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(120.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(icon = Icons.Default.Person, label = "NAME", value = "$profileFirstName $profileLastName")
                InfoRow(icon = Icons.Default.Email, label = "E-MAIL", value = email)
                InfoRow(icon = Icons.Default.Phone, label = "CONTACT", value = contact)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(8.dp))
                Text("EDIT")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors()
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOG OUT")
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}


// ViewModel Composable
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val userFirstName by viewModel.userFirstNames.collectAsState()
    val userLastName by viewModel.userLastNames.collectAsState()
    val profilePicture by viewModel.profilePicture.collectAsState()
    // TODO: Get email and contact from viewModel
    val email = "ALI20254@GMAIL.COM"
    val contact = "012 3456789"

    ProfileScreenContent(
        modifier = modifier,
        profileFirstName = userFirstName,
        profileLastName = userLastName,
        profilePicture = profilePicture,
        email = email,
        contact = contact,
        onEditProfileClick = { navController.navigate(NavRoutes.EDIT_PROFILE) },
        onLogoutClick = { /* TODO: Handle logout */ },
        navController = navController
    )

    BackHandler(
        enabled = true,
    ) {
        // Decide what to do on back press
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_6_pro")
fun PreviewProfileScreen() {
    ProfileScreenContent(
        modifier = Modifier,
        profilePicture = null,
        profileFirstName = "ALI",
        profileLastName = "BIN ABU",
        email = "ALI20254@GMAIL.COM",
        contact = "012 3456789",
        onEditProfileClick = { },
        onLogoutClick = { },
        navController = NavController(LocalContext.current)
    )
}