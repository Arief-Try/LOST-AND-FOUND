package com.example.foundit.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _userFullName = MutableStateFlow("Loading...")
    val userFullName: StateFlow<String> = _userFullName.asStateFlow()

    private val _userEmail = MutableStateFlow("Loading...")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl.asStateFlow()

    init {
        loadGoogleProfile()
    }

    fun loadGoogleProfile() {
        viewModelScope.launch {
            // 1. Initial load from local session
            supabase.auth.currentUserOrNull()?.let { updateUI(it) }

            // 2. Refresh from server to ensure metadata is populated
            try {
                // Get the current token from the session
                val jwt = supabase.auth.currentAccessTokenOrNull()

                if (jwt != null) {
                    val freshUser = supabase.auth.retrieveUser(jwt)
                    updateUI(freshUser)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(user: UserInfo) {
        val metadata = user.userMetadata

        val name = metadata?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "User"
        val avatar = metadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull
            ?: metadata?.get("picture")?.jsonPrimitive?.contentOrNull
        val email = user.email ?: ""

        _userFullName.value = name
        _profilePictureUrl.value = avatar?.takeIf { it.isNotBlank() && it != "null" }
        _userEmail.value = email
    }

    fun uploadProfilePicture(bytes: ByteArray) {
        viewModelScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull() ?: return@launch
                val bucket = supabase.storage.from("avatars")
                val fileName = "${user.id}_avatar.jpg"

                // Fix: upsert must be inside the options block
                bucket.upload(fileName, bytes) {
                    upsert = true
                }

                val publicUrl = bucket.publicUrl(fileName)

                // 1. Update the user metadata in Auth
                supabase.auth.updateUser {
                    data = buildJsonObject {
                        put("avatar_url", publicUrl)
                    }
                }

                // 2. Update the public 'users' table in your schema
                try {
                    supabase.from("users").update(
                        buildJsonObject {
                            put("avatar_url", publicUrl)
                        }
                    ) {
                        filter {
                            eq("id", user.id)
                        }
                    }
                } catch (dbError: Exception) {
                    dbError.printStackTrace()
                }

                _profilePictureUrl.value = publicUrl
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetToDefaultPicture() {
        viewModelScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull() ?: return@launch
                supabase.auth.updateUser {
                    data = buildJsonObject {
                        put("avatar_url", "")
                    }
                }

                // Also reset in public users table
                supabase.from("users").update(
                    buildJsonObject {
                        put("avatar_url", "")
                    }
                ) {
                    filter { eq("id", user.id) }
                }

                _profilePictureUrl.value = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshProfile() {
        loadGoogleProfile()
    }
}
