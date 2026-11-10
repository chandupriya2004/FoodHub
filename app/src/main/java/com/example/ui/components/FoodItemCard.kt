package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FoodItem
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.RatingGold
import com.example.ui.theme.VegGreen

@Composable
fun VegNonVegBadge(isVeg: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isVeg) VegGreen else NonVegRed
    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(borderColor, CircleShape)
        )
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("food_item_${foodItem.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Details Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VegNonVegBadge(isVeg = foodItem.isVeg)
                    if (foodItem.isBestseller) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "BESTSELLER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "₹${foodItem.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (foodItem.originalPrice > foodItem.price) {
                        Text(
                            text = "₹${foodItem.originalPrice.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = RatingGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${foodItem.rating} (${foodItem.reviewCount})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = foodItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (foodItem.portionSizes.isNotEmpty() || foodItem.addOns.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Customisable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FoodHubOrange
                    )
                }
            }

            // Image & Add Button Column
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(130.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = foodItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 14.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                // Add or Quantity Counter Button
                if (quantityInCart == 0) {
                    Surface(
                        modifier = Modifier
                            .testTag("add_button_${foodItem.id}")
                            .width(96.dp)
                            .height(38.dp)
                            .clickable { onAddToCart() },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, FoodHubOrange),
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "ADD",
                                fontWeight = FontWeight.ExtraBold,
                                color = FoodHubOrange,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .width(96.dp)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = FoodHubOrange,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onDecrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "$quantityInCart",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            IconButton(
                                onClick = onIncrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
