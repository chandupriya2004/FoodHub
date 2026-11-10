package com.example.ui.screens.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryTask
import com.example.ui.theme.DeliveryBlue
import com.example.ui.theme.FoodHubGold
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerScreen(
    tasks: List<DeliveryTask>,
    isOnline: Boolean,
    onToggleDuty: () -> Unit,
    onUpdateTaskStatus: (String, String) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpModalTaskId by remember { mutableStateOf<String?>(null) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Rider Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = DeliveryBlue.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = DeliveryBlue)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ramesh Kumar (Rider)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = FoodHubGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "4.9★ · Hero Electric (KA 05 EQ 8821)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = onOpenRoleSwitcher,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Portals", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duty Toggle Row
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isOnline) VegGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(if (isOnline) VegGreen else Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOnline) "YOU ARE ONLINE · RECEIVING ORDERS" else "YOU ARE OFFLINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isOnline) VegGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isOnline,
                            onCheckedChange = { onToggleDuty() },
                            colors = SwitchDefaults.colors(checkedThumbColor = VegGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Earnings & Incentive Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Today's Earnings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹540", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = VegGreen)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Trips Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("8 Trips", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Assigned Tasks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "ACTIVE DELIVERY TRIPS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            }

            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending trips right now",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tasks) { task ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${task.orderNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (task.status) {
                                        "DELIVERED" -> VegGreen.copy(alpha = 0.15f)
                                        "PICKED_UP" -> FoodHubOrange.copy(alpha = 0.15f)
                                        else -> DeliveryBlue.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = task.status,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = when (task.status) {
                                            "DELIVERED" -> VegGreen
                                            "PICKED_UP" -> FoodHubOrange
                                            else -> DeliveryBlue
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pickup
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = FoodHubOrange, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Pickup: ${task.restaurantName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = task.restaurantAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Drop
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = VegGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Deliver to: ${task.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = task.customerAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Items: ${task.itemsSummary}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Trip Payout", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "₹${task.earnings.toInt()} · ${task.distanceKm} km", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VegGreen)
                                }

                                when (task.status) {
                                    "ASSIGNED" -> {
                                        Button(
                                            onClick = { onUpdateTaskStatus(task.id, "PICKED_UP") },
                                            colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Confirm Food Picked Up", fontSize = 12.sp, color = Color.White)
                                        }
                                    }
                                    "PICKED_UP" -> {
                                        Button(
                                            onClick = {
                                                otpModalTaskId = task.id
                                                enteredOtp = ""
                                                otpError = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("verify_otp_deliver_button")
                                        ) {
                                            Text("Verify OTP & Complete", fontSize = 12.sp, color = Color.White)
                                        }
                                    }
                                    "DELIVERED" -> {
                                        Text(text = "Delivered Successfully", fontWeight = FontWeight.Bold, color = VegGreen, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // OTP Verification Dialog
    if (otpModalTaskId != null) {
        val task = tasks.find { it.id == otpModalTaskId }
        AlertDialog(
            onDismissRequest = { otpModalTaskId = null },
            title = { Text("Verify Customer OTP", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the 4-digit code provided by the customer at the door (Hint: ${task?.customerOtp}):", fontSize = 13.sp)
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = {
                            enteredOtp = it
                            otpError = null
                        },
                        placeholder = { Text("4-digit OTP") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rider_otp_input"),
                        singleLine = true
                    )
                    if (otpError != null) {
                        Text(text = otpError!!, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (task != null && enteredOtp.trim() == task.customerOtp) {
                            onUpdateTaskStatus(task.id, "DELIVERED")
                            otpModalTaskId = null
                        } else {
                            otpError = "Incorrect OTP. Please ask customer for correct code."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                ) {
                    Text("Verify & Deliver", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { otpModalTaskId = null }) { Text("Cancel") }
            }
        )
    }
}
