package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Address
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.Restaurant
import com.example.ui.components.VegNonVegBadge
import com.example.ui.theme.FoodHubGold
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    restaurant: Restaurant?,
    cartItems: List<CartItem>,
    appliedCoupon: Coupon?,
    availableCoupons: List<Coupon>,
    selectedAddress: Address,
    driverTip: Double,
    deliveryInstruction: String,
    onBack: () -> Unit,
    onIncreaseItem: (String) -> Unit,
    onDecreaseItem: (String) -> Unit,
    onApplyCoupon: (Coupon?) -> Boolean,
    onSelectTip: (Double) -> Unit,
    onSelectInstruction: (String) -> Unit,
    onProceedToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var couponSheetOpen by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf<String?>(null) }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.totalPrice } }
    val discount = remember(appliedCoupon, subtotal) {
        if (appliedCoupon != null && subtotal >= appliedCoupon.minOrder) {
            if (appliedCoupon.code == "FREEDEL") 35.0
            else minOf((subtotal * appliedCoupon.discountPercent / 100.0), appliedCoupon.maxDiscount)
        } else 0.0
    }
    val deliveryFee = if (appliedCoupon?.code == "FREEDEL") 0.0 else 35.0
    val platformFee = 5.0
    val taxes = subtotal * 0.05
    val totalToPay = maxOf(0.0, (subtotal - discount) + deliveryFee + platformFee + taxes + driverTip)

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Good food is always cooking! Go ahead, order some yummy items from the menu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Explore Restaurants", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = restaurant?.name ?: "Cart",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} items",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
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
                            text = "TO PAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${totalToPay.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onProceedToCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("proceed_to_checkout_button")
                    ) {
                        Text(
                            text = "Proceed to Pay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delivery Address Summary
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FoodHubOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Deliver to: ${selectedAddress.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = selectedAddress.fullAddress,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Cart Items List
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ORDER ITEMS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        cartItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    VegNonVegBadge(
                                        isVeg = item.foodItem.isVeg,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.foodItem.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (item.selectedPortion != null) {
                                            Text(
                                                text = item.selectedPortion.name,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (item.selectedAddOns.isNotEmpty()) {
                                            Text(
                                                text = "+ " + item.selectedAddOns.joinToString(", ") { it.name },
                                                fontSize = 11.sp,
                                                color = FoodHubOrange
                                            )
                                        }
                                        if (item.specialInstructions.isNotEmpty()) {
                                            Text(
                                                text = "Note: ${item.specialInstructions}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Stepper & Total
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { onDecreaseItem(item.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Text(
                                                text = "${item.quantity}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                            IconButton(
                                                onClick = { onIncreaseItem(item.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "₹${item.totalPrice.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            if (index < cartItems.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            // Delivery Instructions
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DELIVERY INSTRUCTIONS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val options = listOf("Leave at doorstep", "Avoid ringing bell", "Don't call", "Pet at home")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            options.forEach { opt ->
                                val isSelected = deliveryInstruction == opt
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) FoodHubOrange.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) BorderStroke(1.5.dp, FoodHubOrange) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectInstruction(opt) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = opt,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) FoodHubOrange else MaterialTheme.colorScheme.onSurface,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Coupon Code Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { couponSheetOpen = true }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Discount,
                                contentDescription = null,
                                tint = FoodHubOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                if (appliedCoupon != null) {
                                    Text(
                                        text = "${appliedCoupon.code} APPLIED",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = VegGreen
                                    )
                                    Text(
                                        text = "Saved ₹${discount.toInt()} on this order",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "Apply Coupon",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Save up to ₹150 with FoodHub promos",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (appliedCoupon != null) {
                            TextButton(onClick = { onApplyCoupon(null) }) {
                                Text("Remove", color = Color.Red, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "View All",
                                fontWeight = FontWeight.Bold,
                                color = FoodHubOrange,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Tip Rider Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = FoodHubOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tip your Delivery Partner",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "100% of the tip goes directly to your rider for their hard work",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val tips = listOf(0.0, 20.0, 30.0, 50.0)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            tips.forEach { t ->
                                val isSelected = driverTip == t
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) FoodHubOrange else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectTip(t) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = if (t == 0.0) "No Tip" else "₹${t.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Bill Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "BILL DETAILS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        BillRow(label = "Item Total", value = "₹${subtotal.toInt()}")

                        if (discount > 0) {
                            BillRow(
                                label = "Coupon Discount (${appliedCoupon?.code})",
                                value = "-₹${discount.toInt()}",
                                isDiscount = true
                            )
                        }

                        BillRow(
                            label = "Delivery Partner Fee",
                            value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}"
                        )

                        BillRow(label = "Platform Fee", value = "₹${platformFee.toInt()}")

                        BillRow(
                            label = "Govt Taxes & Restaurant Charges (5%)",
                            value = "₹${String.format("%.2f", taxes)}"
                        )

                        if (driverTip > 0) {
                            BillRow(label = "Delivery Partner Tip", value = "₹${driverTip.toInt()}")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "To Pay",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "₹${totalToPay.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = FoodHubOrange
                            )
                        }
                    }
                }
            }
        }
    }

    // Coupons BottomSheet
    if (couponSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { couponSheetOpen = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Apply Promo Coupon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = {
                            couponInput = it
                            couponError = null
                        },
                        placeholder = { Text("Enter coupon code") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val found = availableCoupons.find { it.code.equals(couponInput.trim(), ignoreCase = true) }
                            if (found != null) {
                                val success = onApplyCoupon(found)
                                if (success) {
                                    couponSheetOpen = false
                                } else {
                                    couponError = "Minimum order of ₹${found.minOrder.toInt()} required"
                                }
                            } else {
                                couponError = "Invalid coupon code"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (couponError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = couponError!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "AVAILABLE OFFERS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                availableCoupons.forEach { c ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                val success = onApplyCoupon(c)
                                if (success) {
                                    couponSheetOpen = false
                                } else {
                                    couponError = "Minimum order of ₹${c.minOrder.toInt()} required"
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = FoodHubOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = c.code,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = FoodHubOrange,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = c.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = c.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    val success = onApplyCoupon(c)
                                    if (success) {
                                        couponSheetOpen = false
                                    } else {
                                        couponError = "Minimum order of ₹${c.minOrder.toInt()} required"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Apply", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDiscount) VegGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}
