package com.example.foundit.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int? = null,
    val user_id: String,
    val item_type: String,
    val description: String,
    val category: String,
    val location: String,
    val phone_number: String,
    val image_url: String? = null,
    val created_at: String? = null
)
