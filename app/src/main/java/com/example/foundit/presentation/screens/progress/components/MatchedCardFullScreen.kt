package com.example.foundit.presentation.screens.progress.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.foundit.presentation.screens.progress.MatchedCardFullScreenViewModel
import com.example.foundit.ui.theme.LogoColor
import com.example.foundit.ui.theme.RobotFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MatchedCardFullScreen(
    modifier: Modifier = Modifier,
    cardId: String,
    navController: NavHostController
) {
    val viewModel: MatchedCardFullScreenViewModel = hiltViewModel()
    val cardData by viewModel.cardData.collectAsState()

    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomConfirmButton by remember { mutableStateOf(false) }

    LaunchedEffect(notificationPermissionState) {
        if (!notificationPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(cardId) {
        viewModel.fetchCardData(cardId)
    }

    cardData?.let { data ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 2.dp, end = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
            }

            Spacer(modifier = modifier.height(8.dp))

            Column(modifier = modifier.padding(horizontal = 16.dp)) {
                Column(modifier = modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = data["parentCategory"]?.toString() ?: "Unknown",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    HorizontalDivider(modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier
                                .width(IntrinsicSize.Max)
                                .height(IntrinsicSize.Max),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Close",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier.width(4.dp))
                            Text(
                                text = (data["locationAddress"]?.toString() ?: "Unknown location").take(26),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = data["date"]?.toString() ?: "No date available",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 240.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Item Description",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = data["cardDescription"]?.toString()
                                ?: "No description available",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    ElevatedButton(
                        onClick = {
                            if (!notificationPermissionState.status.isGranted) {
                                notificationPermissionState.launchPermissionRequest()
                            }

                            showBottomSheet = true

                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = LogoColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Contact",
                            fontSize = 18.sp,
                        )
                    }


                    if (showBottomSheet) {
                        ModalBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = { showBottomSheet = false },
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {

                                // Name Row
                                Row(
                                    modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Name:",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Bold,
                                        modifier = modifier.padding(end = 8.dp)
                                    )


                                    Text(
                                        text = "Musaib Shabir",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal
                                    )
                                }

                                // Country Row
                                Row(
                                    modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Country:",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Bold,
                                        modifier = modifier.padding(end = 8.dp)
                                    )


                                    Text(
                                        text = "India",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal
                                    )
                                }

                                // Email Row
                                Row(
                                    modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Email:",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Bold,
                                        modifier = modifier.padding(end = 8.dp)
                                    )


                                    Text(
                                        text = "itzmusaibShabir@gmail.com",
                                        fontSize = 16.sp,
                                        fontFamily = RobotFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal
                                    )
                                }

                            }
                        }
                        showBottomConfirmButton = true

                    }

                }


                if (showBottomConfirmButton) {
                    Column(
                        modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Does this item belong to the below person",
                                fontSize = 16.sp,
                                fontFamily = RobotFamily,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic
                            )

                        }

                        Spacer(modifier.height(16.dp))

                        Row(
                            modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedCard(
                                modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max),
                                shape = RoundedCornerShape(38.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {

                                Row(
                                    modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        modifier = modifier.width(112.dp),
                                        onClick = {
                                            showBottomConfirmButton = false
                                        }
                                    ) {
                                        Text(
                                            text = "No",
                                            fontSize = 16.sp,
                                            fontFamily = RobotFamily,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }

                                    Button(
                                        modifier = modifier.width(112.dp),
                                        onClick = {
                                            viewModel.contactUser(cardId) { isSuccessfull ->
                                                if (isSuccessfull) {
                                                    showBottomConfirmButton = false
                                                    navController.popBackStack()
                                                } else {
                                                    // Todo: Handle failure
                                                }
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = "Yes",
                                            fontSize = 16.sp,
                                            fontFamily = RobotFamily,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }


                            }


                        }
                    }
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
@Preview(showBackground = true, showSystemUi = false)
fun PreviewMatchedCardFullScreen() {
    MatchedCardFullScreen(
        modifier = Modifier,
        cardId = "1",
        navController = NavHostController(LocalContext.current)
    )
}
