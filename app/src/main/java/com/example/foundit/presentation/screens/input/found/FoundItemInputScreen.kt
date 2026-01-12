package com.example.foundit.presentation.screens.input.found

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foundit.presentation.screens.input.lost.LostInputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemInputScreen(
    modifier: Modifier = Modifier,
    viewModel: LostInputViewModel,
    onImageClick: () -> Unit
) {
    val description by viewModel.itemDescription.collectAsState()
    val location by viewModel.location.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val categories = viewModel.categories

    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- IMAGE BOX ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray)
                    Text(text = "Add Image", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CATEGORY ---
        Text(text = "CATEGORY", style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it }
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select a category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            viewModel.updateSelectedCategory(category)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- PHONE NUMBER ---
        Text(text = "PHONE NUMBER", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("How can people reach you?") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- DESCRIPTION ---
        Text(text = "DESCRIPTION", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.updateItemDescription(it) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            placeholder = { Text("Describe the item...") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- LOCATION ---
        Text(text = "LOCATION", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = location,
            onValueChange = { viewModel.updateLocation(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Where was it found?") }
        )

        // Internal button removed as requested.
    }
}