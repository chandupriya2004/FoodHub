package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.model.Restaurant
import com.example.ui.components.FoodItemCard
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.FoodHubOrange

@Composable
fun SearchScreen(
    restaurants: List<Restaurant>,
    foodItems: List<FoodItem>,
    favoriteRestaurantIds: Set<String>,
    cartItems: List<CartItem>,
    onToggleFavorite: (String) -> Unit,
    onRestaurantClick: (Restaurant) -> Unit,
    onAddToCart: (FoodItem, Restaurant) -> Unit,
    onOpenCustomizer: (FoodItem) -> Unit,
    onIncreaseCartItem: (String) -> Unit,
    onDecreaseCartItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var vegOnly by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    val trendingKeywords = listOf(
        "Hyderabadi Biryani",
        "Ghee Dosa",
        "Cheese Burger",
        "Butter Chicken",
        "Sourdough Pizza",
        "Ferrero Shake",
        "Salad Bowl"
    )

    // Matching Restaurants
    val matchingRestaurants = remember(restaurants, query, vegOnly, selectedFilter) {
        if (query.trim().isEmpty()) emptyList()
        else {
            restaurants.filter { rest ->
                val matchesText = rest.name.contains(query, ignoreCase = true) ||
                        rest.cuisines.any { it.contains(query, ignoreCase = true) }
                val matchesVeg = if (vegOnly) rest.isPureVeg else true
                matchesText && matchesVeg
            }
        }
    }

    // Matching Dishes
    val matchingDishes = remember(foodItems, query, vegOnly, selectedFilter) {
        if (query.trim().isEmpty()) emptyList()
        else {
            foodItems.filter { dish ->
                val matchesText = dish.name.contains(query, ignoreCase = true) ||
                        dish.description.contains(query, ignoreCase = true) ||
                        dish.category.contains(query, ignoreCase = true)
                val matchesVeg = if (vegOnly) dish.isVeg else true
                matchesText && matchesVeg
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search dishes, restaurants, cuisines...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = FoodHubOrange)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = vegOnly,
                        onClick = { vegOnly = !vegOnly },
                        label = { Text("Pure Veg", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FoodHubOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "4.5+",
                        onClick = { selectedFilter = if (selectedFilter == "4.5+") "All" else "4.5+" },
                        label = { Text("Rating 4.5+", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FoodHubOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Body Content
        if (query.trim().isEmpty()) {
            // Trending Searches
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = FoodHubOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POPULAR & TRENDING SEARCHES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trendingKeywords) { keyword ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.clickable { query = keyword }
                        ) {
                            Text(
                                text = keyword,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else if (matchingRestaurants.isEmpty() && matchingDishes.isEmpty()) {
            // No Results State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No results found for \"$query\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try checking the spelling or searching for another dish or cuisine",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Restaurants Section
                if (matchingRestaurants.isNotEmpty()) {
                    item {
                        Text(
                            text = "RESTAURANTS (${matchingRestaurants.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                    }

                    items(matchingRestaurants) { rest ->
                        RestaurantCard(
                            restaurant = rest,
                            isFavorite = favoriteRestaurantIds.contains(rest.id),
                            onToggleFavorite = { onToggleFavorite(rest.id) },
                            onClick = { onRestaurantClick(rest) }
                        )
                    }
                }

                // Dishes Section
                if (matchingDishes.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DISHES (${matchingDishes.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                    }

                    items(matchingDishes) { dish ->
                        val matchingCartItem = cartItems.find { it.foodItem.id == dish.id }
                        val currentQty = matchingCartItem?.quantity ?: 0
                        val parentRest = restaurants.find { it.id == dish.restaurantId } ?: restaurants.first()

                        FoodItemCard(
                            foodItem = dish,
                            quantityInCart = currentQty,
                            onAddToCart = {
                                if (dish.portionSizes.isNotEmpty() || dish.addOns.isNotEmpty()) {
                                    onOpenCustomizer(dish)
                                } else {
                                    onAddToCart(dish, parentRest)
                                }
                            },
                            onIncrease = {
                                if (matchingCartItem != null) {
                                    onIncreaseCartItem(matchingCartItem.id)
                                } else {
                                    onAddToCart(dish, parentRest)
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
    }
}
