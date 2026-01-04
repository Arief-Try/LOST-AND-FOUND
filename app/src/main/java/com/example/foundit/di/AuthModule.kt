package com.example.foundit.di

import android.content.Context
import com.example.foundit.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        // Configuration for Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("785692989572-9so8qs42vdfrmdppafapri60pnqa2gm5.apps.googleusercontent.com") // <--- Crucial: The ID from GCP
            .requestEmail() // Asks the user for permission to see their email
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}