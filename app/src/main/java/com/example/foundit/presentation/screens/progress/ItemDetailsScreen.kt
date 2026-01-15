package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ItemDetailsScreen(
    itemId: String,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.fetchItem(itemId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item?.let {
            for ((key, value) in it) {
                Text(text = "$key: $value")
            }
        }
    }
}
