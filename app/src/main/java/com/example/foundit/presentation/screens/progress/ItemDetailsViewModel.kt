package com.example.foundit.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    private val postgrest: Postgrest
) : ViewModel() {

    private val _item = MutableStateFlow<Map<String, Any>?>(null)
    val item: StateFlow<Map<String, Any>?> = _item.asStateFlow()

    fun fetchItem(itemId: String) {
        viewModelScope.launch {
            try {
                val response = postgrest.from("items").select { filter {
                    eq("id", itemId)
                } }.decodeSingle<Map<String, Any>>()
                _item.value = response
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
