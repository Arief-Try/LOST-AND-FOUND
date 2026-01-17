package com.example.foundit.presentation.screens.profile.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foundit.presentation.common.TheTopAppBar
import com.example.foundit.presentation.screens.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    modifier: Modifier,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    profilePictureUrl: String?,
    navController: NavController
) {
    Scaffold(
        topBar = { TheTopAppBar(title = "Edit Profile", navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile picture Display
            if (!profilePictureUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profilePictureUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(180.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Full Name Input Field
                TextField(
                    value = fullName,
                    onValueChange = { onFullNameChange(it) },
                    singleLine = true,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onCancelClick) {
                    Text(text = "Cancel", fontSize = 18.sp)
                }

                Button(
                    onClick = onSaveClick,
                    enabled = fullName.isNotBlank()
                ) {
                    Text(text = "Save Changes", fontSize = 18.sp)
                }
            }

            // Note for User
            Text(
                text = "Note: Profile details are managed via Google",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    // Collect the new unified states from ViewModel
    val userFullName by viewModel.userFullName.collectAsState()
    val avatarUrl by viewModel.profilePictureUrl.collectAsState()

    // Local state for editing
    var fullNameState by remember { mutableStateOf("") }

    // Sync local state when ViewModel data loads
    LaunchedEffect(userFullName) {
        fullNameState = userFullName
    }

    EditProfileScreenContent(
        modifier = modifier,
        fullName = fullNameState,
        onFullNameChange = { fullNameState = it },
        onCancelClick = { navController.popBackStack() },
        onSaveClick = {
            // Usually, we don't update Google metadata locally,
            // but you can add a function to ViewModel if needed.
            navController.popBackStack()
        },
        profilePictureUrl = avatarUrl,
        navController = navController
    )
}