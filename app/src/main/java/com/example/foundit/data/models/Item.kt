package com.example.foundit.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    // bigint in SQL maps to Long in Kotlin
    val id: Long? = null,

    // uuid in SQL is handled as a String in Kotlin
    val user_id: String,

    // text in SQL
    val item_type: String,

    // Making these nullable (?) prevents crashes if a row has empty data
    val description: String? = null,
    val category: String? = null,
    val location: String? = null,
    val phone_number: String, // Add this line
    val image_url: String? = null,

    // Timestamps are handled as Strings by the Supabase serializer
    val created_at: String? = null
)