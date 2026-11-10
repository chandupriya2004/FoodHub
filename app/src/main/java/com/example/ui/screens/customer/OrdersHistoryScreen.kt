package com.example.ui.screens.customer

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@Composable
fun OrdersHistoryScreen(
    orders: List<Order>,
    onTrackOrder: (Order) -> Unit,
    onReorder: (Order) -> Unit,
    onRateOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    var invoiceOrder by remember { mutableStateOf<Order?>(null) }

    val activeOrders = remember(orders) {
        orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }
    val pastOrders = remember(orders) {
        orders.filter { it.status == OrderStatus.DELIVERED || it.status == OrderStatus.CANCELLED }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "YOUR ORDERS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Active Orders Section
        if (activeOrders.isNotEmpty()) {
            item {
                Text(
                    text = "ACTIVE DELIVERIES (${activeOrders.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FoodHubOrange,
                    letterSpacing = 0.8.sp
                )
            }

            items(activeOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                            Column {
                                Text(
                                    text = order.restaurant.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "#${order.orderNumber} · ${order.placedAtTime}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FoodHubOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = order.status.title.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = FoodHubOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = order.items.joinToString(", ") { "${it.quantity}x ${it.foodItem.name}" },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${order.totalAmount.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Button(
                                onClick = { onTrackOrder(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("track_active_order_${order.id}")
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Track Live", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Past Orders Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PAST ORDERS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
        }

        if (pastOrders.isEmpty() && activeOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No previous orders found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pastOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                            Column {
                                Text(
                                    text = order.restaurant.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "#${order.orderNumber} · ${order.placedAtTime}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VegGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "DELIVERED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = VegGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = order.items.joinToString(", ") { "${it.quantity}x ${it.foodItem.name}" },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${order.totalAmount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { invoiceOrder = order },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Invoice", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { onRateOrder(order) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Rate", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { onReorder(order) },
                                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Reorder", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Invoice Dialog
    if (invoiceOrder != null) {
        val o = invoiceOrder!!
        AlertDialog(
            onDismissRequest = { invoiceOrder = null },
            title = {
                Text(text = "Tax Invoice - #${o.orderNumber}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "Restaurant: ${o.restaurant.name}", fontWeight = FontWeight.SemiBold)
                    Text(text = "Delivered to: ${o.deliveryAddress.fullAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Payment: ${o.paymentMethod} (PAID)", fontSize = 12.sp, color = VegGreen)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    o.items.forEach { itm ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${itm.quantity}x ${itm.foodItem.name}", fontSize = 12.sp)
                            Text(text = "₹${itm.totalPrice.toInt()}", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Subtotal", fontSize = 12.sp)
                        Text(text = "₹${o.subtotal.toInt()}", fontSize = 12.sp)
                    }
                    if (o.discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Discount", fontSize = 12.sp, color = VegGreen)
                            Text(text = "-₹${o.discount.toInt()}", fontSize = 12.sp, color = VegGreen)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Taxes & Platform Fees", fontSize = 12.sp)
                        Text(text = "₹${(o.taxes + o.platformFee).toInt()}", fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Paid", fontWeight = FontWeight.Bold)
                        Text(text = "₹${o.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = FoodHubOrange)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { invoiceOrder = null }, colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)) {
                    Text("Close")
                }
            }
        )
    }
}
