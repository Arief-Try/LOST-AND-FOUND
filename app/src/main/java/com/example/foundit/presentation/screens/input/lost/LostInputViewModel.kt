package com.example.foundit.presentation.screens.input.lost

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.presentation.data.firestore.FirestoreService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LostInputViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
) : ViewModel() {

    private val _cardType = MutableStateFlow<Int?>(null)
    val cardType: StateFlow<Int?> = _cardType

    fun storeCardType(type: Int) {
        _cardType.value = type
    }

    val categories = listOf("Electronic", "Document", "Bags & Wallet", "Personal", "Other")

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    private val _itemDescription = MutableStateFlow("")
    val itemDescription: StateFlow<String> = _itemDescription.asStateFlow()

    fun updateItemDescription(newDescription: String) {
        if (newDescription.length <= 50) {
            _itemDescription.value = newDescription
        }
    }

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
    }

    fun onSubmitClick(onResult: (Boolean, Exception?) -> Unit) {
        viewModelScope.launch {
            try {
                firestoreService.addCardData(
                    cardType = cardType.value ?: 1,
                    parentCategory = _selectedCategory.value,
                    cardDescription = _itemDescription.value,
                    color = "", // No longer used
                    locationCoordinates = LatLng(0.0, 0.0), // No longer used
                    locationAddress = _location.value,
                    childCategory = "", // No longer used
                    date = selectedDateString.value, // Retaining date for now
                    dateLong = selectedDateMillis.value ?: 0
                )
                onResult(true, null)
            } catch (error: Exception) {
                onResult(false, error)
            }
        }
    }

    fun isNetworkAvailableViewmodel(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    val selectedDateMillis: StateFlow<Long?> = _selectedDateMillis

    private val _selectedDateString = MutableStateFlow("")
    val selectedDateString: StateFlow<String> = _selectedDateString

    fun onDateSelected(localDate: LocalDate) {
        val millis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        _selectedDateMillis.value = millis
        _selectedDateString.value = convertMillisToDate(millis)
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
