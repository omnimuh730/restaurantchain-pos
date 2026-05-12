package com.mh.restaurantchainpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mh.restaurantchainpos.locale.AppLocaleStore
import com.mh.restaurantchainpos.pos.ui.PosApp
import com.mh.restaurantchainpos.ui.theme.RestaurantchainPOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppLocaleStore.applyStoredLocaleBeforeActivityOnCreate(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestaurantchainPOSTheme(darkTheme = false) {
                PosApp()
            }
        }
    }
}