package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Address
import com.example.data.model.UserRole
import com.example.ui.theme.FoodHubGold
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRole: UserRole,
    walletBalance: Double,
    addresses: List<Address>,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onAddFunds: (Double) -> Unit,
    onAddAddress: (Address) -> Unit,
    modifier: Modifier = Modifier
) {
    var addFundsOpen by remember { mutableStateOf(false) }
    var addAddressOpen by remember { mutableStateOf(false) }
    var newAddressTitle by remember { mutableStateOf("Home") }
    var newAddressText by remember { mutableStateOf("") }
    var newAddressLandmark by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var policyDialogText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = FoodHubOrange.copy(alpha = 0.15f),
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "AS",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = FoodHubOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ananya Sharma",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+91 98765 43210 · ananya.s@example.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // FoodHub Gold Membership Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = FoodHubGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FOODHUB GOLD",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = FoodHubGold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlimited Free Delivery on all restaurant orders above ₹149",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FoodHubGold
                    ) {
                        Text(
                            text = "ACTIVE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Wallet Balance Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VegGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = VegGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = "FoodHub Wallet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "₹${walletBalance.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Button(
                        onClick = { addFundsOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Funds", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Saved Addresses Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SAVED ADDRESSES (${addresses.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )

                        TextButton(onClick = { addAddressOpen = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = FoodHubOrange)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New", color = FoodHubOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    addresses.forEach { addr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (addr.title) {
                                    "Home" -> Icons.Default.Home
                                    "Work" -> Icons.Default.Work
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = FoodHubOrange,
                                modifier = Modifier.size(20.dp)
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
                        }
                    }
                }
            }
        }

        // Platform Switcher & Settings
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "PLATFORM SETTINGS & ROLES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch Role Tile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenRoleSwitcher() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = FoodHubOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Switch Platform Role", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "Current: ${currentRole.title}",
                                    fontSize = 12.sp,
                                    color = FoodHubOrange
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Dark Theme Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = FoodHubOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Dark Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = if (isDarkTheme) "Dark theme enabled" else "Light theme enabled",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleDarkTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = FoodHubOrange)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Refer and Earn
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                policyDialogText = "Referral Code: HUBWIN100\nShare with friends! When they place their first order, they get ₹100 off, and you receive ₹100 in your FoodHub Wallet."
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = FoodHubOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Refer Friends & Earn ₹100", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Code: HUBWIN100", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Privacy & Terms
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                policyDialogText = "FoodHub India Privacy Policy & Terms:\n\n1. All payments are verified securely via PCI-DSS compliant channels.\n2. Your live location is only used to compute accurate restaurant delivery radius and real-time order tracking.\n3. Transparent pricing: Item price, 5% GST taxes, and ₹5 platform maintenance fee."
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = FoodHubOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Privacy Policy & Terms", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // Add Funds Dialog
    if (addFundsOpen) {
        var fundAmount by remember { mutableDoubleStateOf(200.0) }
        AlertDialog(
            onDismissRequest = { addFundsOpen = false },
            title = { Text("Add Money to FoodHub Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select amount to recharge:", fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100.0, 200.0, 500.0).forEach { amt ->
                            Button(
                                onClick = { fundAmount = amt },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (fundAmount == amt) VegGreen else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "₹${amt.toInt()}",
                                    color = if (fundAmount == amt) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddFunds(fundAmount)
                        addFundsOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                ) {
                    Text("Top-up ₹${fundAmount.toInt()}", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { addFundsOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Add Address Dialog
    if (addAddressOpen) {
        AlertDialog(
            onDismissRequest = { addAddressOpen = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Home", "Work", "Other").forEach { t ->
                            FilterChip(
                                selected = newAddressTitle == t,
                                onClick = { newAddressTitle = t },
                                label = { Text(t) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = newAddressText,
                        onValueChange = { newAddressText = it },
                        placeholder = { Text("Flat / House No, Street, Locality") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = newAddressLandmark,
                        onValueChange = { newAddressLandmark = it },
                        placeholder = { Text("Landmark (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressText.isNotBlank()) {
                            onAddAddress(
                                Address(
                                    title = newAddressTitle,
                                    fullAddress = newAddressText.trim(),
                                    landmark = newAddressLandmark.trim(),
                                    isDefault = false
                                )
                            )
                            newAddressText = ""
                            newAddressLandmark = ""
                            addAddressOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)
                ) {
                    Text("Save Address", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { addAddressOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Policy info dialog
    if (policyDialogText != null) {
        AlertDialog(
            onDismissRequest = { policyDialogText = null },
            title = { Text("Information") },
            text = { Text(policyDialogText!!) },
            confirmButton = {
                Button(onClick = { policyDialogText = null }, colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)) {
                    Text("OK")
                }
            }
        )
    }
}
