package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.RatingGold

@Composable
fun ReviewDialog(
    order: Order,
    onDismiss: () -> Unit,
    onSubmitReview: (Double, String, List<String>) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var reviewText by remember { mutableStateOf("") }
    val availableTags = listOf("Delicious Food", "Super Fast Delivery", "Hot & Fresh", "Eco Packaging", "Great Portions")
    val selectedTags = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Rate your Order",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${order.restaurant.name} · #${order.orderNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Star Rating
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    for (i in 1..5) {
                        val isFilled = i <= rating
                        Icon(
                            imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Star $i",
                            tint = if (isFilled) RatingGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { rating = i.toDouble() }
                                .padding(4.dp)
                        )
                    }
                }

                Text(
                    text = when (rating.toInt()) {
                        5 -> "Excellent! Loved it! ⭐⭐⭐⭐⭐"
                        4 -> "Very Good! ⭐⭐⭐⭐"
                        3 -> "Average ⭐⭐⭐"
                        2 -> "Below Expectation ⭐⭐"
                        else -> "Poor experience ⭐"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FoodHubOrange
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tag Chips
                Text(
                    text = "What did you like most?",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.take(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Write your review or compliment...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_comment_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitReview(rating, reviewText, selectedTags.toList())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FoodHubOrange),
                modifier = Modifier.testTag("submit_review_button")
            ) {
                Text("Submit Review", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
