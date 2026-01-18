package com.example.foundit.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    fun onAppStart(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Check if there's a valid Supabase session
                val user = supabase.auth.currentUserOrNull()
                onResult(user != null)
            } catch (e: Exception) {
                onResult(false)
                Log.d("Session", "Session error: ${e.message}")
            }
        }
    }
}
