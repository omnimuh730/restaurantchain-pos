package com.mh.restaurantchainpos

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mh.restaurantchainpos.locale.AppLocaleStore
import com.mh.restaurantchainpos.pos.ui.PosApp
import com.mh.restaurantchainpos.ui.theme.RestaurantchainPOSTheme

class MainActivity : AppCompatActivity() {
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