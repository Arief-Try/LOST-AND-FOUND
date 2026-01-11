package com.example.foundit.presentation.screens.input.found

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.foundit.presentation.screens.input.lost.LostInputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemInputScreen(
    modifier: Modifier = Modifier,
    viewModel: LostInputViewModel
) {
    val description by viewModel.itemDescription.collectAsState()
    val location by viewModel.location.collectAsState()
    val categories = viewModel.categories
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Add Image")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* TODO: Handle image upload */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Upload Image")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "CATEGORY")
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it }
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        Text(text = "DESCRIPTION")
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.updateItemDescription(it) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            placeholder = { Text("Max 50 characters") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "LOCATION")
        OutlinedTextField(
            value = location,
            onValueChange = { viewModel.updateLocation(it) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
