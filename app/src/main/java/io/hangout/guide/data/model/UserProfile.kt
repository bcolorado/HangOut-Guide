package io.hangout.guide.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    val preferences: UserPreferences = UserPreferences()
)

@Serializable
data class UserPreferences(
    val museums: Boolean? = null,
    val restaurants: Boolean? = null,
    val parks: Boolean? = null,
    val bars: Boolean? = null
)