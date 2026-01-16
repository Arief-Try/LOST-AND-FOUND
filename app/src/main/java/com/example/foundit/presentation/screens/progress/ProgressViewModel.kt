package com.example.foundit.presentation.screens.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
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

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch {
            _uiState.value = ItemUiState.Loading
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                val items = supabase.postgrest.from("items").select().decodeList<Item>().sortedByDescending { it.created_at }
                Log.d("ProgressViewModel", "Successfully fetched ${items.size} items.")

                val lostItems = items.filter { it.item_type.equals("lost", ignoreCase = true) }
                val foundItems = items.filter { it.item_type.equals("found", ignoreCase = true) }
                val myReportItems = if (userId != null) items.filter { it.user_id == userId } else emptyList()

                Log.d("ProgressViewModel", "Filtered items. Lost: ${lostItems.size}, Found: ${foundItems.size}, My Reports: ${myReportItems.size}")

                _uiState.value = ItemUiState.Success(lostItems, foundItems, myReportItems)

            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error fetching items", e)
                _uiState.value = ItemUiState.Error("Failed to load items: ${e.message}")
            }
        }
    }
}
