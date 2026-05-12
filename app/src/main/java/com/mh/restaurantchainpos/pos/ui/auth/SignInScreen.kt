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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.R
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

    val invalidCredentials = stringResource(R.string.auth_error_invalid_credentials)
    val errUsernameBlank = stringResource(R.string.auth_error_username_blank)
    val errPasswordShort = stringResource(R.string.auth_error_password_short)

    LaunchedEffect(loading) {
        if (!loading) return@LaunchedEffect
        delay(800)
        if (username == "demo" && password == "000000") {
            error = invalidCredentials
            loading = false
            return@LaunchedEffect
        }
        loading = false
        onSignedIn(AuthSession(name = username.ifBlank { "Admin" }, username = username.ifBlank { "admin" }))
    }

    AuthBackdrop {
        AuthBrand(
            stringResource(R.string.auth_welcome_back),
            stringResource(R.string.auth_sign_in_subtitle),
        )
        AuthField(
            label = stringResource(R.string.auth_username),
            value = username,
            onChange = { username = it; error = "" },
            placeholder = stringResource(R.string.auth_username_ph),
        )
        Spacer(Modifier.height(16.dp))
        AuthField(
            label = stringResource(R.string.auth_password),
            value = password,
            onChange = { password = it; error = "" },
            placeholder = stringResource(R.string.auth_password_ph),
            password = true,
        )
        if (error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            AuthErrorBox(error)
        }
        Spacer(Modifier.height(20.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_sign_in),
            enabled = !loading,
            loading = loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            error = ""
            if (username.isBlank()) {
                error = errUsernameBlank
                return@AuthPrimaryButton
            }
            if (password.length < 6) {
                error = errPasswordShort
                return@AuthPrimaryButton
            }
            loading = true
        }
        Spacer(Modifier.height(24.dp))
        AuthSecondaryLink(
            stringResource(R.string.auth_no_account),
            stringResource(R.string.auth_sign_up),
            onSignUp,
        )
    }
}
