package com.example.ui.screens.partner

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
import com.example.data.model.FoodItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.VegNonVegBadge
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantPartnerScreen(
    orders: List<Order>,
    foodItems: List<FoodItem>,
    onToggleItemStock: (String) -> Unit,
    onAdvanceOrderStatus: (String) -> Unit,
    onAddNewFoodItem: (FoodItem) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var addDishModalOpen by remember { mutableStateOf(false) }

    var newDishName by remember { mutableStateOf("") }
    var newDishPrice by remember { mutableStateOf("") }
    var newDishCategory by remember { mutableStateOf("Starters") }
    var newDishIsVeg by remember { mutableStateOf(true) }
    var newDishDesc by remember { mutableStateOf("") }

    val tabs = listOf("Kitchen Orders", "Menu Inventory", "Sales Analytics")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Partner Header Bar
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Meghana Foods Partner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VegGreen
                            ) {
                                Text(
                                    text = "LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Koramangala 5th Block · Kitchen Terminal",
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

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PartnerStatBox(title = "Today's Orders", value = "${orders.size + 14}", modifier = Modifier.weight(1f))
                    PartnerStatBox(title = "Today's Revenue", value = "₹12,450", modifier = Modifier.weight(1f))
                    PartnerStatBox(title = "Avg Prep Time", value = "22 mins", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = FoodHubOrange
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Kitchen Orders Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(orders) { order ->
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
                                            text = "Order #${order.orderNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Placed at: ${order.placedAtTime}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (order.status) {
                                            OrderStatus.DELIVERED -> VegGreen.copy(alpha = 0.15f)
                                            else -> FoodHubOrange.copy(alpha = 0.15f)
                                        }
                                    ) {
                                        Text(
                                            text = order.status.title.uppercase(),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = when (order.status) {
                                                OrderStatus.DELIVERED -> VegGreen
                                                else -> FoodHubOrange
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                // Items List
                                order.items.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            VegNonVegBadge(isVeg = item.foodItem.isVeg)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${item.quantity}x ${item.foodItem.name}",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Text(
                                            text = "₹${item.totalPrice.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (item.specialInstructions.isNotEmpty()) {
                                        Text(
                                            text = "Special request: ${item.specialInstructions}",
                                            fontSize = 11.sp,
                                            color = FoodHubOrange,
                                            modifier = Modifier.padding(start = 24.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Bill: ₹${order.totalAmount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    if (order.status != OrderStatus.DELIVERED) {
                                        Button(
                                            onClick = { onAdvanceOrderStatus(order.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = when (order.status) {
                                                    OrderStatus.ORDER_PLACED -> "Accept Order"
                                                    OrderStatus.RESTAURANT_ACCEPTED -> "Start Cooking"
                                                    OrderStatus.PREPARING_FOOD -> "Mark Ready for Pickup"
                                                    else -> "Update Status"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Menu Inventory Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MENU INVENTORY (${foodItems.size} items)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { addDishModalOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Dish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(foodItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    VegNonVegBadge(isVeg = item.isVeg)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            text = "${item.category} · ₹${item.price.toInt()}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (item.isAvailable) "In Stock" else "Out of Stock",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isAvailable) VegGreen else Color.Red
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = item.isAvailable,
                                        onCheckedChange = { onToggleItemStock(item.id) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = VegGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Sales Analytics Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "WEEKLY REVENUE BREAKDOWN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                val days = listOf("Mon" to 14200, "Tue" to 11800, "Wed" to 13500, "Thu" to 15200, "Fri" to 19800, "Sat" to 24500, "Sun" to 28900)
                                days.forEach { (d, rev) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = d, fontSize = 13.sp)
                                        Text(text = "₹$rev", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Dish Dialog
    if (addDishModalOpen) {
        AlertDialog(
            onDismissRequest = { addDishModalOpen = false },
            title = { Text("Add New Food Item", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newDishName,
                        onValueChange = { newDishName = it },
                        placeholder = { Text("Dish Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDishPrice,
                        onValueChange = { newDishPrice = it },
                        placeholder = { Text("Price in ₹") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDishCategory,
                        onValueChange = { newDishCategory = it },
                        placeholder = { Text("Category (e.g. Biryani, Starters)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Vegetarian?")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = newDishIsVeg, onCheckedChange = { newDishIsVeg = it })
                    }
                    OutlinedTextField(
                        value = newDishDesc,
                        onValueChange = { newDishDesc = it },
                        placeholder = { Text("Description & Ingredients") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = newDishPrice.toDoubleOrNull() ?: 200.0
                        if (newDishName.isNotBlank()) {
                            onAddNewFoodItem(
                                FoodItem(
                                    id = "food_${System.currentTimeMillis()}",
                                    restaurantId = "rest_1",
                                    name = newDishName.trim(),
                                    description = newDishDesc.trim(),
                                    price = p,
                                    originalPrice = p * 1.15,
                                    isVeg = newDishIsVeg,
                                    category = newDishCategory.trim(),
                                    imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500"
                                )
                            )
                            addDishModalOpen = false
                            newDishName = ""
                            newDishPrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)
                ) {
                    Text("Add Dish", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { addDishModalOpen = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PartnerStatBox(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
