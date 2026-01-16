package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Lost", "Found", "My Reports")

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. ITEM LIST TITLE
        Text(
            text = "ITEM LIST",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
        )

        // 2. SEARCH BAR & REFRESH BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilledIconButton(
                onClick = { viewModel.fetchItems(isRefresh = true) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh list")
            }
        }

        // 3. TOP NAV (TABS)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // 4. THE LIST CONTENT
        when (val state = uiState) {
            is ItemUiState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ItemUiState.Success -> {
                // Filter the list based on search and tab
                val currentTabList = when (selectedTabIndex) {
                    0 -> state.lostItems
                    1 -> state.foundItems
                    else -> state.myReportItems
                }

                val filteredList = currentTabList.filter {
                    it.location.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { item ->
                        ItemCard(
                            imageUrl = item.image_url ?: "",
                            location = item.location,
                            date = item.created_at?.substringBefore("T") ?: "",
                            onItemClick = {
                                navController.navigate("${NavRoutes.ITEM_DETAILS}/${item.id}")
                            }
                        )
                    }

                    // NEXT BUTTON
                    item {
                        if (filteredList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchItems(isRefresh = false) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Next (Load 5 more)", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        } else {
                            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                Text("No items found.", color = Color.Gray)
                            }
                        }
                    }
                }
            }
            is ItemUiState.Error -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
        }
    }
}