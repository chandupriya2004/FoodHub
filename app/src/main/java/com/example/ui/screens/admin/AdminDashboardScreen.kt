package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Coupon
import com.example.data.model.Restaurant
import com.example.ui.theme.DeliveryBlue
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    restaurants: List<Restaurant>,
    coupons: List<Coupon>,
    supportTickets: List<String>,
    onToggleRestaurant: (String) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var createCouponModalOpen by remember { mutableStateOf(false) }
    var newCouponCode by remember { mutableStateOf("") }
    var newCouponDiscount by remember { mutableStateOf("25") }
    var newCouponMax by remember { mutableStateOf("100") }
    var newCouponMin by remember { mutableStateOf("299") }
    val localCoupons = remember { mutableStateListOf<Coupon>().apply { addAll(coupons) } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = FoodHubOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FoodHub Admin Central",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Real-time Platform Operations & Financial Overview",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
        }

        // Platform KPI Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard(title = "Gross Merchandise Value", value = "₹2,48,500", color = FoodHubOrange, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Platform Commission (15%)", value = "₹37,275", color = VegGreen, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard(title = "Total Orders Delivered", value = "1,248", color = DeliveryBlue, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Active Delivery Fleet", value = "32 Riders", color = Color(0xFF673AB7), modifier = Modifier.weight(1f))
                }
            }
        }

        // Restaurant Approvals & Status
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ONBOARDED RESTAURANTS (${restaurants.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    restaurants.forEach { rest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = rest.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${rest.cuisines.firstOrNull()} · Rating ${rest.rating}★",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (rest.isOpen) "Accepting Orders" else "Temporarily Closed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rest.isOpen) VegGreen else Color.Red
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(
                                    checked = rest.isOpen,
                                    onCheckedChange = { onToggleRestaurant(rest.id) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = VegGreen)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Coupon Campaign Manager
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            text = "ACTIVE COUPON CAMPAIGNS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )

                        Button(
                            onClick = { createCouponModalOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Coupon", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    localCoupons.forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    color = FoodHubOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = c.code,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = FoodHubOrange,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(text = c.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(
                                text = "Min ₹${c.minOrder.toInt()}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Support Tickets
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CUSTOMER COMPLAINTS & TICKETS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    supportTickets.forEach { t ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = t, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }

    // Create Coupon Modal
    if (createCouponModalOpen) {
        AlertDialog(
            onDismissRequest = { createCouponModalOpen = false },
            title = { Text("Create Promo Coupon", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCouponCode,
                        onValueChange = { newCouponCode = it.uppercase() },
                        placeholder = { Text("Code (e.g. FLASH30)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCouponDiscount,
                        onValueChange = { newCouponDiscount = it },
                        placeholder = { Text("Discount %") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCouponMax,
                        onValueChange = { newCouponMax = it },
                        placeholder = { Text("Max Discount in ₹") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCouponMin,
                        onValueChange = { newCouponMin = it },
                        placeholder = { Text("Min Order in ₹") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCouponCode.isNotBlank()) {
                            val disc = newCouponDiscount.toIntOrNull() ?: 20
                            val max = newCouponMax.toDoubleOrNull() ?: 100.0
                            val min = newCouponMin.toDoubleOrNull() ?: 249.0
                            localCoupons.add(
                                Coupon(
                                    code = newCouponCode.trim(),
                                    title = "$disc% OFF Up to ₹${max.toInt()}",
                                    description = "Valid on orders above ₹${min.toInt()}",
                                    discountPercent = disc,
                                    maxDiscount = max,
                                    minOrder = min
                                )
                            )
                            createCouponModalOpen = false
                            newCouponCode = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)
                ) {
                    Text("Create Coupon", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { createCouponModalOpen = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
