package com.example.foundit.presentation.screens.input.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.* // This * covers all Compose state functions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.foundit.presentation.screens.input.lost.LostInputViewModel
import com.example.foundit.ui.theme.MainGreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(
    modifier: Modifier = Modifier,
    datePickerTitle: String,
    viewModel: LostInputViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Collecting states from the ViewModel
    val selectedDateString by viewModel.selectedDateString.collectAsState()
    val selectedDateMillis by viewModel.selectedDateMillis.collectAsState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis() // Prevents future dates
            }
        }
    )

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDateString,
            onValueChange = {},
            label = { Text("Date") },
            placeholder = { Text("Tap to select date", fontStyle = FontStyle.Italic) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date", tint = MainGreen)
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MainGreen,
                cursorColor = MainGreen
            )
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onDateSelected(selectedLocalDate)
                        }
                        showDatePicker = false
                    }) { Text("Confirm", color = MainGreen) }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = { Text(text = datePickerTitle, modifier = Modifier.padding(16.dp)) }
                )
            }
        }
    }
}

