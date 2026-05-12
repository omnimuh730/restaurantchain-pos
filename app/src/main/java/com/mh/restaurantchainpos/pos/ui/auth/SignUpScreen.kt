package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.AuthSession
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import kotlinx.coroutines.delay

@Composable
fun SignUpScreen(
    onSignedUp: (AuthSession) -> Unit,
    onSignIn: () -> Unit,
) {
    var mode by remember { mutableStateOf(SignUpMode.Select) }

    var restaurantStep by remember { mutableStateOf(RestaurantStep.Form) }
    var restName by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var restUsername by remember { mutableStateOf("") }
    var restPassword by remember { mutableStateOf("") }
    var restConfirm by remember { mutableStateOf("") }
    var restError by remember { mutableStateOf("") }
    var restLoading by remember { mutableStateOf(false) }

    var staffStep by remember { mutableStateOf(StaffStep.SelectRestaurant) }
    var selectedRestaurantId by remember { mutableStateOf<String?>(null) }
    var restaurantSearch by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("") }
    var staffUsername by remember { mutableStateOf("") }
    var staffPassword by remember { mutableStateOf("") }
    var staffConfirm by remember { mutableStateOf("") }
    var staffError by remember { mutableStateOf("") }
    var staffLoading by remember { mutableStateOf(false) }
    var dropdownOpen by remember { mutableStateOf(false) }

    val selectedRestaurant = Restaurants.firstOrNull { it.id == selectedRestaurantId }

    val errAllRequired = stringResource(R.string.auth_su_error_all_required)
    val errPwdShort = stringResource(R.string.auth_su_error_password_short)
    val errMismatch = stringResource(R.string.auth_su_error_password_mismatch)

    fun reset() {
        mode = SignUpMode.Select
        restaurantStep = RestaurantStep.Form
        staffStep = StaffStep.SelectRestaurant
        restName = ""; adminName = ""; restUsername = ""; restPassword = ""; restConfirm = ""
        restError = ""; restLoading = false
        selectedRestaurantId = null; restaurantSearch = ""
        staffName = ""; staffUsername = ""; staffPassword = ""; staffConfirm = ""
        staffError = ""; staffLoading = false
    }

    LaunchedEffect(restLoading) {
        if (!restLoading) return@LaunchedEffect
        delay(1000)
        restLoading = false
        restaurantStep = RestaurantStep.WaitingApproval
    }
    LaunchedEffect(staffLoading) {
        if (!staffLoading) return@LaunchedEffect
        delay(1000)
        staffLoading = false
        staffStep = StaffStep.WaitingStaffApproval
    }

    AuthBackdrop {
        when (mode) {
            SignUpMode.Select -> {
                AuthBrand(stringResource(R.string.auth_su_select_title), stringResource(R.string.auth_su_select_subtitle))
                ChoiceCard(
                    stringResource(R.string.auth_su_choice_restaurant_title),
                    stringResource(R.string.auth_su_choice_restaurant_desc),
                ) { mode = SignUpMode.Restaurant }
                Spacer(Modifier.height(12.dp))
                ChoiceCard(
                    stringResource(R.string.auth_su_choice_staff_title),
                    stringResource(R.string.auth_su_choice_staff_desc),
                ) { mode = SignUpMode.Staff }
                Spacer(Modifier.height(20.dp))
                AuthSecondaryLink(
                    stringResource(R.string.auth_su_already_have),
                    stringResource(R.string.auth_sign_in),
                    onSignIn,
                )
            }
            SignUpMode.Restaurant -> when (restaurantStep) {
                RestaurantStep.Form -> {
                    AuthBrand(
                        stringResource(R.string.auth_su_restaurant_brand_title),
                        stringResource(R.string.auth_su_restaurant_brand_subtitle),
                    )
                    AuthField(
                        stringResource(R.string.auth_su_field_restaurant_name),
                        restName,
                        { restName = it; restError = "" },
                        stringResource(R.string.auth_su_ph_restaurant_name),
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_admin_name),
                        adminName,
                        { adminName = it; restError = "" },
                        stringResource(R.string.auth_su_ph_full_name),
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_username),
                        restUsername,
                        { restUsername = it; restError = "" },
                        stringResource(R.string.auth_su_ph_username),
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_password),
                        restPassword,
                        { restPassword = it; restError = "" },
                        stringResource(R.string.auth_su_ph_password),
                        password = true,
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_confirm_password),
                        restConfirm,
                        { restConfirm = it; restError = "" },
                        stringResource(R.string.auth_su_ph_confirm_password),
                        password = true,
                    )
                    if (restError.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        AuthErrorBox(restError)
                    }
                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = if (restLoading) {
                            stringResource(R.string.auth_su_submitting)
                        } else {
                            stringResource(R.string.auth_su_register_restaurant)
                        },
                        enabled = !restLoading,
                        loading = restLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        restError = ""
                        when {
                            restName.isBlank() || adminName.isBlank() || restUsername.isBlank() ->
                                restError = errAllRequired
                            restPassword.length < 6 -> restError = errPwdShort
                            restPassword != restConfirm -> restError = errMismatch
                            else -> restLoading = true
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink(stringResource(R.string.auth_su_back)) { reset() }
                }
                RestaurantStep.WaitingApproval -> WaitingPanel(
                    title = stringResource(R.string.auth_su_rest_submitted_title),
                    subtitle = stringResource(R.string.auth_su_rest_submitted_subtitle),
                    onSignIn = onSignIn,
                )
            }
            SignUpMode.Staff -> when (staffStep) {
                StaffStep.SelectRestaurant -> {
                    AuthBrand(
                        stringResource(R.string.auth_su_staff_brand_title),
                        stringResource(R.string.auth_su_staff_brand_subtitle),
                    )
                    Text(
                        stringResource(R.string.auth_su_select_restaurant_label),
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(6.dp))
                    DropdownButton(
                        label = selectedRestaurant?.name ?: stringResource(R.string.auth_su_choose_restaurant),
                        placeholder = selectedRestaurant == null,
                        open = dropdownOpen,
                        onToggle = { dropdownOpen = !dropdownOpen },
                    )
                    AnimatedVisibility(dropdownOpen) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2A2D35))
                                .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp)),
                        ) {
                            Box(Modifier.padding(8.dp)) {
                                AuthField(
                                    label = "",
                                    value = restaurantSearch,
                                    onChange = { restaurantSearch = it },
                                    placeholder = stringResource(R.string.auth_su_search_restaurants_ph),
                                )
                            }
                            val filtered = Restaurants.filter { it.name.contains(restaurantSearch, ignoreCase = true) }
                            if (filtered.isEmpty()) {
                                Text(
                                    stringResource(R.string.auth_su_no_restaurants),
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            } else {
                                LazyColumn(Modifier.heightIn(max = 192.dp)) {
                                    items(filtered) { rest ->
                                        val active = selectedRestaurantId == rest.id
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedRestaurantId = rest.id
                                                    dropdownOpen = false
                                                }
                                                .background(if (active) Blue600 else Color.Transparent)
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text("\uD83C\uDFEA", fontSize = 13.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Text(rest.name, color = if (active) Color.White else Color(0xFFE5E7EB), fontSize = 13.sp)
                                            if (rest.approved) {
                                                Spacer(Modifier.weight(1f))
                                                Box(
                                                    Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (active) Blue600.copy(alpha = 0.7f) else Color(0xFF1E3A8A).copy(alpha = 0.4f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                ) {
                                                    Text(
                                                        stringResource(R.string.auth_su_restaurant_approved),
                                                        color = if (active) Color(0xFFDBEAFE) else Color(0xFF60A5FA),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = stringResource(R.string.auth_su_next),
                        enabled = selectedRestaurant != null,
                        loading = false,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val rest = selectedRestaurant ?: return@AuthPrimaryButton
                        staffStep = if (rest.approved) StaffStep.Details else StaffStep.WaitingRestaurantApproval
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink(stringResource(R.string.auth_su_back)) { reset() }
                }
                StaffStep.WaitingRestaurantApproval -> WaitingPanel(
                    title = stringResource(R.string.auth_su_rest_not_approved_title),
                    subtitle = stringResource(
                        R.string.auth_su_rest_not_approved_subtitle,
                        selectedRestaurant?.name.orEmpty(),
                    ),
                    onBack = { staffStep = StaffStep.SelectRestaurant },
                    onSignIn = onSignIn,
                )
                StaffStep.Details -> {
                    AuthBrand(
                        stringResource(R.string.auth_su_staff_details_title),
                        stringResource(
                            R.string.auth_su_staff_details_subtitle,
                            selectedRestaurant?.name.orEmpty(),
                        ),
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E3A8A).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF1E40AF).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("\uD83C\uDFEA", fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(selectedRestaurant?.name ?: "", color = Color(0xFF60A5FA), fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            stringResource(R.string.auth_su_change_restaurant),
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp,
                            modifier = Modifier.clickableNoIndication { staffStep = StaffStep.SelectRestaurant },
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_staff_full_name),
                        staffName,
                        { staffName = it; staffError = "" },
                        stringResource(R.string.auth_su_ph_full_name),
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_username),
                        staffUsername,
                        { staffUsername = it; staffError = "" },
                        stringResource(R.string.auth_su_ph_username),
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_password),
                        staffPassword,
                        { staffPassword = it; staffError = "" },
                        stringResource(R.string.auth_su_ph_password),
                        password = true,
                    )
                    Spacer(Modifier.height(12.dp))
                    AuthField(
                        stringResource(R.string.auth_su_field_confirm_password),
                        staffConfirm,
                        { staffConfirm = it; staffError = "" },
                        stringResource(R.string.auth_su_ph_confirm_password),
                        password = true,
                    )
                    if (staffError.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        AuthErrorBox(staffError)
                    }
                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = if (staffLoading) {
                            stringResource(R.string.auth_su_submitting)
                        } else {
                            stringResource(R.string.auth_su_request_join)
                        },
                        enabled = !staffLoading,
                        loading = staffLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        staffError = ""
                        when {
                            staffName.isBlank() || staffUsername.isBlank() ->
                                staffError = errAllRequired
                            staffPassword.length < 6 -> staffError = errPwdShort
                            staffPassword != staffConfirm -> staffError = errMismatch
                            else -> staffLoading = true
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink(stringResource(R.string.auth_su_back)) { staffStep = StaffStep.SelectRestaurant }
                }
                StaffStep.WaitingStaffApproval -> WaitingPanel(
                    title = stringResource(R.string.auth_su_request_submitted_title),
                    subtitle = stringResource(
                        R.string.auth_su_request_submitted_subtitle,
                        selectedRestaurant?.name.orEmpty(),
                    ),
                    onSignIn = onSignIn,
                )
            }
        }
    }
}
