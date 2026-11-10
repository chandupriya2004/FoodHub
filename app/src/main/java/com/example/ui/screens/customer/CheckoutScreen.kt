package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.Restaurant
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    restaurant: Restaurant?,
    totalAmount: Double,
    selectedAddress: Address,
    availableAddresses: List<Address>,
    walletBalance: Double,
    onBack: () -> Unit,
    onSelectAddress: (Address) -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedPaymentType by remember { mutableStateOf("UPI") }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var upiIdInput by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var useWalletCash by remember { mutableStateOf(false) }
    var addressModalOpen by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val finalPayable = remember(totalAmount, useWalletCash, walletBalance) {
        if (useWalletCash) maxOf(0.0, totalAmount - walletBalance) else totalAmount
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout & Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                            text = "TOTAL PAYABLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${finalPayable.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(1200) // Realistic payment processing animation
                                val method = if (useWalletCash && finalPayable == 0.0) {
                                    "FoodHub Wallet"
                                } else {
                                    "$selectedPaymentType ($selectedUpiApp)"
                                }
                                onOrderPlaced(method)
                                isProcessing = false
                            }
                        },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("pay_and_place_order_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "Pay & Place Order",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delivery Address Selection Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = FoodHubOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DELIVERY ADDRESS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(onClick = { addressModalOpen = true }) {
                            Text("Change", color = FoodHubOrange, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedAddress.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = selectedAddress.fullAddress,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (selectedAddress.contactNumber.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Contact: ${selectedAddress.contactNumber}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // FoodHub Wallet Toggle
            if (walletBalance > 0) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { useWalletCash = !useWalletCash }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = VegGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Use FoodHub Wallet Cash",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Available: ₹${walletBalance.toInt()}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Checkbox(
                            checked = useWalletCash,
                            onCheckedChange = { useWalletCash = it },
                            colors = CheckboxDefaults.colors(checkedColor = VegGreen)
                        )
                    }
                }
            }

            // Payment Options
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
                        text = "SELECT PAYMENT METHOD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // UPI Option
                    PaymentMethodTile(
                        icon = Icons.Default.Smartphone,
                        title = "UPI (Instant & Zero Fee)",
                        subtitle = "Google Pay, PhonePe, Paytm, or any UPI ID",
                        isSelected = selectedPaymentType == "UPI",
                        onSelect = { selectedPaymentType = "UPI" }
                    )

                    if (selectedPaymentType == "UPI") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, bottom = 12.dp)
                        ) {
                            val upiApps = listOf("Google Pay", "PhonePe", "Paytm", "BHIM UPI")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                upiApps.forEach { app ->
                                    val isChosen = selectedUpiApp == app
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isChosen) FoodHubOrange.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isChosen) BorderStroke(1.5.dp, FoodHubOrange) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedUpiApp = app }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text(
                                                text = app,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) FoodHubOrange else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Credit / Debit Card
                    PaymentMethodTile(
                        icon = Icons.Default.CreditCard,
                        title = "Credit or Debit Card",
                        subtitle = "Visa, Mastercard, RuPay, Maestro",
                        isSelected = selectedPaymentType == "CARD",
                        onSelect = { selectedPaymentType = "CARD" }
                    )

                    if (selectedPaymentType == "CARD") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                placeholder = { Text("Card Number (16 digits)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    placeholder = { Text("MM/YY") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it },
                                    placeholder = { Text("CVV") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Cash on Delivery
                    PaymentMethodTile(
                        icon = Icons.Default.Payments,
                        title = "Cash on Delivery (COD)",
                        subtitle = "Pay cash or QR scan at doorstep on delivery",
                        isSelected = selectedPaymentType == "COD",
                        onSelect = { selectedPaymentType = "COD" }
                    )
                }
            }
        }
    }

    // Change Address BottomSheet
    if (addressModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { addressModalOpen = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Delivery Address",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                availableAddresses.forEach { addr ->
                    val isCur = addr.id == selectedAddress.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                onSelectAddress(addr)
                                addressModalOpen = false
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCur) FoodHubOrange.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = if (isCur) BorderStroke(1.5.dp, FoodHubOrange) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (addr.title) {
                                    "Home" -> Icons.Default.Home
                                    "Work" -> Icons.Default.Work
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = if (isCur) FoodHubOrange else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = addr.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = addr.fullAddress,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isCur) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = FoodHubOrange
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = FoodHubOrange)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) FoodHubOrange else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
