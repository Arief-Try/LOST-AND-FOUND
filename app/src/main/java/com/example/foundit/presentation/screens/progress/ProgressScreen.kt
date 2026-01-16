package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foundit.presentation.data.navigation.NavRoutes
import com.example.foundit.presentation.screens.progress.components.ItemCard

@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lost", "Found", "My Reports")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        when (val state = uiState) {
            is ItemUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ItemUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is ItemUiState.Success -> {
                val currentList = remember(selectedTabIndex, state.items) {
                    val (lost, found, myReports) = state.items.partition { (it.item_type).equals("lost", ignoreCase = true) }
                        .let { (lost, remaining) ->
                            val (found, myReports) = remaining.partition { (it.item_type).equals("found", ignoreCase = true) }
                            Triple(lost, found, myReports)
                        }

                    when (selectedTabIndex) {
                        0 -> lost
                        1 -> found
                        else -> myReports
                    }
                }

                if (currentList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No items found.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                        items(currentList) { item ->
                            ItemCard(
                                imageUrl = item.image_url ?: "",
                                location = item.location,
                                date = item.created_at?.substringBefore("T") ?: "",
                                onItemClick = {
                                    navController.navigate("${NavRoutes.ITEM_DETAILS}/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
