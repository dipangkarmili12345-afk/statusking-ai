package com.statusking.ai.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.statusking.ai.service.BillingManager
import com.statusking.ai.service.BillingResult
import com.statusking.ai.service.SubscriptionProduct
import com.statusking.ai.ui.theme.AccentGold
import com.statusking.ai.ui.theme.AccentNeonPink
import com.statusking.ai.ui.theme.PrimaryViolet
import com.statusking.ai.ui.theme.PrimaryVioletDark
import com.statusking.ai.ui.theme.PrimaryVioletLight

@Composable
fun PremiumScreen(
    billingManager: BillingManager,
    isPremium: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPlanId by remember { mutableStateOf("statusking_yearly_sub") }
    var placeholderDialogMessage by remember { mutableStateOf<String?>(null) }

    val benefits = listOf(
        "Remove watermark from all exports" to "👑",
        "100% Ad-Free experience everywhere" to "🚫",
        "Ultra HD 9:16 high-resolution export" to "⚡",
        "Unlock all VIP premium templates" to "🎨",
        "Exclusive Royal fonts & typography" to "✍️",
        "Unlimited AI status generations" to "🚀",
        "Exclusive gradient backgrounds & glow" to "🌟",
        "Unlimited saved favorites & collections" to "❤️",
        "Priority Indian social media poster designs" to "🇮🇳"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C0318),
                        Color(0xFF1B0A33),
                        Color(0xFF280E4D)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("premium_screen")
    ) {
        // Top Close & Status Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            if (isPremium) {
                Surface(
                    color = AccentGold,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "VIP ACTIVE",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = Color(0xFF382300),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Banner
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(AccentGold, Color(0xFFFF9E00), PrimaryVioletLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "StatusKing Premium",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Elevate your social media presence with royal VIP features",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Benefits List
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "VIP BENEFITS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentGold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        benefits.forEach { (benefit, icon) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(AccentGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "$icon  $benefit",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Plans Selector (Monthly vs Yearly)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CHOOSE YOUR PLAN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                billingManager.availablePlans.forEach { plan ->
                    val isSelected = selectedPlanId == plan.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedPlanId = plan.id }
                            .testTag("plan_card_${plan.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AccentGold.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f)
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AccentGold else Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    if (plan.savingsBadge != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = AccentNeonPink,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = plan.savingsBadge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plan.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            Text(
                                text = plan.priceText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) AccentGold else Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Upgrade Button
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        billingManager.launchPurchaseFlow(
                            activity = context as? Activity,
                            productId = selectedPlanId,
                            simulateForDevTesting = false
                        ) { result ->
                            when (result) {
                                is BillingResult.Success -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                }
                                is BillingResult.DevPlaceholder -> {
                                    placeholderDialogMessage = result.note
                                }
                                is BillingResult.Error -> {
                                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = Color(0xFF2E1C00)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("subscribe_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.WorkspacePremium,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upgrade to StatusKing VIP",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            // Developer Testing Sandbox Toggle (Honest development placeholder as instructed)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, PrimaryVioletLight.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Developer Sandbox Mode",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                )
                                Text(
                                    text = "Toggle VIP state for testing ads/watermark without Google Play Console billing setup.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            }
                            Switch(
                                checked = isPremium,
                                onCheckedChange = { billingManager.toggleDevPremium() },
                                modifier = Modifier.testTag("dev_vip_toggle")
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Google Play Billing Dev Placeholder Dialog
        if (placeholderDialogMessage != null) {
            AlertDialog(
                onDismissRequest = { placeholderDialogMessage = null },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = AccentGold
                    )
                },
                title = {
                    Text(
                        text = "Google Play Billing Setup",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = placeholderDialogMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            billingManager.launchPurchaseFlow(
                                activity = context as? Activity,
                                productId = selectedPlanId,
                                simulateForDevTesting = true
                            ) {
                                placeholderDialogMessage = null
                            }
                        }
                    ) {
                        Text("Enable VIP (Dev Mode)")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { placeholderDialogMessage = null }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}
