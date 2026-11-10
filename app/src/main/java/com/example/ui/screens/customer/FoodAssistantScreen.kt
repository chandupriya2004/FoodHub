package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiChatMessage
import com.example.data.model.FoodItem
import com.example.data.model.Restaurant
import com.example.ui.components.FoodItemCard
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen
import kotlinx.coroutines.launch

@Composable
fun FoodAssistantScreen(
    messages: List<AiChatMessage>,
    restaurants: List<Restaurant>,
    onSendMessage: (String) -> Unit,
    onAddToCart: (FoodItem, Restaurant) -> Unit,
    onOpenCustomizer: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val promptChips = listOf(
        "🍛 Best Dum Biryani in town",
        "🥗 High-protein veg dinner",
        "💰 Quick meals under ₹200",
        "🍔 Craving gourmet burgers",
        "🍰 Late night desserts & shakes"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Bot Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = FoodHubOrange.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FoodHubOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HubBot · Food Concierge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VegGreen, CircleShape)
                        )
                    }
                    Text(
                        text = "Powered by FoodHub Smart Taste Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.isBot) Alignment.Start else Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (msg.isBot) 4.dp else 18.dp,
                            bottomEnd = if (msg.isBot) 18.dp else 4.dp
                        ),
                        color = if (msg.isBot) MaterialTheme.colorScheme.surface else FoodHubOrange,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (msg.isBot) MaterialTheme.colorScheme.onSurface else Color.White,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                fontSize = 10.sp,
                                color = if (msg.isBot) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    // If Bot suggested dishes
                    if (msg.suggestedItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "RECOMMENDED DISHES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        msg.suggestedItems.forEach { dish ->
                            val parentRest = restaurants.find { it.id == dish.restaurantId } ?: restaurants.first()
                            Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                FoodItemCard(
                                    foodItem = dish,
                                    quantityInCart = 0,
                                    onAddToCart = {
                                        if (dish.portionSizes.isNotEmpty() || dish.addOns.isNotEmpty()) {
                                            onOpenCustomizer(dish)
                                        } else {
                                            onAddToCart(dish, parentRest)
                                        }
                                    },
                                    onIncrease = {},
                                    onDecrease = {}
                                )
                            }
                        }
                    }
                }
            }
        }

        // Prompt suggestions chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(promptChips) { chip ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        onSendMessage(chip)
                    }
                ) {
                    Text(
                        text = chip,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom input bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask HubBot for meal recommendations...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_assistant_input"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .background(FoodHubOrange, CircleShape)
                        .size(46.dp)
                        .testTag("ai_assistant_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
