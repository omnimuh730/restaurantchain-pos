package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.AuthSession
import kotlinx.coroutines.delay

@Composable
fun SignInScreen(
    onSignedIn: (AuthSession) -> Unit,
    onSignUp: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(loading) {
        if (!loading) return@LaunchedEffect
        delay(800)
        if (username == "demo" && password == "000000") {
            error = "Invalid username or password."
            loading = false
            return@LaunchedEffect
        }
        loading = false
        onSignedIn(AuthSession(name = username.ifBlank { "Admin" }, username = username.ifBlank { "admin" }))
    }

    AuthBackdrop {
        AuthBrand("Welcome Back", "Sign in to your POS account")
        AuthField(
            label = "Username",
            value = username,
            onChange = { username = it; error = "" },
            placeholder = "Enter your username",
        )
        Spacer(Modifier.height(16.dp))
        AuthField(
            label = "Password",
            value = password,
            onChange = { password = it; error = "" },
            placeholder = "Enter your password",
            password = true,
        )
        if (error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            AuthErrorBox(error)
        }
        Spacer(Modifier.height(20.dp))
        AuthPrimaryButton(
            text = "Sign In",
            enabled = !loading,
            loading = loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            error = ""
            if (username.isBlank()) {
                error = "Please enter your username."
                return@AuthPrimaryButton
            }
            if (password.length < 6) {
                error = "Please enter your password (min 6 characters)."
                return@AuthPrimaryButton
            }
            loading = true
        }
        Spacer(Modifier.height(24.dp))
        AuthSecondaryLink("Don't have an account?", "Sign Up", onSignUp)
    }
}
