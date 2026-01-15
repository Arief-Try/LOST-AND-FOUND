package com.example.foundit.presentation.screens.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _lostItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val lostItems: StateFlow<List<Map<String, Any>>> = _lostItems.asStateFlow()

    private val _foundItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val foundItems: StateFlow<List<Map<String, Any>>> = _foundItems.asStateFlow()

    private val _myReportItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val myReportItems: StateFlow<List<Map<String, Any>>> = _myReportItems.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        Log.d("Diagnosis", "ProgressViewModel initialized. Fetching data...")
        fetchData()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                Log.d("Diagnosis", "Fetching items from Supabase...")
                val response = supabase.postgrest.from("items").select().decodeList<Map<String, Any>>()
                Log.d("Diagnosis", "Supabase response received. ${response.size} items found.")

                _lostItems.value = response.filter { it["item_type"] == "LOST" }
                _foundItems.value = response.filter { it["item_type"] == "FOUND" }
                Log.d("Diagnosis", "Filtering complete. Lost: ${_lostItems.value.size}, Found: ${_foundItems.value.size}")

                if (userId != null) {
                    _myReportItems.value = response.filter { it["user_id"] == userId }
                    Log.d("Diagnosis", "My Reports filtering complete. Found: ${_myReportItems.value.size}")
                } else {
                    Log.w("Diagnosis", "User ID is null. Cannot filter 'My Reports'.")
                }

            } catch (e: Exception) {
                Log.e("Diagnosis", "An error occurred while fetching data: ${e.message}", e)
            }
        }
    }
}
