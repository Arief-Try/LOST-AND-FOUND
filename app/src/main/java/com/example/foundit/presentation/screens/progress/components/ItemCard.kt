package com.example.foundit.presentation.screens.progress.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemCard(
    imageUrl: String,
    location: String,
    date: String,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Image URL: $imageUrl")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Location: $location")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: $date")
        }
    }
}
