package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.data.repository.FoodHubRepository
import com.example.ui.components.CustomizationBottomSheet
import com.example.ui.components.ReviewDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.customer.*
import com.example.ui.screens.delivery.DeliveryPartnerScreen
import com.example.ui.screens.partner.RestaurantPartnerScreen
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.FoodHubTheme

enum class CustomerTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    EXPLORE("Explore", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    SEARCH("Search", Icons.Filled.Search, Icons.Outlined.Search),
    ORDERS("Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    AI_BOT("HubBot", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

enum class CustomerScreenState {
    MAIN_TABS,
    RESTAURANT_DETAIL,
    CART,
    CHECKOUT,
    ORDER_TRACKING
}

@Composable
fun FoodHubApp(
    repository: FoodHubRepository = FoodHubRepository.getInstance()
) {
    val currentRole by repository.currentRole.collectAsStateWithLifecycle()
    val isDarkTheme by repository.isDarkTheme.collectAsStateWithLifecycle()
    val selectedLocation by repository.selectedLocation.collectAsStateWithLifecycle()
    val walletBalance by repository.walletBalance.collectAsStateWithLifecycle()
    val favoriteRestaurantIds by repository.favoriteRestaurantIds.collectAsStateWithLifecycle()
    val restaurants by repository.restaurants.collectAsStateWithLifecycle()
    val foodItems by repository.foodItems.collectAsStateWithLifecycle()
    val cartItems by repository.cartItems.collectAsStateWithLifecycle()
    val cartRestaurant by repository.cartRestaurant.collectAsStateWithLifecycle()
    val appliedCoupon by repository.appliedCoupon.collectAsStateWithLifecycle()
    val driverTip by repository.driverTip.collectAsStateWithLifecycle()
    val deliveryInstruction by repository.deliveryInstruction.collectAsStateWithLifecycle()
    val orders by repository.orders.collectAsStateWithLifecycle()
    val activeTrackingOrderId by repository.activeTrackingOrderId.collectAsStateWithLifecycle()
    val addresses by repository.addresses.collectAsStateWithLifecycle()
    val selectedAddress by repository.selectedAddress.collectAsStateWithLifecycle()
    val deliveryTasks by repository.deliveryTasks.collectAsStateWithLifecycle()
    val isRiderOnline by repository.isRiderOnline.collectAsStateWithLifecycle()
    val chatMessages by repository.chatMessages.collectAsStateWithLifecycle()
    val supportTickets by repository.supportTickets.collectAsStateWithLifecycle()

    var customerTab by remember { mutableStateOf(CustomerTab.EXPLORE) }
    var customerScreenState by remember { mutableStateOf(CustomerScreenState.MAIN_TABS) }
    var viewingRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var customizerFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var ratingOrder by remember { mutableStateOf<Order?>(null) }
    var roleSwitcherOpen by remember { mutableStateOf(false) }

    FoodHubTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentRole) {
                UserRole.RESTAURANT_PARTNER -> {
                    RestaurantPartnerScreen(
                        orders = orders,
                        foodItems = foodItems,
                        onToggleItemStock = { repository.toggleFoodItemAvailability(it) },
                        onAdvanceOrderStatus = { repository.advanceOrderStatus(it) },
                        onAddNewFoodItem = { repository.addFoodItem(it) },
                        onOpenRoleSwitcher = { roleSwitcherOpen = true }
                    )
                }

                UserRole.DELIVERY_PARTNER -> {
                    DeliveryPartnerScreen(
                        tasks = deliveryTasks,
                        isOnline = isRiderOnline,
                        onToggleDuty = { repository.toggleRiderDuty() },
                        onUpdateTaskStatus = { taskId, newStatus ->
                            repository.updateDeliveryTaskStatus(taskId, newStatus)
                        },
                        onOpenRoleSwitcher = { roleSwitcherOpen = true }
                    )
                }

                UserRole.ADMIN -> {
                    AdminDashboardScreen(
                        restaurants = restaurants,
                        coupons = repository.coupons,
                        supportTickets = supportTickets,
                        onToggleRestaurant = { repository.toggleRestaurantActive(it) },
                        onOpenRoleSwitcher = { roleSwitcherOpen = true }
                    )
                }

                UserRole.CUSTOMER -> {
                    Scaffold(
                        bottomBar = {
                            if (customerScreenState == CustomerScreenState.MAIN_TABS) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                                ) {
                                    CustomerTab.values().forEach { tab ->
                                        val isSelected = customerTab == tab
                                        val icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { customerTab = tab },
                                            icon = {
                                                if (tab == CustomerTab.ORDERS && cartItems.isNotEmpty()) {
                                                    BadgedBox(
                                                        badge = {
                                                            Badge(containerColor = FoodHubOrange) {
                                                                Text("${cartItems.sumOf { it.quantity }}")
                                                            }
                                                        }
                                                    ) {
                                                        Icon(imageVector = icon, contentDescription = tab.title)
                                                    }
                                                } else {
                                                    Icon(imageVector = icon, contentDescription = tab.title)
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = tab.title,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = FoodHubOrange,
                                                selectedTextColor = FoodHubOrange,
                                                indicatorColor = FoodHubOrange.copy(alpha = 0.15f)
                                            ),
                                            modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = customerScreenState, label = "customer_nav") { screenState ->
                                when (screenState) {
                                    CustomerScreenState.MAIN_TABS -> {
                                        when (customerTab) {
                                            CustomerTab.EXPLORE -> {
                                                HomeScreen(
                                                    restaurants = restaurants,
                                                    categories = repository.categories,
                                                    favoriteRestaurantIds = favoriteRestaurantIds,
                                                    cartItems = cartItems,
                                                    selectedLocation = selectedLocation,
                                                    onSelectLocation = { repository.setSelectedLocation(it) },
                                                    onToggleFavorite = { repository.toggleFavorite(it) },
                                                    onRestaurantClick = { rest ->
                                                        viewingRestaurant = rest
                                                        customerScreenState = CustomerScreenState.RESTAURANT_DETAIL
                                                    },
                                                    onCategoryClick = { cat ->
                                                        customerTab = CustomerTab.SEARCH
                                                    },
                                                    onSearchClick = { customerTab = CustomerTab.SEARCH },
                                                    onCartClick = { customerScreenState = CustomerScreenState.CART },
                                                    onOpenRoleSwitcher = { roleSwitcherOpen = true }
                                                )
                                            }

                                            CustomerTab.SEARCH -> {
                                                SearchScreen(
                                                    restaurants = restaurants,
                                                    foodItems = foodItems,
                                                    favoriteRestaurantIds = favoriteRestaurantIds,
                                                    cartItems = cartItems,
                                                    onToggleFavorite = { repository.toggleFavorite(it) },
                                                    onRestaurantClick = { rest ->
                                                        viewingRestaurant = rest
                                                        customerScreenState = CustomerScreenState.RESTAURANT_DETAIL
                                                    },
                                                    onAddToCart = { dish, parentRest ->
                                                        repository.addToCart(dish, parentRest)
                                                    },
                                                    onOpenCustomizer = { dish ->
                                                        customizerFoodItem = dish
                                                    },
                                                    onIncreaseCartItem = { repository.updateCartItemQuantity(it, 1) },
                                                    onDecreaseCartItem = { repository.updateCartItemQuantity(it, -1) }
                                                )
                                            }

                                            CustomerTab.ORDERS -> {
                                                OrdersHistoryScreen(
                                                    orders = orders,
                                                    onTrackOrder = { ord ->
                                                        repository.setActiveTrackingOrder(ord.id)
                                                        customerScreenState = CustomerScreenState.ORDER_TRACKING
                                                    },
                                                    onReorder = { ord ->
                                                        repository.reorder(ord)
                                                        viewingRestaurant = ord.restaurant
                                                        customerScreenState = CustomerScreenState.CART
                                                    },
                                                    onRateOrder = { ord ->
                                                        ratingOrder = ord
                                                    }
                                                )
                                            }

                                            CustomerTab.AI_BOT -> {
                                                FoodAssistantScreen(
                                                    messages = chatMessages,
                                                    restaurants = restaurants,
                                                    onSendMessage = { repository.sendUserChatMessage(it) },
                                                    onAddToCart = { dish, parentRest ->
                                                        repository.addToCart(dish, parentRest)
                                                    },
                                                    onOpenCustomizer = { dish ->
                                                        customizerFoodItem = dish
                                                    }
                                                )
                                            }

                                            CustomerTab.PROFILE -> {
                                                ProfileScreen(
                                                    currentRole = currentRole,
                                                    walletBalance = walletBalance,
                                                    addresses = addresses,
                                                    isDarkTheme = isDarkTheme,
                                                    onToggleDarkTheme = { repository.toggleDarkTheme() },
                                                    onOpenRoleSwitcher = { roleSwitcherOpen = true },
                                                    onAddFunds = { repository.addWalletFunds(it) },
                                                    onAddAddress = { repository.addAddress(it) }
                                                )
                                            }
                                        }
                                    }

                                    CustomerScreenState.RESTAURANT_DETAIL -> {
                                        val rest = viewingRestaurant ?: restaurants.first()
                                        RestaurantDetailScreen(
                                            restaurant = rest,
                                            allFoodItems = foodItems,
                                            cartItems = cartItems,
                                            isFavorite = favoriteRestaurantIds.contains(rest.id),
                                            onBack = { customerScreenState = CustomerScreenState.MAIN_TABS },
                                            onToggleFavorite = { repository.toggleFavorite(rest.id) },
                                            onAddToCart = { dish ->
                                                repository.addToCart(dish, rest)
                                            },
                                            onOpenCustomizer = { dish ->
                                                customizerFoodItem = dish
                                            },
                                            onIncreaseCartItem = { repository.updateCartItemQuantity(it, 1) },
                                            onDecreaseCartItem = { repository.updateCartItemQuantity(it, -1) },
                                            onViewCart = { customerScreenState = CustomerScreenState.CART }
                                        )
                                    }

                                    CustomerScreenState.CART -> {
                                        CartScreen(
                                            restaurant = cartRestaurant ?: viewingRestaurant,
                                            cartItems = cartItems,
                                            appliedCoupon = appliedCoupon,
                                            availableCoupons = repository.coupons,
                                            selectedAddress = selectedAddress,
                                            driverTip = driverTip,
                                            deliveryInstruction = deliveryInstruction,
                                            onBack = {
                                                customerScreenState = if (viewingRestaurant != null) {
                                                    CustomerScreenState.RESTAURANT_DETAIL
                                                } else {
                                                    CustomerScreenState.MAIN_TABS
                                                }
                                            },
                                            onIncreaseItem = { repository.updateCartItemQuantity(it, 1) },
                                            onDecreaseItem = { repository.updateCartItemQuantity(it, -1) },
                                            onApplyCoupon = { repository.applyCoupon(it) },
                                            onSelectTip = { repository.setDriverTip(it) },
                                            onSelectInstruction = { repository.setDeliveryInstruction(it) },
                                            onProceedToCheckout = {
                                                customerScreenState = CustomerScreenState.CHECKOUT
                                            }
                                        )
                                    }

                                    CustomerScreenState.CHECKOUT -> {
                                        val sub = cartItems.sumOf { it.totalPrice }
                                        val disc = if (appliedCoupon != null && sub >= appliedCoupon!!.minOrder) {
                                            if (appliedCoupon!!.code == "FREEDEL") 35.0
                                            else minOf((sub * appliedCoupon!!.discountPercent / 100.0), appliedCoupon!!.maxDiscount)
                                        } else 0.0
                                        val delFee = if (appliedCoupon?.code == "FREEDEL") 0.0 else 35.0
                                        val total = maxOf(0.0, (sub - disc) + delFee + 5.0 + (sub * 0.05) + driverTip)

                                        CheckoutScreen(
                                            restaurant = cartRestaurant ?: viewingRestaurant,
                                            totalAmount = total,
                                            selectedAddress = selectedAddress,
                                            availableAddresses = addresses,
                                            walletBalance = walletBalance,
                                            onBack = { customerScreenState = CustomerScreenState.CART },
                                            onSelectAddress = { repository.selectAddress(it) },
                                            onOrderPlaced = { paymentMethod ->
                                                val placedOrder = repository.placeOrder(paymentMethod)
                                                if (placedOrder != null) {
                                                    customerScreenState = CustomerScreenState.ORDER_TRACKING
                                                } else {
                                                    customerScreenState = CustomerScreenState.MAIN_TABS
                                                }
                                            }
                                        )
                                    }

                                    CustomerScreenState.ORDER_TRACKING -> {
                                        val trackingOrder = orders.find { it.id == activeTrackingOrderId } ?: orders.firstOrNull()
                                        if (trackingOrder != null) {
                                            OrderTrackingScreen(
                                                order = trackingOrder,
                                                onBack = {
                                                    customerScreenState = CustomerScreenState.MAIN_TABS
                                                    customerTab = CustomerTab.ORDERS
                                                },
                                                onAdvanceStep = { orderId ->
                                                    repository.advanceOrderStatus(orderId)
                                                },
                                                onRateOrder = { ord ->
                                                    ratingOrder = ord
                                                }
                                            )
                                        } else {
                                            customerScreenState = CustomerScreenState.MAIN_TABS
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customization Modal
            if (customizerFoodItem != null) {
                val dish = customizerFoodItem!!
                val parentRest = viewingRestaurant ?: restaurants.find { it.id == dish.restaurantId } ?: restaurants.first()
                CustomizationBottomSheet(
                    foodItem = dish,
                    onDismiss = { customizerFoodItem = null },
                    onConfirmAdd = { portion, addOns, qty, instructions ->
                        for (i in 1..qty) {
                            repository.addToCart(
                                foodItem = dish,
                                restaurant = parentRest,
                                portion = portion,
                                addOns = addOns,
                                specialInstructions = instructions
                            )
                        }
                        customizerFoodItem = null
                    }
                )
            }

            // Rating & Review Modal
            if (ratingOrder != null) {
                ReviewDialog(
                    order = ratingOrder!!,
                    onDismiss = { ratingOrder = null },
                    onSubmitReview = { rating, comment, tags ->
                        // Submit feedback
                        ratingOrder = null
                    }
                )
            }

            // Multi-Role Switcher Modal
            if (roleSwitcherOpen) {
                RoleSwitcherDialog(
                    currentRole = currentRole,
                    onRoleSelected = { newRole ->
                        repository.switchRole(newRole)
                        customerScreenState = CustomerScreenState.MAIN_TABS
                    },
                    onDismiss = { roleSwitcherOpen = false }
                )
            }
        }
    }
}
