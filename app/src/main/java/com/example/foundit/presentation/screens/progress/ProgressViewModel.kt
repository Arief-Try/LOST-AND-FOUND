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

    private var currentOffset = 0L
    private val pageSize = 5L

    // We pass the 'tab' and 'search' to the fetch function
    fun fetchItems(type: String, query: String = "", isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = ItemUiState.Loading

            if (isRefresh) currentOffset = 0L

            try {
                val userId = supabase.auth.currentUserOrNull()?.id

                val result = supabase.postgrest.from("items")
                    .select {
                        filter {
                            if (type != "My Reports") {
                                eq("item_type", type.lowercase())
                            } else if (userId != null) {
                                eq("user_id", userId)
                            }
                            if (query.isNotEmpty()) {
                                or {
                                    ilike("location", "%$query%")
                                    ilike("category", "%$query%")
                                }
                            }
                        }
                        order("created_at", order = Order.DESCENDING)
                        range(currentOffset, currentOffset + pageSize - 1)
                    }
                    .decodeList<Item>()

                _uiState.value = ItemUiState.Success(
                    lostItems = if (type == "Lost") result else emptyList(),
                    foundItems = if (type == "Found") result else emptyList(),
                    myReportItems = if (type == "My Reports") result else emptyList()
                )

            } catch (e: Exception) {
                _uiState.value = ItemUiState.Error("Failed: ${e.message}")
            }
        }
    }
    fun loadNextPage(type: String, query: String) {
        currentOffset += pageSize
        fetchItems(type, query, isRefresh = false)
    }
}
