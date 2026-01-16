package com.example.foundit.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ItemDetailsUiState {
    object Loading : ItemDetailsUiState()
    data class Success(val item: Item) : ItemDetailsUiState()
    data class Error(val message: String) : ItemDetailsUiState()
}

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    private val postgrest: Postgrest
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailsUiState>(ItemDetailsUiState.Loading)
    val uiState: StateFlow<ItemDetailsUiState> = _uiState.asStateFlow()

    fun fetchItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = ItemDetailsUiState.Loading
            try {
                val item = postgrest.from("items").select {
                    filter {
                        eq("id", itemId)
                    }
                }.decodeSingle<Item>()
                _uiState.value = ItemDetailsUiState.Success(item)
            } catch (e: Exception) {
                _uiState.value = ItemDetailsUiState.Error("Failed to fetch item details: ${e.message}")
            }
        }
    }
}
