package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.FoodHubOrange

@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Switch Platform Role",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Experience all personas in the FoodHub ecosystem",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoleOptionCard(
                    role = UserRole.CUSTOMER,
                    icon = Icons.Default.Person,
                    title = "Customer Experience",
                    subtitle = "Order food, customize dishes, live GPS tracking",
                    isSelected = currentRole == UserRole.CUSTOMER,
                    onClick = {
                        onRoleSelected(UserRole.CUSTOMER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    role = UserRole.RESTAURANT_PARTNER,
                    icon = Icons.Default.Restaurant,
                    title = "Restaurant Partner Portal",
                    subtitle = "Accept incoming orders, manage menu inventory",
                    isSelected = currentRole == UserRole.RESTAURANT_PARTNER,
                    onClick = {
                        onRoleSelected(UserRole.RESTAURANT_PARTNER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    role = UserRole.DELIVERY_PARTNER,
                    icon = Icons.Default.DeliveryDining,
                    title = "Delivery Rider App",
                    subtitle = "Online duty, accept pickups, verify OTP delivery",
                    isSelected = currentRole == UserRole.DELIVERY_PARTNER,
                    onClick = {
                        onRoleSelected(UserRole.DELIVERY_PARTNER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    role = UserRole.ADMIN,
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Admin Central Dashboard",
                    subtitle = "Platform metrics, commissions, promo coupons",
                    isSelected = currentRole == UserRole.ADMIN,
                    onClick = {
                        onRoleSelected(UserRole.ADMIN)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun RoleOptionCard(
    role: UserRole,
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) FoodHubOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val bgColor = if (isSelected) FoodHubOrange.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("switch_to_${role.name.lowercase()}")
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FoodHubOrange else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
