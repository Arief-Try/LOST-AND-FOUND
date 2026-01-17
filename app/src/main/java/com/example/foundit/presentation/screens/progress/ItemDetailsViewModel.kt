package com.example.foundit.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.models.Item
import com.example.foundit.data.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
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
    private val postgrest: Postgrest,
    private val auth: Auth
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailsUiState>(ItemDetailsUiState.Loading)
    val uiState: StateFlow<ItemDetailsUiState> = _uiState.asStateFlow()

    val currentUserId: String? get() = auth.currentUserOrNull()?.id

    fun fetchItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = ItemDetailsUiState.Loading
            try {
                // Step 1: Fetch the item only (No join, so no error)
                val item = postgrest.from("items").select {
                    filter { eq("id", itemId) }
                }.decodeSingle<Item>()

                // Step 2: Fetch the user profile separately
                val userProfile = try {
                    postgrest.from("users").select {
                        filter { eq("id", item.user_id) }
                    }.decodeSingle<UserProfile>()
                } catch (e: Exception) {
                    UserProfile(full_name = "Anonymous User")
                }

                // Combine them and show the screen
                _uiState.value = ItemDetailsUiState.Success(item.copy(users = userProfile))

            } catch (e: Exception) {
                _uiState.value = ItemDetailsUiState.Error("Could not load item: ${e.message}")
            }
        }
    }

    fun deleteItem(itemId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                postgrest.from("items").delete {
                    filter { eq("id", itemId) }
                }
                onDeleted()
            } catch (e: Exception) {
                _uiState.value = ItemDetailsUiState.Error("Delete failed: ${e.message}")
            }
        }
    }
}