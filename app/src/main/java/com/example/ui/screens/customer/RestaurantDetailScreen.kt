package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.model.Restaurant
import com.example.ui.components.CustomizationBottomSheet
import com.example.ui.components.FoodItemCard
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    allFoodItems: List<FoodItem>,
    cartItems: List<CartItem>,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    onOpenCustomizer: (FoodItem) -> Unit,
    onIncreaseCartItem: (String) -> Unit,
    onDecreaseCartItem: (String) -> Unit,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var vegOnly by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val restaurantItems = remember(allFoodItems, restaurant.id) {
        allFoodItems.filter { it.restaurantId == restaurant.id }
    }

    val availableCategories = remember(restaurantItems) {
        listOf("All") + restaurantItems.map { it.category }.distinct()
    }

    val filteredItems = remember(restaurantItems, searchQuery, vegOnly, selectedCategory) {
        restaurantItems.filter { item ->
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)
            val matchesVeg = if (vegOnly) item.isVeg else true
            val matchesCat = if (selectedCategory == "All") true else item.category == selectedCategory
            matchesSearch && matchesVeg && matchesCat
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 90.dp else 24.dp)
        ) {
            // Header Image with Back and Favorite Buttons
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = restaurant.coverUrl,
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Top Bar controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .testTag("restaurant_back_button")
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) FoodHubOrange else Color.White
                            )
                        }
                    }
                }
            }

            // Restaurant Details Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-16).dp),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = restaurant.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = restaurant.cuisines.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = restaurant.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Surface(
                                color = VegGreen,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${restaurant.rating}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Delivery & Cost Info Pill Row
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = FoodHubOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${restaurant.deliveryTimeMinutes} mins",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = FoodHubOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${restaurant.distanceKm} km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyRupee,
                                        contentDescription = null,
                                        tint = FoodHubOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "₹${restaurant.costForTwo} for two",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        if (restaurant.featuredPromo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = FoodHubOrange.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Discount,
                                        contentDescription = null,
                                        tint = FoodHubOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = restaurant.featuredPromo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = FoodHubOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Menu Search & Veg Toggle
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search dishes in this menu...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = FoodHubOrange)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("menu_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(availableCategories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FoodHubOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Veg Only Filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { vegOnly = !vegOnly }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Veg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (vegOnly) VegGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = vegOnly,
                                onCheckedChange = { vegOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = VegGreen)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Dishes List
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No dishes found matching your criteria",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredItems) { foodItem ->
                    val matchingCartItem = cartItems.find { it.foodItem.id == foodItem.id }
                    val currentQty = matchingCartItem?.quantity ?: 0

                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        FoodItemCard(
                            foodItem = foodItem,
                            quantityInCart = currentQty,
                            onAddToCart = {
                                if (foodItem.portionSizes.isNotEmpty() || foodItem.addOns.isNotEmpty()) {
                                    onOpenCustomizer(foodItem)
                                } else {
                                    onAddToCart(foodItem)
                                }
                            },
                            onIncrease = {
                                if (matchingCartItem != null) {
                                    onIncreaseCartItem(matchingCartItem.id)
                                } else {
                                    onAddToCart(foodItem)
                                }
                            },
                            onDecrease = {
                                if (matchingCartItem != null) {
                                    onDecreaseCartItem(matchingCartItem.id)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Bottom Cart Banner
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            val totalItems = cartItems.sumOf { it.quantity }
            val totalPrice = cartItems.sumOf { it.totalPrice }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("restaurant_detail_cart_banner")
                    .clickable { onViewCart() },
                shape = RoundedCornerShape(16.dp),
                color = FoodHubOrange,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$totalItems ${if (totalItems == 1) "ITEM" else "ITEMS"} IN CART",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "₹${totalPrice.toInt()} plus taxes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Cart",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
