package io.hangout.guide.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.hangout.guide.data.model.UserPreferences
import io.hangout.guide.data.model.UserProfile
import io.hangout.guide.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthManager,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUserProfile() {
        val userId = auth.getCurrentUser()?.uid ?: return
        _isLoading.value = true

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                _isLoading.value = false
                if (document.exists()) {
                    val user = document.toObject(UserProfile::class.java)
                    _userProfile.value = user
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _error.value = exception.message
                Timber.tag("ProfileViewModel").e(exception, "Error loading user profile")
            }
    }

    fun createUserProfile(uid: String, name: String, email: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Upload image if provided
                val profileImageUrl = imageUri?.let { uploadImage(it) }

                // Create user profile
                val userProfile = UserProfile(
                    name = name,
                    email = email,
                    profileImage = profileImageUrl ?: "",
                    preferences = UserPreferences() // Default preferences
                )

                // Save to Firestore
                firestore.collection("users").document(uid)
                    .set(userProfile)
                    .await()

                _userProfile.value = userProfile
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
                Timber.tag("ProfileViewModel").e(e, "Error creating user profile")
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String {
        return try {
            val userId = auth.getCurrentUser()?.uid ?: throw IllegalStateException("User not authenticated")
            val fileName = "profile_images/$userId/${UUID.randomUUID()}"
            val storageRef = storage.reference.child(fileName)

            // Upload file
            val uploadTask = storageRef.putFile(imageUri).await()

            // Get download URL
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.message}")
        }
    }

    fun updateUserProfile(name: String, email: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.getCurrentUser()?.uid ?: throw IllegalStateException("User not authenticated")

                // Upload new image if provided
                val profileImageUrl = imageUri?.let { uploadImage(it) }

                val userUpdates = mutableMapOf<String, Any>(
                    "name" to name,
                    "email" to email
                )

                // Only update image if a new one was uploaded
                profileImageUrl?.let { userUpdates["profileImage"] = it }

                // Update Firestore
                firestore.collection("users").document(userId)
                    .update(userUpdates)
                    .await()

                // Update local state
                _userProfile.value = _userProfile.value?.copy(
                    name = name,
                    email = email,
                    profileImage = profileImageUrl ?: _userProfile.value?.profileImage ?: ""
                )

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
                Timber.tag("ProfileViewModel").e(e, "Error updating profile")
            }
        }
    }

    fun updateUserPreferences(preferences: UserPreferences) {
        val userId = auth.getCurrentUser()?.uid ?: return

        // Convertimos a un mapa y filtramos valores nulos
        val updateMap = mutableMapOf<String, Any>()
        preferences.museums?.let { updateMap["preferences.museums"] = it }
        preferences.restaurants?.let { updateMap["preferences.restaurants"] = it }
        preferences.parks?.let { updateMap["preferences.parks"] = it }
        preferences.bars?.let { updateMap["preferences.bars"] = it }

        if (updateMap.isNotEmpty()) { // Solo actualizamos si hay valores válidos
            firestore.collection("users").document(userId).update(updateMap)
                .addOnSuccessListener {
                    _userProfile.value = _userProfile.value?.preferences?.copy(
                        museums = preferences.museums ?: _userProfile.value?.preferences?.museums,
                        restaurants = preferences.restaurants ?: _userProfile.value?.preferences?.restaurants,
                        parks = preferences.parks ?: _userProfile.value?.preferences?.parks,
                        bars = preferences.bars ?: _userProfile.value?.preferences?.bars
                    )?.let { it1 ->
                        _userProfile.value?.copy(
                            preferences = it1
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag("ProfileViewModel").e(exception, "Error updating preferences")
                }
        }
    }

    fun clearError() {
        _error.value = null
    }
}