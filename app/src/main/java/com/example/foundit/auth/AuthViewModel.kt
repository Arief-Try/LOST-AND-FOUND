package com.example.foundit.auth

import io.github.jan.supabase.auth.providers.Google
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    // 1. Returns the "Google Sign In" intent to show the popup
    fun getSignInIntent() = googleSignInClient.signInIntent

    // 2. Sends the Google Token to Supabase to log the user in
    fun signInWithSupabase(idTokenString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // This "signs in" the user in the background
                supabaseClient.auth.signInWith(IDToken) {
                    idToken = idTokenString
                    // 2. ADD THIS LINE: This fixes the "provider required" error
                    provider = Google
                }
                onResult(true) // Success!
            } catch (e: Exception) {
                // ADD THIS LINE to see the real reason in Logcat
                Log.e("AUTH_ERROR", "Supabase Error: ${e.localizedMessage}")
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}