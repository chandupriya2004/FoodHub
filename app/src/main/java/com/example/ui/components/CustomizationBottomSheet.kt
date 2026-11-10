package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodAddOn
import com.example.data.model.FoodItem
import com.example.data.model.PortionSize
import com.example.ui.theme.FoodHubOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationBottomSheet(
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onConfirmAdd: (PortionSize?, List<FoodAddOn>, Int, String) -> Unit
) {
    var selectedPortion by remember {
        mutableStateOf(foodItem.portionSizes.firstOrNull())
    }
    val selectedAddOns = remember {
        mutableStateListOf<FoodAddOn>()
    }
    var quantity by remember { mutableIntStateOf(1) }
    var specialInstructions by remember { mutableStateOf("") }

    val currentUnitPrice = remember(selectedPortion, selectedAddOns.toList()) {
        val portionExtra = selectedPortion?.priceDelta ?: 0.0
        val addOnsExtra = selectedAddOns.sumOf { it.price }
        foodItem.price + portionExtra + addOnsExtra
    }

    val currentTotalPrice = currentUnitPrice * quantity

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    VegNonVegBadge(isVeg = foodItem.isVeg)
                    Text(
                        text = foodItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Customise as per your taste",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Portion Sizes (Radio options)
            if (foodItem.portionSizes.isNotEmpty()) {
                Text(
                    text = "Choice of Size",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                foodItem.portionSizes.forEach { portion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPortion = portion }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPortion?.id == portion.id,
                                onClick = { selectedPortion = portion },
                                colors = RadioButtonDefaults.colors(selectedColor = FoodHubOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = portion.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (portion.priceDelta > 0) {
                            Text(
                                text = "+₹${portion.priceDelta.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            // Add-ons (Checkbox options)
            if (foodItem.addOns.isNotEmpty()) {
                Text(
                    text = "Add-ons & Extras",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                foodItem.addOns.forEach { addOn ->
                    val isChecked = selectedAddOns.any { it.id == addOn.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) {
                                    selectedAddOns.removeAll { it.id == addOn.id }
                                } else {
                                    selectedAddOns.add(addOn)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) selectedAddOns.add(addOn)
                                    else selectedAddOns.removeAll { it.id == addOn.id }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = FoodHubOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = addOn.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "+₹${addOn.price.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            // Cooking instructions
            Text(
                text = "Special Cooking Request",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = specialInstructions,
                onValueChange = { specialInstructions = it },
                placeholder = { Text("e.g. Less spicy, send extra spoons, no onions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("special_instructions_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quantity Stepper and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Stepper
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        onConfirmAdd(selectedPortion, selectedAddOns.toList(), quantity, specialInstructions)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("confirm_add_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange)
                ) {
                    Text(
                        text = "Add Item · ₹${currentTotalPrice.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
