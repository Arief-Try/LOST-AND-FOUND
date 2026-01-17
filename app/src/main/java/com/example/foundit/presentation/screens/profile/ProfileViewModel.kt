package com.example.foundit.presentation.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    // Using StateFlow to hold and expose the username and metadata
    private val _userFullName = MutableStateFlow("Loading...")
    val userFullName: StateFlow<String> = _userFullName.asStateFlow()

    private val _userEmail = MutableStateFlow("Loading...")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl.asStateFlow()

    init {
        loadGoogleProfile()
    }

    /**
     * Pulls the 'full_name' and 'avatar_url' directly from the
     * Google Metadata stored in the current Supabase session.
     */
    fun loadGoogleProfile() {
        viewModelScope.launch {
            val user = supabase.auth.currentUserOrNull()
            user?.let {
                // Google metadata keys are standard in Supabase
                val name = it.userMetadata?.get("full_name")?.toString() ?: "User"
                val avatar = it.userMetadata?.get("avatar_url")?.toString()
                val email = it.email ?: ""

                _userFullName.value = name
                _profilePictureUrl.value = avatar
                _userEmail.value = email
            }
        }
    }

    // Add this inside your ProfileViewModel class in ProfileViewModel.kt
    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Function for manual refresh if needed
    fun refreshProfile() {
        loadGoogleProfile()
    }
}
