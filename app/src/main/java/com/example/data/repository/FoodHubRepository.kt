package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class FoodHubRepository private constructor() {

    // Current active role
    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Dark theme preference
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Current selected delivery location
    private val _selectedLocation = MutableStateFlow("Koramangala, Bengaluru")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    // User wallet balance
    private val _walletBalance = MutableStateFlow(350.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    // User favorites restaurant ids
    private val _favoriteRestaurantIds = MutableStateFlow(setOf("rest_1", "rest_3"))
    val favoriteRestaurantIds: StateFlow<Set<String>> = _favoriteRestaurantIds.asStateFlow()

    // Restaurants list
    private val _restaurants = MutableStateFlow(getInitialRestaurants())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    // Food items list
    private val _foodItems = MutableStateFlow(getInitialFoodItems())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    // Categories
    val categories: List<FoodCategory> = listOf(
        FoodCategory("cat_1", "Biryani", "🍛", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=300"),
        FoodCategory("cat_2", "Burgers", "🍔", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300"),
        FoodCategory("cat_3", "Pizzas", "🍕", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=300"),
        FoodCategory("cat_4", "South Indian", "🥞", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=300"),
        FoodCategory("cat_5", "North Indian", "🥘", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=300"),
        FoodCategory("cat_6", "Healthy", "🥗", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=300"),
        FoodCategory("cat_7", "Desserts", "🍰", "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=300"),
        FoodCategory("cat_8", "Chinese", "🍜", "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=300")
    )

    // Saved Addresses
    private val _addresses = MutableStateFlow(
        listOf(
            Address(
                id = "addr_1",
                title = "Home",
                fullAddress = "Flat 402, Sunshine Apartments, 12th Main Road, 4th Block, Koramangala, Bengaluru - 560034",
                landmark = "Near Sony World Signal",
                contactNumber = "+91 98765 43210",
                isDefault = true
            ),
            Address(
                id = "addr_2",
                title = "Work",
                fullAddress = "Tech Park Block B, 5th Floor, Outer Ring Road, Bellandur, Bengaluru - 560103",
                landmark = "Opposite Central Mall",
                contactNumber = "+91 98765 43210",
                isDefault = false
            ),
            Address(
                id = "addr_3",
                title = "Other",
                fullAddress = "Villa 18, Palm Meadows, Varthur Main Road, Whitefield, Bengaluru - 560066",
                landmark = "Behind Forum Shantiniketan",
                contactNumber = "+91 98765 43210",
                isDefault = false
            )
        )
    )
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow(_addresses.value.first())
    val selectedAddress: StateFlow<Address> = _selectedAddress.asStateFlow()

    // Available coupons
    val coupons: List<Coupon> = listOf(
        Coupon("FOODHUB50", "50% OFF Up to ₹100", "Use code FOODHUB50 on orders above ₹199", 50, 100.0, 199.0),
        Coupon("WELCOME75", "FLAT ₹75 OFF", "Welcome discount for foodie lovers on orders above ₹299", 25, 75.0, 299.0),
        Coupon("FREEDEL", "FREE DELIVERY", "Zero delivery charge on orders above ₹149", 100, 40.0, 149.0),
        Coupon("FESTIVE150", "FLAT ₹150 OFF", "Mega savings on orders above ₹699", 20, 150.0, 699.0)
    )

    // Cart state
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartRestaurant = MutableStateFlow<Restaurant?>(null)
    val cartRestaurant: StateFlow<Restaurant?> = _cartRestaurant.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _driverTip = MutableStateFlow(20.0)
    val driverTip: StateFlow<Double> = _driverTip.asStateFlow()

    private val _deliveryInstruction = MutableStateFlow("Leave at doorstep")
    val deliveryInstruction: StateFlow<String> = _deliveryInstruction.asStateFlow()

    // Orders state
    private val _orders = MutableStateFlow(getInitialOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeTrackingOrderId = MutableStateFlow<String?>(getInitialOrders().firstOrNull()?.id)
    val activeTrackingOrderId: StateFlow<String?> = _activeTrackingOrderId.asStateFlow()

    // Delivery Partner Tasks
    private val _deliveryTasks = MutableStateFlow(getInitialDeliveryTasks())
    val deliveryTasks: StateFlow<List<DeliveryTask>> = _deliveryTasks.asStateFlow()

    private val _isRiderOnline = MutableStateFlow(true)
    val isRiderOnline: StateFlow<Boolean> = _isRiderOnline.asStateFlow()

    // AI Foodie Chat
    private val _chatMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                isBot = true,
                text = "Hello! I'm HubBot, your personal food concierge 🍲. Ask me what to eat today, recommendations under budget, or best dishes near you!",
                timestamp = "Just now"
            )
        )
    )
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    // Support Tickets
    private val _supportTickets = MutableStateFlow(
        listOf(
            "TICK-801: Order delayed by 10 mins (Resolved)",
            "TICK-802: Request extra raita in next order (Closed)",
            "TICK-803: Missing cutlery in order #FH-9921 (Refund issued ₹20)"
        )
    )
    val supportTickets: StateFlow<List<String>> = _supportTickets.asStateFlow()

    // Actions
    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setSelectedLocation(location: String) {
        _selectedLocation.value = location
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun addAddress(address: Address) {
        _addresses.value = _addresses.value + address
        if (address.isDefault) {
            _selectedAddress.value = address
        }
    }

    fun toggleFavorite(restaurantId: String) {
        val current = _favoriteRestaurantIds.value
        _favoriteRestaurantIds.value = if (current.contains(restaurantId)) {
            current - restaurantId
        } else {
            current + restaurantId
        }
    }

    fun setDriverTip(tip: Double) {
        _driverTip.value = tip
    }

    fun setDeliveryInstruction(instruction: String) {
        _deliveryInstruction.value = instruction
    }

    fun applyCoupon(coupon: Coupon?): Boolean {
        if (coupon == null) {
            _appliedCoupon.value = null
            return true
        }
        val subtotal = _cartItems.value.sumOf { it.totalPrice }
        if (subtotal >= coupon.minOrder) {
            _appliedCoupon.value = coupon
            return true
        }
        return false
    }

    fun addToCart(
        foodItem: FoodItem,
        restaurant: Restaurant,
        portion: PortionSize? = null,
        addOns: List<FoodAddOn> = emptyList(),
        specialInstructions: String = ""
    ) {
        // Check if item belongs to current restaurant or different
        val currentRest = _cartRestaurant.value
        if (currentRest != null && currentRest.id != restaurant.id) {
            // New restaurant: reset cart to this restaurant
            _cartRestaurant.value = restaurant
            _cartItems.value = listOf(
                CartItem(
                    foodItem = foodItem,
                    selectedPortion = portion,
                    selectedAddOns = addOns,
                    quantity = 1,
                    specialInstructions = specialInstructions
                )
            )
            return
        }

        _cartRestaurant.value = restaurant
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.foodItem.id == foodItem.id &&
                    it.selectedPortion?.id == portion?.id &&
                    it.selectedAddOns.map { a -> a.id }.toSet() == addOns.map { a -> a.id }.toSet()
        }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentList.add(
                CartItem(
                    foodItem = foodItem,
                    selectedPortion = portion,
                    selectedAddOns = addOns,
                    quantity = 1,
                    specialInstructions = specialInstructions
                )
            )
        }
        _cartItems.value = currentList
    }

    fun updateCartItemQuantity(cartItemId: String, delta: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            val current = currentList[index]
            val newQty = current.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = current.copy(quantity = newQty)
            }
        }
        _cartItems.value = currentList
        if (currentList.isEmpty()) {
            _cartRestaurant.value = null
            _appliedCoupon.value = null
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartRestaurant.value = null
        _appliedCoupon.value = null
    }

    fun placeOrder(paymentMethod: String): Order? {
        val items = _cartItems.value
        val restaurant = _cartRestaurant.value ?: return null
        if (items.isEmpty()) return null

        val subtotal = items.sumOf { it.totalPrice }
        val coupon = _appliedCoupon.value
        val discount = if (coupon != null && subtotal >= coupon.minOrder) {
            if (coupon.code == "FREEDEL") 35.0 else minOf((subtotal * coupon.discountPercent / 100.0), coupon.maxDiscount)
        } else 0.0

        val deliveryFee = if (coupon?.code == "FREEDEL") 0.0 else 35.0
        val platformFee = 5.0
        val taxes = subtotal * 0.05 // 5% GST
        val tip = _driverTip.value
        val total = maxOf(0.0, (subtotal - discount) + deliveryFee + platformFee + taxes + tip)

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val now = Date()
        val estCalendar = Calendar.getInstance().apply {
            time = now
            add(Calendar.MINUTE, restaurant.deliveryTimeMinutes)
        }

        val orderNum = "FH-${Random().nextInt(90000) + 10000}"
        val otp = String.format("%04d", Random().nextInt(9000) + 1000)

        val newOrder = Order(
            orderNumber = orderNum,
            restaurant = restaurant,
            items = items,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            platformFee = platformFee,
            taxes = taxes,
            tip = tip,
            totalAmount = total,
            status = OrderStatus.ORDER_PLACED,
            placedAtTime = timeFormatter.format(now),
            estimatedDeliveryTime = timeFormatter.format(estCalendar.time),
            deliveryAddress = _selectedAddress.value,
            paymentMethod = paymentMethod,
            paymentStatus = "PAID",
            deliveryPartnerName = "Ramesh Kumar",
            deliveryPartnerPhone = "+91 98765 12340",
            deliveryPartnerRating = 4.9,
            deliveryPartnerVehicle = "Hero Electric (KA 05 EQ 8821)",
            deliveryOtp = otp,
            riderProgress = 0.1f
        )

        _orders.value = listOf(newOrder) + _orders.value
        _activeTrackingOrderId.value = newOrder.id

        // Also add task for delivery partner
        val deliveryTask = DeliveryTask(
            orderId = newOrder.id,
            orderNumber = orderNum,
            restaurantName = restaurant.name,
            restaurantAddress = restaurant.address,
            customerName = "Ananya Sharma",
            customerAddress = _selectedAddress.value.fullAddress,
            customerPhone = _selectedAddress.value.contactNumber,
            itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.foodItem.name}" },
            earnings = 65.0,
            distanceKm = restaurant.distanceKm,
            status = "ASSIGNED",
            customerOtp = otp
        )
        _deliveryTasks.value = listOf(deliveryTask) + _deliveryTasks.value

        clearCart()
        return newOrder
    }

    fun setActiveTrackingOrder(orderId: String) {
        _activeTrackingOrderId.value = orderId
    }

    fun advanceOrderStatus(orderId: String) {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            val order = currentOrders[index]
            val nextStatus = when (order.status) {
                OrderStatus.ORDER_PLACED -> OrderStatus.RESTAURANT_ACCEPTED
                OrderStatus.RESTAURANT_ACCEPTED -> OrderStatus.PREPARING_FOOD
                OrderStatus.PREPARING_FOOD -> OrderStatus.READY_FOR_PICKUP
                OrderStatus.READY_FOR_PICKUP -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                OrderStatus.DELIVERED -> OrderStatus.DELIVERED
                OrderStatus.CANCELLED -> OrderStatus.CANCELLED
            }
            val nextProgress = when (nextStatus) {
                OrderStatus.ORDER_PLACED -> 0.1f
                OrderStatus.RESTAURANT_ACCEPTED -> 0.25f
                OrderStatus.PREPARING_FOOD -> 0.45f
                OrderStatus.READY_FOR_PICKUP -> 0.65f
                OrderStatus.OUT_FOR_DELIVERY -> 0.85f
                OrderStatus.DELIVERED -> 1.0f
                OrderStatus.CANCELLED -> 0.0f
            }
            currentOrders[index] = order.copy(status = nextStatus, riderProgress = nextProgress)
            _orders.value = currentOrders
        }
    }

    fun reorder(order: Order) {
        _cartRestaurant.value = order.restaurant
        _cartItems.value = order.items
    }

    // Delivery Partner controls
    fun toggleRiderDuty() {
        _isRiderOnline.value = !_isRiderOnline.value
    }

    fun updateDeliveryTaskStatus(taskId: String, newStatus: String) {
        val tasks = _deliveryTasks.value.toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index >= 0) {
            val task = tasks[index]
            tasks[index] = task.copy(status = newStatus)
            _deliveryTasks.value = tasks

            // Link to customer order
            if (newStatus == "PICKED_UP") {
                advanceOrderStatus(task.orderId)
            } else if (newStatus == "DELIVERED") {
                val ords = _orders.value.toMutableList()
                val oIdx = ords.indexOfFirst { it.id == task.orderId }
                if (oIdx >= 0) {
                    ords[oIdx] = ords[oIdx].copy(status = OrderStatus.DELIVERED, riderProgress = 1.0f)
                    _orders.value = ords
                }
            }
        }
    }

    // Restaurant Partner controls
    fun toggleFoodItemAvailability(foodItemId: String) {
        val items = _foodItems.value.toMutableList()
        val index = items.indexOfFirst { it.id == foodItemId }
        if (index >= 0) {
            val item = items[index]
            items[index] = item.copy(isAvailable = !item.isAvailable)
            _foodItems.value = items
        }
    }

    fun addFoodItem(newItem: FoodItem) {
        _foodItems.value = listOf(newItem) + _foodItems.value
    }

    // Admin controls
    fun toggleRestaurantActive(restaurantId: String) {
        val rests = _restaurants.value.toMutableList()
        val index = rests.indexOfFirst { it.id == restaurantId }
        if (index >= 0) {
            val r = rests[index]
            rests[index] = r.copy(isOpen = !r.isOpen)
            _restaurants.value = rests
        }
    }

    fun addWalletFunds(amount: Double) {
        _walletBalance.value = _walletBalance.value + amount
    }

    // AI Chat Assistant
    fun sendUserChatMessage(prompt: String) {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val userMsg = AiChatMessage(isBot = false, text = prompt, timestamp = time)
        val current = _chatMessages.value + userMsg
        _chatMessages.value = current

        // Generate response based on prompt
        val lower = prompt.lowercase()
        val allDishes = _foodItems.value

        val (botReply, suggested) = when {
            lower.contains("biryani") -> {
                Pair(
                    "Here are the most aromatic, top-rated Hyderabadi Dum Biryanis prepared with pure saffron and slow-cooked perfection!",
                    allDishes.filter { it.name.contains("Biryani", ignoreCase = true) }
                )
            }
            lower.contains("veg") || lower.contains("vegetarian") || lower.contains("paneer") -> {
                Pair(
                    "Here are exquisite Pure Vegetarian delicacies from top kitchens around your area, cooked fresh with authentic spices!",
                    allDishes.filter { it.isVeg }.take(4)
                )
            }
            lower.contains("burger") || lower.contains("pizza") || lower.contains("fast") -> {
                Pair(
                    "Craving juicy gourmet bites? Here are artisanal sourdough pizzas and gourmet smash burgers highly rated by foodies!",
                    allDishes.filter { it.category == "Burgers" || it.category == "Pizzas" }
                )
            }
            lower.contains("budget") || lower.contains("under") || lower.contains("cheap") || lower.contains("200") -> {
                Pair(
                    "Great budget-friendly meals under ₹250 packed with tremendous flavor and quick delivery:",
                    allDishes.filter { it.price <= 250 }.take(4)
                )
            }
            lower.contains("sweet") || lower.contains("dessert") -> {
                Pair(
                    "Satisfy your sweet tooth with warm Gulab Jamuns, decadent chocolate brownies, and creamy Rasmalai!",
                    allDishes.filter { it.category == "Desserts" }
                )
            }
            else -> {
                Pair(
                    "Based on trending orders in Koramangala today, I highly recommend our Chef Specials below. You can customize size and add-ons directly!",
                    allDishes.filter { it.isBestseller }.take(3)
                )
            }
        }

        val botMsg = AiChatMessage(
            isBot = true,
            text = botReply,
            suggestedItems = suggested,
            timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        _chatMessages.value = _chatMessages.value + botMsg
    }

    companion object {
        @Volatile
        private var instance: FoodHubRepository? = null

        fun getInstance(): FoodHubRepository {
            return instance ?: synchronized(this) {
                instance ?: FoodHubRepository().also { instance = it }
            }
        }

        private fun getInitialRestaurants(): List<Restaurant> = listOf(
            Restaurant(
                id = "rest_1",
                name = "Meghana Foods",
                tagline = "Authentic Andhra & Hyderabadi Dum Biryani",
                coverUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
                rating = 4.6,
                reviewCount = 3840,
                deliveryTimeMinutes = 26,
                distanceKm = 2.1,
                costForTwo = 450,
                cuisines = listOf("Biryani", "Andhra", "North Indian", "South Indian"),
                isPureVeg = false,
                isOpen = true,
                featuredPromo = "50% OFF up to ₹100",
                address = "124, 1st Cross, 5th Block, Koramangala, Bengaluru"
            ),
            Restaurant(
                id = "rest_2",
                name = "Truffles Gourmet",
                tagline = "Legendary Burgers, Steaks & Pastas",
                coverUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800",
                rating = 4.7,
                reviewCount = 5210,
                deliveryTimeMinutes = 32,
                distanceKm = 3.4,
                costForTwo = 550,
                cuisines = listOf("Burgers", "American", "Continental", "Desserts"),
                isPureVeg = false,
                isOpen = true,
                featuredPromo = "Flat ₹75 OFF on ₹349+",
                address = "28, 4th 'B' Cross, 5th Block, Koramangala, Bengaluru"
            ),
            Restaurant(
                id = "rest_3",
                name = "Udupi Sri Krishna Bhavan",
                tagline = "Pure Veg Crispy Ghee Dosas & Filter Coffee",
                coverUrl = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800",
                rating = 4.8,
                reviewCount = 2950,
                deliveryTimeMinutes = 18,
                distanceKm = 1.1,
                costForTwo = 250,
                cuisines = listOf("South Indian", "Pure Veg", "Breakfast", "Snacks"),
                isPureVeg = true,
                isOpen = true,
                featuredPromo = "Free Delivery on ₹149+",
                address = "45, 80 Feet Road, 4th Block, Koramangala, Bengaluru"
            ),
            Restaurant(
                id = "rest_4",
                name = "Pizza Bella Napoli",
                tagline = "Artisanal Wood-Fired Neapolitan Pizzas",
                coverUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800",
                rating = 4.5,
                reviewCount = 1890,
                deliveryTimeMinutes = 35,
                distanceKm = 3.9,
                costForTwo = 650,
                cuisines = listOf("Pizzas", "Italian", "Pastas", "Garlic Breads"),
                isPureVeg = false,
                isOpen = true,
                featuredPromo = "20% OFF using PIZZALOVE",
                address = "77, 100 Feet Road, Indiranagar, Bengaluru"
            ),
            Restaurant(
                id = "rest_5",
                name = "Punjab Grill & Curry House",
                tagline = "Rich Butter Chicken, Dal Makhani & Tandoor",
                coverUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=800",
                rating = 4.6,
                reviewCount = 2100,
                deliveryTimeMinutes = 28,
                distanceKm = 2.8,
                costForTwo = 500,
                cuisines = listOf("North Indian", "Mughlai", "Tandoor", "Curries"),
                isPureVeg = false,
                isOpen = true,
                featuredPromo = "Free Butter Naan with Curries",
                address = "12, 17th Main, 2nd Stage, BTM Layout, Bengaluru"
            ),
            Restaurant(
                id = "rest_6",
                name = "Green Bowl Organics",
                tagline = "Nutrient Dense Salads, Grain Bowls & Smoothies",
                coverUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800",
                rating = 4.4,
                reviewCount = 940,
                deliveryTimeMinutes = 22,
                distanceKm = 1.9,
                costForTwo = 380,
                cuisines = listOf("Healthy", "Salads", "Bowls", "Beverages"),
                isPureVeg = true,
                isOpen = true,
                featuredPromo = "HEALTHY20 - 20% OFF",
                address = "90, Jyoti Nivas College Road, Koramangala, Bengaluru"
            )
        )

        private fun getInitialFoodItems(): List<FoodItem> = listOf(
            // Meghana Foods
            FoodItem(
                id = "food_1",
                restaurantId = "rest_1",
                name = "Special Hyderabadi Chicken Biryani",
                description = "Fragrant long-grain basmati rice cooked with succulent tender chicken, saffron, browned onions and secret Andhra spices. Served with cooling raita & mirchi ka salan.",
                price = 320.0,
                originalPrice = 370.0,
                isVeg = false,
                isBestseller = true,
                category = "Biryani",
                imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500",
                rating = 4.8,
                reviewCount = 1420,
                spicyLevel = 3,
                portionSizes = listOf(
                    PortionSize("p_reg", "Regular (Serves 1)", 0.0),
                    PortionSize("p_large", "Jumbo (Serves 2)", 150.0)
                ),
                addOns = listOf(
                    FoodAddOn("a_raita", "Extra Boondi Raita", 35.0),
                    FoodAddOn("a_salan", "Extra Mirchi Ka Salan", 40.0),
                    FoodAddOn("a_egg", "Boiled Egg (2 pcs)", 30.0)
                )
            ),
            FoodItem(
                id = "food_2",
                restaurantId = "rest_1",
                name = "Paneer Dum Biryani",
                description = "Cubes of fresh cottage cheese marinated in spiced yogurt and layered with aromatic basmati rice and slow cooked in sealed handi.",
                price = 280.0,
                originalPrice = 320.0,
                isVeg = true,
                isBestseller = true,
                category = "Biryani",
                imageUrl = "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500",
                rating = 4.6,
                reviewCount = 820,
                spicyLevel = 2,
                portionSizes = listOf(
                    PortionSize("p_reg", "Regular", 0.0),
                    PortionSize("p_large", "Large", 130.0)
                ),
                addOns = listOf(
                    FoodAddOn("a_raita", "Extra Boondi Raita", 35.0),
                    FoodAddOn("a_paneer", "Extra Roasted Paneer", 60.0)
                )
            ),
            FoodItem(
                id = "food_3",
                restaurantId = "rest_1",
                name = "Andhra Chilli Chicken (Dry)",
                description = "Crispy chicken tossed with spicy green chillies, curry leaves, and crushed black peppercorns. An all-time Bengaluru favorite appetizer.",
                price = 260.0,
                originalPrice = 295.0,
                isVeg = false,
                isBestseller = true,
                category = "Starters",
                imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=500",
                rating = 4.7,
                reviewCount = 990,
                spicyLevel = 3
            ),

            // Truffles
            FoodItem(
                id = "food_4",
                restaurantId = "rest_2",
                name = "All American Cheese Burger",
                description = "Flame-grilled crispy patty topped with double molten cheddar cheese, caramelized onions, crisp lettuce, gherkins and secret cocktail sauce.",
                price = 240.0,
                originalPrice = 280.0,
                isVeg = false,
                isBestseller = true,
                category = "Burgers",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500",
                rating = 4.9,
                reviewCount = 2100,
                spicyLevel = 1,
                portionSizes = listOf(
                    PortionSize("p_sng", "Single Patty", 0.0),
                    PortionSize("p_dbl", "Double Beast Patty", 95.0)
                ),
                addOns = listOf(
                    FoodAddOn("a_fries", "Peri Peri Fries", 60.0),
                    FoodAddOn("a_cheese", "Extra Melted Cheddar", 40.0),
                    FoodAddOn("a_dip", "Garlic Mayo Dip", 25.0)
                )
            ),
            FoodItem(
                id = "food_5",
                restaurantId = "rest_2",
                name = "Crunchy Crispy Veg Supreme Burger",
                description = "Golden crumb-fried potato and green peas patty layered with tomatoes, cucumber, cheese slice and chipotle mayo in sesame brioche bun.",
                price = 190.0,
                originalPrice = 220.0,
                isVeg = true,
                isBestseller = false,
                category = "Burgers",
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=500",
                rating = 4.5,
                reviewCount = 640,
                spicyLevel = 1,
                addOns = listOf(
                    FoodAddOn("a_fries", "Salted French Fries", 50.0),
                    FoodAddOn("a_cheese", "Cheese Slice", 30.0)
                )
            ),
            FoodItem(
                id = "food_6",
                restaurantId = "rest_2",
                name = "Ferrero Rocher Decadent Shake",
                description = "Thick rich hazelnut milkshake blended with crushed Ferrero Rocher chocolates, vanilla gelato, and chocolate fudge syrup.",
                price = 180.0,
                originalPrice = 210.0,
                isVeg = true,
                isBestseller = true,
                category = "Desserts",
                imageUrl = "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500",
                rating = 4.8,
                reviewCount = 1150
            ),

            // Udupi Sri Krishna Bhavan
            FoodItem(
                id = "food_7",
                restaurantId = "rest_3",
                name = "Ghee Masala Dosa",
                description = "Crispy golden crepe roasted in pure desi butter ghee, stuffed with spiced potato mash, served with piping hot sambar and three varieties of chutneys.",
                price = 120.0,
                originalPrice = 140.0,
                isVeg = true,
                isBestseller = true,
                category = "South Indian",
                imageUrl = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500",
                rating = 4.9,
                reviewCount = 1870,
                spicyLevel = 1,
                addOns = listOf(
                    FoodAddOn("a_vada", "Crispy Medu Vada (1 pc)", 35.0),
                    FoodAddOn("a_filter_coffee", "Degree Filter Coffee", 40.0)
                )
            ),
            FoodItem(
                id = "food_8",
                restaurantId = "rest_3",
                name = "Steamed Button Idli & Vada Combo",
                description = "Pillow soft steamed rice cakes (2 pcs) and crispy lentil vada (1 pc) served submerged in drumstick sambar and fresh coconut chutney.",
                price = 95.0,
                originalPrice = 110.0,
                isVeg = true,
                isBestseller = true,
                category = "South Indian",
                imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500",
                rating = 4.7,
                reviewCount = 1320,
                spicyLevel = 1
            ),

            // Pizza Bella Napoli
            FoodItem(
                id = "food_9",
                restaurantId = "rest_4",
                name = "Margherita Speciale Pizza",
                description = "San Marzano tomato sauce, fresh buffalo mozzarella, aromatic sweet basil leaves, and a drizzle of extra virgin olive oil on 48-hr fermented sourdough crust.",
                price = 360.0,
                originalPrice = 420.0,
                isVeg = true,
                isBestseller = true,
                category = "Pizzas",
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500",
                rating = 4.8,
                reviewCount = 920,
                portionSizes = listOf(
                    PortionSize("p_med", "Medium 10 inch", 0.0),
                    PortionSize("p_lrg", "Large 12 inch", 140.0)
                ),
                addOns = listOf(
                    FoodAddOn("a_cheese_burst", "Cheese Burst Crust", 80.0),
                    FoodAddOn("a_garlic_bread", "Stuffed Garlic Breadsticks", 120.0)
                )
            ),
            FoodItem(
                id = "food_10",
                restaurantId = "rest_4",
                name = "Smoky Barbecue Pepperoni Feast",
                description = "Loaded with Italian pepperoni slices, barbecue grilled chicken chunks, smoked mozzarella, and roasted red peppers.",
                price = 450.0,
                originalPrice = 520.0,
                isVeg = false,
                isBestseller = true,
                category = "Pizzas",
                imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?w=500",
                rating = 4.7,
                reviewCount = 740,
                spicyLevel = 2,
                portionSizes = listOf(
                    PortionSize("p_med", "Medium 10 inch", 0.0),
                    PortionSize("p_lrg", "Large 12 inch", 160.0)
                )
            ),

            // Punjab Grill
            FoodItem(
                id = "food_11",
                restaurantId = "rest_5",
                name = "Murgh Makhani (Butter Chicken)",
                description = "Tandoor-charred chicken tikka simmered in a velvety smooth tomato, butter, and cashew cream gravy flavored with kasoori methi.",
                price = 340.0,
                originalPrice = 390.0,
                isVeg = false,
                isBestseller = true,
                category = "North Indian",
                imageUrl = "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=500",
                rating = 4.9,
                reviewCount = 1560,
                spicyLevel = 1,
                addOns = listOf(
                    FoodAddOn("a_garlic_naan", "Butter Garlic Naan (2 pcs)", 70.0),
                    FoodAddOn("a_jeera_rice", "Fragrant Jeera Rice", 80.0)
                )
            ),
            FoodItem(
                id = "food_12",
                restaurantId = "rest_5",
                name = "Dal Makhani Amritsari",
                description = "Black lentils and kidney beans slow-simmered overnight over charcoal embers, finished with churned white butter and cream.",
                price = 240.0,
                originalPrice = 280.0,
                isVeg = true,
                isBestseller = true,
                category = "North Indian",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500",
                rating = 4.8,
                reviewCount = 1100,
                spicyLevel = 1,
                addOns = listOf(
                    FoodAddOn("a_butter_roti", "Tandoori Butter Roti (2 pcs)", 50.0)
                )
            ),

            // Green Bowl
            FoodItem(
                id = "food_13",
                restaurantId = "rest_6",
                name = "Mediterranean Quinoa Falafel Bowl",
                description = "Herbed quinoa, crispy baked falafel, roasted bell peppers, kalamata olives, cucumber salad, and creamy tahini dressing.",
                price = 260.0,
                originalPrice = 310.0,
                isVeg = true,
                isBestseller = true,
                category = "Healthy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500",
                rating = 4.6,
                reviewCount = 490
            ),
            FoodItem(
                id = "food_14",
                restaurantId = "rest_6",
                name = "Alphonso Mango Chia Pudding",
                description = "Almond milk chia seed pudding layered with seasonal Alphonso mango puree, toasted almond flakes, and organic honey.",
                price = 190.0,
                originalPrice = 220.0,
                isVeg = true,
                isBestseller = false,
                category = "Desserts",
                imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=500",
                rating = 4.7,
                reviewCount = 310
            )
        )

        private fun getInitialOrders(): List<Order> {
            val rest = getInitialRestaurants()[0]
            val items = listOf(
                CartItem(
                    foodItem = getInitialFoodItems()[0],
                    quantity = 1,
                    selectedPortion = PortionSize("p_reg", "Regular", 0.0)
                ),
                CartItem(
                    foodItem = getInitialFoodItems()[2],
                    quantity = 1
                )
            )
            return listOf(
                Order(
                    orderNumber = "FH-88421",
                    restaurant = rest,
                    items = items,
                    subtotal = 580.0,
                    discount = 100.0,
                    deliveryFee = 35.0,
                    platformFee = 5.0,
                    taxes = 29.0,
                    tip = 20.0,
                    totalAmount = 569.0,
                    status = OrderStatus.OUT_FOR_DELIVERY,
                    placedAtTime = "01:15 PM",
                    estimatedDeliveryTime = "01:45 PM",
                    deliveryAddress = Address(
                        title = "Home",
                        fullAddress = "Flat 402, Sunshine Apartments, 12th Main Road, 4th Block, Koramangala, Bengaluru - 560034",
                        isDefault = true
                    ),
                    paymentMethod = "Google Pay (UPI)",
                    paymentStatus = "PAID",
                    deliveryPartnerName = "Ramesh Kumar",
                    deliveryPartnerPhone = "+91 98765 12340",
                    deliveryPartnerRating = 4.9,
                    deliveryPartnerVehicle = "Hero Electric Optima (KA 05 EQ 8821)",
                    deliveryOtp = "4829",
                    riderProgress = 0.75f
                ),
                Order(
                    orderNumber = "FH-77190",
                    restaurant = getInitialRestaurants()[2],
                    items = listOf(
                        CartItem(foodItem = getInitialFoodItems()[6], quantity = 2),
                        CartItem(foodItem = getInitialFoodItems()[7], quantity = 1)
                    ),
                    subtotal = 335.0,
                    discount = 50.0,
                    deliveryFee = 0.0,
                    platformFee = 5.0,
                    taxes = 16.75,
                    tip = 0.0,
                    totalAmount = 306.75,
                    status = OrderStatus.DELIVERED,
                    placedAtTime = "Yesterday, 08:30 AM",
                    estimatedDeliveryTime = "Yesterday, 08:55 AM",
                    deliveryAddress = Address(
                        title = "Home",
                        fullAddress = "Flat 402, Sunshine Apartments, 12th Main Road, 4th Block, Koramangala, Bengaluru - 560034",
                        isDefault = true
                    ),
                    paymentMethod = "PhonePe (UPI)",
                    paymentStatus = "PAID",
                    deliveryPartnerName = "Vikas Gowda",
                    deliveryOtp = "7102",
                    riderProgress = 1.0f
                )
            )
        }

        private fun getInitialDeliveryTasks(): List<DeliveryTask> = listOf(
            DeliveryTask(
                orderId = "order_1",
                orderNumber = "FH-88421",
                restaurantName = "Meghana Foods",
                restaurantAddress = "124, 1st Cross, 5th Block, Koramangala",
                customerName = "Ananya Sharma",
                customerAddress = "Flat 402, Sunshine Apts, 12th Main, Koramangala",
                customerPhone = "+91 98765 43210",
                itemsSummary = "1x Chicken Biryani, 1x Chilli Chicken",
                earnings = 65.0,
                distanceKm = 2.1,
                status = "PICKED_UP",
                customerOtp = "4829"
            ),
            DeliveryTask(
                orderId = "order_2",
                orderNumber = "FH-89100",
                restaurantName = "Truffles Gourmet",
                restaurantAddress = "28, 4th B Cross, Koramangala",
                customerName = "Karthik Verma",
                customerAddress = "Prestige Tech Park, Marathahalli",
                customerPhone = "+91 99887 66554",
                itemsSummary = "2x All American Burgers, 1x Ferrero Shake",
                earnings = 85.0,
                distanceKm = 4.2,
                status = "ASSIGNED",
                customerOtp = "5521"
            )
        )
    }
}
