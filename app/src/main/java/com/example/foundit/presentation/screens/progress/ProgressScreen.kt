package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Lost", "Found", "My Reports")

    // Automatically fetch when Tab or Search changes
    LaunchedEffect(selectedTabIndex, searchQuery) {
        viewModel.fetchItems(tabs[selectedTabIndex], searchQuery, isRefresh = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ITEM LIST",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            // Update the hint text
            placeholder = { Text("Search location or category") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState is ItemUiState.Loading,
            onRefresh = { viewModel.fetchItems(tabs[selectedTabIndex], searchQuery, isRefresh = true) },
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = uiState) {
                is ItemUiState.Success -> {
                    // This list will now always contain 5 items (if available in DB)
                    val displayList = when (selectedTabIndex) {
                        0 -> state.lostItems
                        1 -> state.foundItems
                        else -> state.myReportItems
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(displayList) { item ->
                            ItemCard(
                                imageUrl = item.image_url ?: "",
                                location = item.location,
                                category = item.category ?: "General", // Pass the category here
                                date = item.created_at?.substringBefore("T") ?: "",
                                onItemClick = {
                                    navController.navigate("${NavRoutes.ITEM_DETAILS}/${item.id}")
                                }
                            )
                        }

                        item {
                            if (displayList.size >= 5) { // Only show 'Next' if we have a full page
                                Button(
                                    onClick = { viewModel.loadNextPage(tabs[selectedTabIndex], searchQuery) },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next (Show Next 5)", fontWeight = FontWeight.Bold)
                                }
                            } else if (displayList.isEmpty()) {
                                Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                    Text("No items found", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
                is ItemUiState.Error -> { /* Show Error Text */ }
                is ItemUiState.Loading -> { /* Loading is handled by PullToRefreshBox */ }
            }
        }
    }
}