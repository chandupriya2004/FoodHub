package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.components.LiveMapCanvas
import com.example.ui.theme.DeliveryBlue
import com.example.ui.theme.FoodHubGold
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    order: Order,
    onBack: () -> Unit,
    onAdvanceStep: (String) -> Unit,
    onRateOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var riderChatOpen by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    val riderChatMessages = remember {
        mutableStateListOf(
            Pair("rider", "Hello! I have picked up your order and am driving towards Koramangala."),
            Pair("user", "Please leave the parcel at the door, thanks!")
        )
    }
    var helpModalOpen by remember { mutableStateOf(false) }
    var callSnackbarOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Order Tracking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "#${order.orderNumber} · ${order.restaurant.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { helpModalOpen = true }) {
                        Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Interactive Map
            item {
                LiveMapCanvas(
                    restaurantName = order.restaurant.name,
                    customerLocationName = order.deliveryAddress.title,
                    riderProgress = order.riderProgress
                )
            }

            // OTP Banner
            if (order.status != OrderStatus.DELIVERED) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = FoodHubOrange.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FoodHubOrange)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DELIVERY PIN / OTP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FoodHubOrange,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Share this 4-digit PIN with your rider on arrival",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FoodHubOrange
                            ) {
                                Text(
                                    text = order.deliveryOtp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    letterSpacing = 3.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Status Timeline Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = order.status.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (order.status == OrderStatus.DELIVERED) VegGreen else FoodHubOrange
                                )
                                Text(
                                    text = order.status.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Simulation Step Advance Button for Tester/User
                            FilledTonalButton(
                                onClick = { onAdvanceStep(order.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = FoodHubOrange.copy(alpha = 0.15f)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("simulate_next_status_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = null,
                                    tint = FoodHubOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (order.status == OrderStatus.DELIVERED) "Completed" else "Next Step",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FoodHubOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Timeline stages
                        val stages = listOf(
                            Pair(OrderStatus.ORDER_PLACED, "Order Received by Kitchen"),
                            Pair(OrderStatus.RESTAURANT_ACCEPTED, "Restaurant Accepted"),
                            Pair(OrderStatus.PREPARING_FOOD, "Food is Being Prepared"),
                            Pair(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery"),
                            Pair(OrderStatus.DELIVERED, "Delivered to Customer")
                        )

                        stages.forEachIndexed { index, (stage, label) ->
                            val isReached = order.status.ordinal >= stage.ordinal
                            val isCurrent = order.status == stage

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isReached) FoodHubOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isReached) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isReached) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (index < stages.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 11.dp)
                                        .width(2.dp)
                                        .height(14.dp)
                                        .background(
                                            if (order.status.ordinal > stage.ordinal) FoodHubOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Delivery Partner Info Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DeliveryBlue.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = null,
                                        tint = DeliveryBlue,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = order.deliveryPartnerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = FoodHubGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${order.deliveryPartnerRating} · 850+ deliveries",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = order.deliveryPartnerVehicle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Call & Chat buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { callSnackbarOpen = true },
                                modifier = Modifier
                                    .background(VegGreen.copy(alpha = 0.15f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Rider",
                                    tint = VegGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { riderChatOpen = true },
                                modifier = Modifier
                                    .background(FoodHubOrange.copy(alpha = 0.15f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Chat with Rider",
                                    tint = FoodHubOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Order Items & Receipt Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ORDER DETAILS (${order.items.sumOf { it.quantity }} items)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.foodItem.name}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "₹${item.totalPrice.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Paid (${order.paymentMethod})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "₹${order.totalAmount.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = FoodHubOrange
                            )
                        }

                        if (order.status == OrderStatus.DELIVERED) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onRateOrder(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rate and Review Order", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Call Rider Dialog
    if (callSnackbarOpen) {
        AlertDialog(
            onDismissRequest = { callSnackbarOpen = false },
            title = { Text("Connecting to Delivery Partner") },
            text = { Text("Calling ${order.deliveryPartnerName} on ${order.deliveryPartnerPhone} via secure masked number call bridge.") },
            confirmButton = {
                Button(onClick = { callSnackbarOpen = false }, colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)) {
                    Text("OK")
                }
            }
        )
    }

    // Rider Chat Modal
    if (riderChatOpen) {
        ModalBottomSheet(
            onDismissRequest = { riderChatOpen = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = DeliveryBlue.copy(alpha = 0.2f), modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = DeliveryBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Chat with ${order.deliveryPartnerName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Delivery Partner", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { riderChatOpen = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    riderChatMessages.forEach { (sender, text) ->
                        val isMe = sender == "user"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isMe) FoodHubOrange else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Message rider...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                riderChatMessages.add(Pair("user", chatInput.trim()))
                                chatInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(FoodHubOrange, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }

    // Help Center Modal
    if (helpModalOpen) {
        AlertDialog(
            onDismissRequest = { helpModalOpen = false },
            title = { Text("24x7 Customer Support") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Order #${order.orderNumber}")
                    Text("• Track live ETA or route updates\n• Modify delivery address or instructions\n• Report missing or spilled items\n• Instant refund request to FoodHub Wallet")
                }
            },
            confirmButton = {
                Button(
                    onClick = { helpModalOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
