package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foundit.presentation.data.navigation.NavRoutes
import com.example.foundit.presentation.screens.progress.components.ItemCard

@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val lostItems by viewModel.lostItems.collectAsState()
    val foundItems by viewModel.foundItems.collectAsState()
    val myReportItems by viewModel.myReportItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Lost", "Found", "My Reports")

    val currentList = remember(selectedTabIndex, lostItems, foundItems, myReportItems, searchQuery) {
        val baseList = when (selectedTabIndex) {
            0 -> lostItems
            1 -> foundItems
            else -> myReportItems
        }

        if (searchQuery.isEmpty()) {
            baseList
        } else {
            baseList.filter { item ->
                val category = (item["category"] as? String)?.lowercase() ?: ""
                val location = (item["location"] as? String)?.lowercase() ?: ""
                category.contains(searchQuery.lowercase()) ||
                        location.contains(searchQuery.lowercase())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "ITEMS LIST",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search category or location...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            divider = {}, 
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(currentList) { item ->
                val rawDate = item["created_at"] as? String ?: ""

                ItemCard(
                    imageUrl = item["image_url"] as? String ?: "",
                    location = item["location"] as? String ?: "Unknown Location",
                    date = if (rawDate.contains("T")) rawDate.substringBefore("T") else rawDate,
                    onItemClick = {
                        val itemId = item["id"].toString()
                        navController.navigate("${NavRoutes.ITEM_DETAILS}/$itemId")
                    }
                )
            }

            if (currentList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No items found", color = Color.Gray)
                    }
                }
            }
        }
    }
}
