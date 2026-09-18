package com.statusking.ai.service

import android.content.Context
import android.util.Log
import com.statusking.ai.data.local.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

object AdMobConfig {
    // Official Google AdMob Test Ad Unit IDs for Android
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

    // Replace these with your production AdMob IDs before publishing to Google Play
    var PRODUCTION_APP_ID: String? = null
    var PRODUCTION_BANNER_ID: String? = null
    var PRODUCTION_INTERSTITIAL_ID: String? = null
    var PRODUCTION_REWARDED_ID: String? = null

    val currentBannerId: String
        get() = PRODUCTION_BANNER_ID ?: TEST_BANNER_ID

    val currentInterstitialId: String
        get() = PRODUCTION_INTERSTITIAL_ID ?: TEST_INTERSTITIAL_ID

    val currentRewardedId: String
        get() = PRODUCTION_REWARDED_ID ?: TEST_REWARDED_ID
}

class AdsManager(private val preferencesManager: PreferencesManager) {

    private val isPremium: StateFlow<Boolean> = preferencesManager.isPremium
    private var lastInterstitialShownTime: Long = 0
    private val interstitialCooldownMs: Long = 60_000 // Minimum 60 seconds between interstitials to avoid excessive ads

    fun shouldShowAds(): Boolean {
        return !isPremium.value
    }

    fun showInterstitial(context: Context, placementName: String, onDismissed: () -> Unit) {
        if (!shouldShowAds()) {
            onDismissed()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInterstitialShownTime < interstitialCooldownMs) {
            Log.d("AdsManager", "Interstitial on cooldown for placement: $placementName")
            onDismissed()
            return
        }

        lastInterstitialShownTime = currentTime
        Log.d("AdsManager", "Showing AdMob Interstitial [Test ID: ${AdMobConfig.currentInterstitialId}] for placement: $placementName")
        // Ready for MobileAds SDK show callback
        onDismissed()
    }

    fun showRewardedAd(
        context: Context,
        onRewardEarned: (rewardAmount: Int) -> Unit,
        onDismissed: () -> Unit
    ) {
        if (!shouldShowAds()) {
            onRewardEarned(5)
            onDismissed()
            return
        }

        Log.d("AdsManager", "Showing AdMob Rewarded Ad [Test ID: ${AdMobConfig.currentRewardedId}]")
        // Award bonus free AI generations
        preferencesManager.addRewardedGenerations(5)
        onRewardEarned(5)
        onDismissed()
    }
}
