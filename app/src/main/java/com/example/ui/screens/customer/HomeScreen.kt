package com.example.ui.screens.customer

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.FoodCategory
import com.example.data.model.Restaurant
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.FoodHubGold
import com.example.ui.theme.FoodHubOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    restaurants: List<Restaurant>,
    categories: List<FoodCategory>,
    favoriteRestaurantIds: Set<String>,
    cartItems: List<CartItem>,
    selectedLocation: String,
    onSelectLocation: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRestaurantClick: (Restaurant) -> Unit,
    onCategoryClick: (FoodCategory) -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var locationMenuExpanded by remember { mutableStateOf(false) }

    val locations = listOf(
        "Koramangala, Bengaluru",
        "Hitech City, Hyderabad",
        "Connaught Place, New Delhi",
        "Bandra West, Mumbai",
        "T Nagar, Chennai"
    )

    val filteredRestaurants = remember(restaurants, selectedFilter) {
        when (selectedFilter) {
            "Pure Veg" -> restaurants.filter { it.isPureVeg }
            "Rating 4.5+" -> restaurants.filter { it.rating >= 4.5 }
            "Fast (<25m)" -> restaurants.filter { it.deliveryTimeMinutes <= 25 }
            "Offers" -> restaurants.filter { it.featuredPromo.isNotEmpty() }
            "Budget" -> restaurants.filter { it.costForTwo <= 350 }
            else -> restaurants
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 90.dp else 24.dp)
        ) {
            // Header Location & Role Bar
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Location Selector
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clickable { locationMenuExpanded = true }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = FoodHubOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = selectedLocation.split(",").firstOrNull() ?: selectedLocation,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = selectedLocation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = locationMenuExpanded,
                                    onDismissRequest = { locationMenuExpanded = false }
                                ) {
                                    locations.forEach { loc ->
                                        DropdownMenuItem(
                                            text = { Text(loc) },
                                            onClick = {
                                                onSelectLocation(loc)
                                                locationMenuExpanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Place,
                                                    contentDescription = null,
                                                    tint = FoodHubOrange
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            // Role Switcher Button & Cart
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = onOpenRoleSwitcher,
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Portal",
                                        modifier = Modifier.size(16.dp),
                                        tint = FoodHubOrange
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Portals",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FoodHubOrange
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Search Trigger Bar
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("home_search_trigger")
                                .clickable { onSearchClick() },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = FoodHubOrange
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Search 'Dum Biryani', 'Burgers' or 'Pizzas'...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Promotional Banners Carousel
            item {
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PromoBanner(
                            title = "FLAT 50% OFF",
                            subtitle = "Up to ₹100 on your favorite Biryanis",
                            code = "FOODHUB50",
                            gradient = listOf(Color(0xFFFF5200), Color(0xFFFF7A3D))
                        )
                    }
                    item {
                        PromoBanner(
                            title = "FREE DELIVERY",
                            subtitle = "Zero delivery charges all week long",
                            code = "FREEDEL",
                            gradient = listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
                        )
                    }
                    item {
                        PromoBanner(
                            title = "GOURMET CARNIVAL",
                            subtitle = "Flat ₹150 OFF on orders above ₹699",
                            code = "FESTIVE150",
                            gradient = listOf(Color(0xFF2E7D32), Color(0xFF43A047))
                        )
                    }
                }
            }

            // What's on your mind (Categories)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "WHAT'S ON YOUR MIND?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onCategoryClick(category) }
                                .width(74.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(66.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = category.imageUrl,
                                    contentDescription = category.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = category.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                Spacer(modifier = Modifier.height(20.dp))
                val filters = listOf("All", "Pure Veg", "Rating 4.5+", "Fast (<25m)", "Offers", "Budget")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FoodHubOrange,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Section Title
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOP RESTAURANTS NEAR YOU",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${filteredRestaurants.size} places",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Restaurants List
            items(filteredRestaurants) { rest ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    RestaurantCard(
                        restaurant = rest,
                        isFavorite = favoriteRestaurantIds.contains(rest.id),
                        onToggleFavorite = { onToggleFavorite(rest.id) },
                        onClick = { onRestaurantClick(rest) }
                    )
                }
            }
        }

        // Floating Bottom Cart Banner if items exist
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
                    .testTag("floating_cart_banner")
                    .clickable { onCartClick() },
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
                            text = "$totalItems ${if (totalItems == 1) "ITEM" else "ITEMS"} ADDED",
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
                            imageVector = Icons.Default.ArrowForward,
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

@Composable
private fun PromoBanner(
    title: String,
    subtitle: String,
    code: String,
    gradient: List<Color>
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(115.dp),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradient))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "USE: $code",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodHubGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
