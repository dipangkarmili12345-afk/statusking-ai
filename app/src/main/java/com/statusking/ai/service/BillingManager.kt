package com.statusking.ai.service

import android.app.Activity
import com.statusking.ai.data.local.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

data class SubscriptionProduct(
    val id: String,
    val title: String,
    val description: String,
    val priceText: String,
    val billingPeriod: String,
    val savingsBadge: String? = null
)

sealed class BillingResult {
    data class Success(val message: String) : BillingResult()
    data class DevPlaceholder(val note: String) : BillingResult()
    data class Error(val error: String) : BillingResult()
}

class BillingManager(private val preferencesManager: PreferencesManager) {

    val isPremium: StateFlow<Boolean> = preferencesManager.isPremium

    val availablePlans = listOf(
        SubscriptionProduct(
            id = "statusking_monthly_sub",
            title = "Monthly VIP",
            description = "Unlimited access, billed monthly",
            priceText = "₹149 / month",
            billingPeriod = "Month",
            savingsBadge = null
        ),
        SubscriptionProduct(
            id = "statusking_yearly_sub",
            title = "Annual Royal Pass",
            description = "Full year of premium features, save 65%",
            priceText = "₹499 / year",
            billingPeriod = "Year",
            savingsBadge = "SAVE 65%"
        )
    )

    /**
     * Google Play Billing launch placeholder.
     * Keeps production readiness: when Play Billing SDK is integrated, launchBillingFlow will be called here.
     * In development mode without active Google Play license keys, it displays a development placeholder
     * or allows developer simulation if explicitly toggled.
     */
    fun launchPurchaseFlow(
        activity: Activity?,
        productId: String,
        simulateForDevTesting: Boolean = false,
        onResult: (BillingResult) -> Unit
    ) {
        if (simulateForDevTesting) {
            preferencesManager.setPremium(true)
            onResult(BillingResult.Success("Premium unlocked for development testing!"))
        } else {
            // Respecting guideline: Do not pretend a fake payment succeeded
            onResult(
                BillingResult.DevPlaceholder(
                    "Google Play Billing requires Google Play Console setup, signed release APK, and test merchant account. Ready for Play Billing Client integration with SKU: $productId."
                )
            )
        }
    }

    fun toggleDevPremium() {
        preferencesManager.setPremium(!preferencesManager.isPremium.value)
    }
}
