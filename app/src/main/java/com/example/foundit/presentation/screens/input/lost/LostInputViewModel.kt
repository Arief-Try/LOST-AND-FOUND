package com.example.foundit.presentation.screens.input.lost

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.time.LocalDate
import java.time.ZoneId

@HiltViewModel
class LostInputViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    // --- State Variables ---
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _itemDescription = MutableStateFlow("")
    val itemDescription: StateFlow<String> = _itemDescription.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _selectedDateString = MutableStateFlow("")
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    private val _cardType = MutableStateFlow(0)
    val cardType: StateFlow<Int> = _cardType.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()
    private var selectedImageBytes: ByteArray? = null

    val categories = listOf("Electronic", "Document", "Bags & Wallet", "Personal", "Other")

    // --- Update Logic ---
    fun updatePhoneNumber(newPhone: String) { _phoneNumber.value = newPhone }
    fun updateCardType(type: Int) { _cardType.value = type }
    fun updateSelectedCategory(category: String) { _selectedCategory.value = category }
    fun updateLocation(newLocation: String) { _location.value = newLocation }
    fun updateItemDescription(newDescription: String) { _itemDescription.value = newDescription }

    /**
     * Updated with Dispatchers.IO to prevent UI Lag (ANR)
     */
    fun onImageSelected(uri: Uri?, context: Context) {
        _selectedImageUri.value = uri
        uri?.let {
            viewModelScope.launch(Dispatchers.IO) { // Move to background thread
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    selectedImageBytes = inputStream?.readBytes()
                    inputStream?.close()
                } catch (e: Exception) {
                    Log.e("ViewModel", "Error reading image bytes", e)
                }
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDateString.value = date.toString()
        _selectedDateMillis.value = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Sends the data to Supabase
     */
    fun insertItem(itemType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = supabase.auth.currentUserOrNull()
                val user_id = user?.id ?: throw Exception("User not logged in")

                var uploadedImageUrl: String? = null

                // 1. Image Upload
                selectedImageBytes?.let { bytes ->
                    val fileName = "item_${System.currentTimeMillis()}.jpg"
                    val bucket = supabase.storage.from("item-images")
                    bucket.upload(fileName, bytes)
                    uploadedImageUrl = bucket.publicUrl(fileName)
                }

                // 2. Data Insertion
                val newItem = Item(
                    user_id = user_id,
                    item_type = itemType,
                    description = _itemDescription.value,
                    category = _selectedCategory.value,
                    location = _location.value,
                    phone_number = _phoneNumber.value,
                    image_url = uploadedImageUrl
                )

                supabase.postgrest["items"].insert(newItem)
                _isSuccess.value = true
                Log.d("Supabase", "Success: Item added to database")

            } catch (e: Exception) {
                _isSuccess.value = false
                Log.e("SupabaseError", "Insert failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isNetworkAvailableViewmodel(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(network)
        return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}