package com.example.foundit.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ItemUiState {
    object Loading : ItemUiState()
    data class Success(
        val lostItems: List<Item>,
        val foundItems: List<Item>,
        val myReportItems: List<Item>
    ) : ItemUiState()
    data class Error(val message: String) : ItemUiState()
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemUiState>(ItemUiState.Loading)
    val uiState: StateFlow<ItemUiState> = _uiState.asStateFlow()

    private var allItems = mutableListOf<Item>()
    private var currentOffset = 0L
    private val pageSize = 5L // Strictly limited to 5 per page

    init {
        fetchItems(isRefresh = true)
    }

    fun fetchItems(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = ItemUiState.Loading
                currentOffset = 0L
                allItems.clear()
            }

            try {
                val userId = supabase.auth.currentUserOrNull()?.id

                // Fetch from Supabase with Order and Range
                val newItems = supabase.postgrest.from("items")
                    .select {
                        order("created_at", order = Order.DESCENDING)
                        range(currentOffset, currentOffset + pageSize - 1)
                    }
                    .decodeList<Item>()

                allItems.addAll(newItems)
                currentOffset += pageSize

                // Filter lists for the tabs
                val lostItems = allItems.filter { it.item_type.equals("lost", ignoreCase = true) }
                val foundItems = allItems.filter { it.item_type.equals("found", ignoreCase = true) }
                val myReportItems = if (userId != null) allItems.filter { it.user_id == userId } else emptyList()

                _uiState.value = ItemUiState.Success(lostItems, foundItems, myReportItems)

            } catch (e: Exception) {
                _uiState.value = ItemUiState.Error("Failed to load items: ${e.message}")
            }
        }
    }
}