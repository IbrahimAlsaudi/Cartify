package com.example.cartify.core.util

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

import javax.inject.Inject

class GoogleSignInHelper @Inject constructor() {
    suspend fun signIn(activity: Activity): Result<String> {
        if (Constants.WEB_CLIENT_ID.isEmpty()) {
            return Result.failure(Exception("Google Web Client ID is not configured"))
        }
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constants.WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(activity)
            val response = credentialManager.getCredential(
                request = request,
                context = activity
            )

            val credential = response.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Result.success(googleIdTokenCredential.idToken)

        } catch (e: NoCredentialException) {
            Log.e("GoogleSignInHelper", "No credentials: ${e.message}")
            Result.failure(Exception("No Google accounts found on this device"))
        } catch (e: GetCredentialCancellationException) {
            Log.w("GoogleSignInHelper", "Sign in cancelled")
            Result.failure(Exception("Sign in cancelled"))
        } catch (e: GetCredentialInterruptedException) {
            Log.e("GoogleSignInHelper", "Interrupted: ${e.message}")
            Result.failure(Exception("Sign in interrupted"))
        } catch (e: GetCredentialProviderConfigurationException) {
            Log.e("GoogleSignInHelper", "Provider configuration error: ${e.message}")
            Result.failure(Exception("Google Sign-In is not properly configured"))
        } catch (e: GetCredentialUnknownException) {
            Log.e("GoogleSignInHelper", "Unknown credential error: ${e.message}")
            Result.failure(Exception("An unknown error occurred during sign in"))
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInHelper", "Credential error: ${e.message}")
            Result.failure(Exception(e.message ?: "Credential error"))
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Unexpected error", e)
            Result.failure(e)
        }
    }
}