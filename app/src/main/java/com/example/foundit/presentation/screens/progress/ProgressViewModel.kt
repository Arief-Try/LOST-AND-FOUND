package com.example.foundit.presentation.screens.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
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

    // Using Map<String, Any> because there is no Item.kt data class
    private val _lostItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val lostItems: StateFlow<List<Map<String, Any>>> = _lostItems.asStateFlow()

    private val _foundItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val foundItems: StateFlow<List<Map<String, Any>>> = _foundItems.asStateFlow()

    private val _myReportItems = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val myReportItems: StateFlow<List<Map<String, Any>>> = _myReportItems.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                // 1. Fetch all raw data from Supabase
                val response = supabase.postgrest.from("items")
                    .select()
                    .decodeList<Map<String, Any>>()

               // val response = supabase.postgrest.from("items").select().decodeList<Map<String, Any>>()
                Log.d("SUPABASE_CHECK", "Items fetched: ${response.size}")
                if (response.isEmpty()) {
                    Log.d("SUPABASE_CHECK", "The list is empty. Check RLS Policies or Table Name.")
                }
                Log.d("SupabaseDebug", "Raw Response Size: ${response.size}")
                Log.d("SupabaseDebug", "Raw Data: $response")

                // 2. Filter for lowercase "lost"
                _lostItems.value = response.filter {
                    (it["item_type"] as? String)?.lowercase() == "lost"
                }

                // 3. Filter for lowercase "found"
                _foundItems.value = response.filter {
                    (it["item_type"] as? String)?.lowercase() == "found"
                }

                // 4. My Reports: Showing all for testing since login is bypassed
                _myReportItems.value = response

                Log.d("SupabaseSuccess", "Loaded ${response.size} items")
            } catch (e: Exception) {
                Log.e("SupabaseError", "Fetch failed: ${e.message}")
            }
        }
    }
}