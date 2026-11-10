package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeliveryBlue
import com.example.ui.theme.FoodHubOrange
import com.example.ui.theme.VegGreen
import kotlin.math.atan2

@Composable
fun LiveMapCanvas(
    restaurantName: String,
    customerLocationName: String,
    riderProgress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier
) {
    // Pulse animation for the active rider marker
    val infiniteTransition = rememberInfiniteTransition(label = "rider_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE9ECEF))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background Map Terrain Features
            // Draw subtle park area
            drawRoundRect(
                color = Color(0xFFD4EDDA),
                topLeft = Offset(w * 0.55f, h * 0.1f),
                size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.35f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            // Draw subtle lake/water feature
            drawRoundRect(
                color = Color(0xFFD0E1FD),
                topLeft = Offset(w * 0.05f, h * 0.65f),
                size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
            )

            // Draw road grid lines
            val roadColor = Color(0xFFFFFFFF)
            val roadStroke = 12f
            // Horizontal roads
            drawLine(roadColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = roadStroke)
            drawLine(roadColor, Offset(0f, h * 0.55f), Offset(w, h * 0.55f), strokeWidth = roadStroke)
            drawLine(roadColor, Offset(0f, h * 0.82f), Offset(w, h * 0.82f), strokeWidth = roadStroke)
            // Vertical roads
            drawLine(roadColor, Offset(w * 0.22f, 0f), Offset(w * 0.22f, h), strokeWidth = roadStroke)
            drawLine(roadColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = roadStroke)
            drawLine(roadColor, Offset(w * 0.78f, 0f), Offset(w * 0.78f, h), strokeWidth = roadStroke)

            // Delivery Route Path (Restaurant -> Waypoints -> Customer)
            val p0 = Offset(w * 0.18f, h * 0.25f) // Restaurant
            val p1 = Offset(w * 0.5f, h * 0.25f)
            val p2 = Offset(w * 0.5f, h * 0.55f)
            val p3 = Offset(w * 0.78f, h * 0.55f)
            val p4 = Offset(w * 0.78f, h * 0.82f) // Customer House

            val fullRoutePath = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }

            // Draw Route Shadow / Outer Glow
            drawPath(
                path = fullRoutePath,
                color = Color(0x33FF5200),
                style = Stroke(width = 10f)
            )

            // Draw Dashed Route Path
            drawPath(
                path = fullRoutePath,
                color = FoodHubOrange,
                style = Stroke(
                    width = 6f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                )
            )

            // Interpolate Rider Position along the segments
            val currentProgress = riderProgress.coerceIn(0f, 1f)
            val riderPos: Offset
            when {
                currentProgress <= 0.25f -> {
                    val t = currentProgress / 0.25f
                    riderPos = Offset(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t)
                }
                currentProgress <= 0.5f -> {
                    val t = (currentProgress - 0.25f) / 0.25f
                    riderPos = Offset(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t)
                }
                currentProgress <= 0.75f -> {
                    val t = (currentProgress - 0.5f) / 0.25f
                    riderPos = Offset(p2.x + (p3.x - p2.x) * t, p2.y + (p3.y - p2.y) * t)
                }
                else -> {
                    val t = (currentProgress - 0.75f) / 0.25f
                    riderPos = Offset(p3.x + (p4.x - p3.x) * t, p3.y + (p4.y - p3.y) * t)
                }
            }

            // Restaurant Marker
            drawCircle(color = FoodHubOrange, radius = 16f, center = p0)
            drawCircle(color = Color.White, radius = 8f, center = p0)

            // Customer Destination Marker
            drawCircle(color = VegGreen, radius = 18f, center = p4)
            drawCircle(color = Color.White, radius = 9f, center = p4)

            // Rider Marker Pulse Ring
            drawCircle(
                color = DeliveryBlue.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = riderPos
            )
            // Rider Center Pin
            drawCircle(color = DeliveryBlue, radius = 16f, center = riderPos)
            drawCircle(color = Color.White, radius = 6f, center = riderPos)
        }

        // Overlay Labels for Restaurant & Customer
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 12.dp),
            color = Color.Black.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = FoodHubOrange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = restaurantName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp),
            color = Color.Black.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = VegGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = customerLocationName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Floating ETA Badge Center-Top
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            color = FoodHubOrange,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val remainingMins = maxOf(2, ((1f - riderProgress) * 20).toInt())
                Text(
                    text = if (riderProgress >= 0.95f) "Arrived at Doorstep" else "$remainingMins mins away · Live GPS",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
