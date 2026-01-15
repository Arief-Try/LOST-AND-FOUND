package com.example.foundit.presentation.screens.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Lost", "Found", "My Reports")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(8.dp)) {
            // Determine which list to display based on the selected tab
            val currentList = when (selectedTabIndex) {
                0 -> lostItems
                1 -> foundItems
                else -> myReportItems
            }

            items(currentList) { item ->
                // Extracting values from the Map safely
                val rawDate = item["created_at"] as? String ?: ""

                ItemCard(
                    imageUrl = item["image_url"] as? String ?: "",
                    location = item["location"] as? String ?: "No location",
                    date = rawDate.substringBefore("T"), // Simplifies the Supabase timestamp
                    onItemClick = {
                        // Pass the ID to the details screen
                        val itemId = item["id"].toString()
                        navController.navigate(NavRoutes.ITEM_DETAILS + "/$itemId")
                    }
                )
            }
        }
    }
}
