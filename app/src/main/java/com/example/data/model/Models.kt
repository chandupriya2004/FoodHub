package com.example.data.model

import java.util.UUID

enum class UserRole(val title: String) {
    CUSTOMER("Customer"),
    RESTAURANT_PARTNER("Restaurant Partner"),
    DELIVERY_PARTNER("Delivery Rider"),
    ADMIN("Admin Portal")
}

data class Restaurant(
    val id: String,
    val name: String,
    val tagline: String,
    val coverUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMinutes: Int,
    val distanceKm: Double,
    val costForTwo: Int,
    val cuisines: List<String>,
    val isPureVeg: Boolean = false,
    val isOpen: Boolean = true,
    val featuredPromo: String = "",
    val address: String,
    val latitude: Double = 12.9716,
    val longitude: Double = 77.5946
)

data class FoodCategory(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val imageUrl: String
)

data class PortionSize(
    val id: String,
    val name: String,
    val priceDelta: Double = 0.0
)

data class FoodAddOn(
    val id: String,
    val name: String,
    val price: Double
)

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val isVeg: Boolean,
    val isBestseller: Boolean = false,
    val isAvailable: Boolean = true,
    val category: String,
    val imageUrl: String,
    val rating: Double = 4.5,
    val reviewCount: Int = 120,
    val spicyLevel: Int = 1,
    val portionSizes: List<PortionSize> = emptyList(),
    val addOns: List<FoodAddOn> = emptyList()
)

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val foodItem: FoodItem,
    val selectedPortion: PortionSize? = null,
    val selectedAddOns: List<FoodAddOn> = emptyList(),
    val quantity: Int = 1,
    val specialInstructions: String = ""
) {
    val unitPrice: Double
        get() {
            val portionExtra = selectedPortion?.priceDelta ?: 0.0
            val addOnsSum = selectedAddOns.sumOf { it.price }
            return foodItem.price + portionExtra + addOnsSum
        }

    val totalPrice: Double
        get() = unitPrice * quantity
}

data class Address(
    val id: String = UUID.randomUUID().toString(),
    val title: String, // Home, Work, Other
    val fullAddress: String,
    val landmark: String = "",
    val contactNumber: String = "+91 98765 43210",
    val isDefault: Boolean = false
)

enum class OrderStatus(val title: String, val description: String) {
    ORDER_PLACED("Order Placed", "Your order has been sent to the restaurant"),
    RESTAURANT_ACCEPTED("Order Accepted", "Restaurant is reviewing your order"),
    PREPARING_FOOD("Preparing Food", "Chef is cooking with fresh ingredients"),
    READY_FOR_PICKUP("Ready for Pickup", "Order packed and waiting for delivery partner"),
    OUT_FOR_DELIVERY("Out for Delivery", "Delivery partner is on the way"),
    DELIVERED("Delivered", "Delivered successfully! Enjoy your meal"),
    CANCELLED("Cancelled", "Order was cancelled")
}

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val orderNumber: String,
    val restaurant: Restaurant,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val platformFee: Double = 5.0,
    val taxes: Double,
    val tip: Double = 0.0,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.ORDER_PLACED,
    val placedAtTime: String,
    val estimatedDeliveryTime: String,
    val deliveryAddress: Address,
    val paymentMethod: String,
    val paymentStatus: String = "PAID",
    val deliveryPartnerName: String = "Ramesh Kumar",
    val deliveryPartnerPhone: String = "+91 98765 12340",
    val deliveryPartnerRating: Double = 4.9,
    val deliveryPartnerVehicle: String = "Hero Electric Optima (KA 05 EQ 8821)",
    val deliveryOtp: String = "4829",
    val riderProgress: Float = 0.15f // 0.0 (at restaurant) to 1.0 (delivered)
)

data class Coupon(
    val code: String,
    val title: String,
    val description: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrder: Double
)

data class Review(
    val id: String = UUID.randomUUID().toString(),
    val author: String,
    val rating: Double,
    val date: String,
    val comment: String,
    val tags: List<String> = emptyList(),
    val verified: Boolean = true
)

data class DeliveryTask(
    val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val orderNumber: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val customerName: String,
    val customerAddress: String,
    val customerPhone: String,
    val itemsSummary: String,
    val earnings: Double,
    val distanceKm: Double,
    val status: String = "ASSIGNED", // ASSIGNED, PICKED_UP, DELIVERED
    val customerOtp: String = "4829"
)

data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isBot: Boolean,
    val text: String,
    val suggestedItems: List<FoodItem> = emptyList(),
    val timestamp: String = "Just now"
)
