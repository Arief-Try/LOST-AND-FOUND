package com.example.foundit.presentation.screens.input.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.* // Use wildcard * to fix delegate errors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foundit.presentation.screens.input.lost.LostInputViewModel
import com.example.foundit.ui.theme.MainGreen

@Composable
fun ItemDescriptionAndDatePickerScreen(
    modifier: Modifier = Modifier,
    viewModel: LostInputViewModel
) {
    // Collecting state from ViewModel
    val cardType by viewModel.cardType.collectAsState()
    val itemDescription by viewModel.itemDescription.collectAsState()

    // Headings based on card type
    val descriptionCategoryTopHeading = if (cardType == 0) {
        "Provide a brief description of your lost item to assist in its quick recovery."
    } else {
        "Provide a brief description of your found item to assist in its quick recovery."
    }

    val datePickerTopHeading = if (cardType == 0) {
        "Tell us the date when the item was lost."
    } else {
        "Tell us the date when you found the item."
    }

    val datePickerTitle = if (cardType == 0) "Select date lost" else "Select date found"

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Description Section ---
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MainGreen)
        ) {
            Text(
                text = descriptionCategoryTopHeading,
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MainGreen.copy(alpha = 0.1f))
        ) {
            OutlinedTextField(
                value = itemDescription,
                onValueChange = { if (it.length <= 250) viewModel.updateItemDescription(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                label = { Text("Item Description") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text // Changed from Password
                ),
                trailingIcon = {
                    if (itemDescription.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateItemDescription("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Date Picker Section ---
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MainGreen)
        ) {
            Text(
                text = datePickerTopHeading,
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        DatePickerDocked(
            modifier = Modifier.fillMaxWidth(),
            datePickerTitle = datePickerTitle,
            viewModel = viewModel
        )
    }
}