package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.AuthSession
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import kotlinx.coroutines.delay

private data class SignUpRestaurant(val id: String, val name: String, val approved: Boolean)

private val Restaurants = listOf(
    SignUpRestaurant("r1", "Glass Onion", true),
    SignUpRestaurant("r2", "The Blue Lotus", true),
    SignUpRestaurant("r3", "Sakura Garden", true),
    SignUpRestaurant("r4", "Dragon Pearl", false),
    SignUpRestaurant("r5", "Bamboo House", true),
)

private enum class SignUpMode { Select, Restaurant, Staff }
private enum class StaffStep { SelectRestaurant, WaitingRestaurantApproval, Details, WaitingStaffApproval }
private enum class RestaurantStep { Form, WaitingApproval }

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
                AuthBrand("Create Account", "Choose how you'd like to sign up")
                ChoiceCard("Restaurant Sign Up", "Register a new restaurant as admin") { mode = SignUpMode.Restaurant }
                Spacer(Modifier.height(12.dp))
                ChoiceCard("Staff Sign Up", "Join an existing restaurant as staff") { mode = SignUpMode.Staff }
                Spacer(Modifier.height(20.dp))
                AuthSecondaryLink("Already have an account?", "Sign In", onSignIn)
            }
            SignUpMode.Restaurant -> when (restaurantStep) {
                RestaurantStep.Form -> {
                    AuthBrand("Restaurant Sign Up", "Register your restaurant on the platform")
                    AuthField("Restaurant Name *", restName, { restName = it; restError = "" }, "e.g. Glass Onion")
                    Spacer(Modifier.height(12.dp))
                    AuthField("Admin Name *", adminName, { adminName = it; restError = "" }, "Your full name")
                    Spacer(Modifier.height(12.dp))
                    AuthField("Username *", restUsername, { restUsername = it; restError = "" }, "Choose a username")
                    Spacer(Modifier.height(12.dp))
                    AuthField("Password *", restPassword, { restPassword = it; restError = "" }, "Create a password", password = true)
                    Spacer(Modifier.height(12.dp))
                    AuthField("Confirm Password *", restConfirm, { restConfirm = it; restError = "" }, "Confirm your password", password = true)
                    if (restError.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        AuthErrorBox(restError)
                    }
                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = if (restLoading) "Submitting" else "Register Restaurant",
                        enabled = !restLoading,
                        loading = restLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        restError = ""
                        when {
                            restName.isBlank() || adminName.isBlank() || restUsername.isBlank() ->
                                restError = "All fields are required."
                            restPassword.length < 6 -> restError = "Password must be at least 6 characters."
                            restPassword != restConfirm -> restError = "Passwords do not match."
                            else -> restLoading = true
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink("Back") { reset() }
                }
                RestaurantStep.WaitingApproval -> WaitingPanel(
                    title = "Registration Submitted",
                    subtitle = "Your restaurant registration is pending approval. You'll be automatically logged in once approved.",
                    onSignIn = onSignIn,
                )
            }
            SignUpMode.Staff -> when (staffStep) {
                StaffStep.SelectRestaurant -> {
                    AuthBrand("Staff Sign Up", "Select your restaurant to get started")
                    Text("Select Restaurant *", color = Color(0xFF9CA3AF), fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    DropdownButton(
                        label = selectedRestaurant?.name ?: "Choose a restaurant...",
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
                                    placeholder = "Search restaurants...",
                                )
                            }
                            val filtered = Restaurants.filter { it.name.contains(restaurantSearch, ignoreCase = true) }
                            if (filtered.isEmpty()) {
                                Text(
                                    "No restaurants found",
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
                                                        "Approved",
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
                        text = "Next",
                        enabled = selectedRestaurant != null,
                        loading = false,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val rest = selectedRestaurant ?: return@AuthPrimaryButton
                        staffStep = if (rest.approved) StaffStep.Details else StaffStep.WaitingRestaurantApproval
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink("Back") { reset() }
                }
                StaffStep.WaitingRestaurantApproval -> WaitingPanel(
                    title = "Restaurant Not Approved",
                    subtitle = "\"${selectedRestaurant?.name}\" is still pending approval. You can sign up once the restaurant has been approved.",
                    onBack = { staffStep = StaffStep.SelectRestaurant },
                    onSignIn = onSignIn,
                )
                StaffStep.Details -> {
                    AuthBrand("Staff Details", "Create your staff account for ${selectedRestaurant?.name ?: ""}")
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
                            "Change",
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp,
                            modifier = Modifier.clickableNoIndication { staffStep = StaffStep.SelectRestaurant },
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    AuthField("Full Name *", staffName, { staffName = it; staffError = "" }, "Your full name")
                    Spacer(Modifier.height(12.dp))
                    AuthField("Username *", staffUsername, { staffUsername = it; staffError = "" }, "Choose a username")
                    Spacer(Modifier.height(12.dp))
                    AuthField("Password *", staffPassword, { staffPassword = it; staffError = "" }, "Create a password", password = true)
                    Spacer(Modifier.height(12.dp))
                    AuthField("Confirm Password *", staffConfirm, { staffConfirm = it; staffError = "" }, "Confirm your password", password = true)
                    if (staffError.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        AuthErrorBox(staffError)
                    }
                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = if (staffLoading) "Submitting" else "Request to Join",
                        enabled = !staffLoading,
                        loading = staffLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        staffError = ""
                        when {
                            staffName.isBlank() || staffUsername.isBlank() ->
                                staffError = "All fields are required."
                            staffPassword.length < 6 -> staffError = "Password must be at least 6 characters."
                            staffPassword != staffConfirm -> staffError = "Passwords do not match."
                            else -> staffLoading = true
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BackTextLink("Back") { staffStep = StaffStep.SelectRestaurant }
                }
                StaffStep.WaitingStaffApproval -> WaitingPanel(
                    title = "Request Submitted",
                    subtitle = "Your request to join \"${selectedRestaurant?.name}\" has been sent. The admin will review and approve your registration.",
                    onSignIn = onSignIn,
                )
            }
        }
    }
}

@Composable
private fun ChoiceCard(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1D25))
            .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
            .clickableNoIndication(onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Blue600.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("→", color = Color(0xFF60A5FA), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = Color(0xFFE5E7EB), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF6B7280), fontSize = 12.sp)
        }
        Text("›", color = Color(0xFF6B7280), fontSize = 18.sp)
    }
}

@Composable
private fun DropdownButton(label: String, placeholder: Boolean, open: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A1D25))
            .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
            .clickableNoIndication(onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = if (placeholder) Color(0xFF4B5563) else Color(0xFFE5E7EB), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(if (open) "˄" else "˅", color = Color(0xFF6B7280), fontSize = 14.sp)
    }
}

@Composable
private fun WaitingPanel(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    onSignIn: () -> Unit,
) {
    Box(
        Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0xFF78350F).copy(alpha = 0.3f))
            .border(1.dp, Color(0xFFB45309).copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("⏱", color = Color(0xFFFBBF24), fontSize = 22.sp)
    }
    Spacer(Modifier.height(20.dp))
    Text(title, color = Color(0xFFE5E7EB), fontSize = 18.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
    Text(subtitle, color = Color(0xFF6B7280), fontSize = 13.sp)
    Spacer(Modifier.height(24.dp))
    if (onBack != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
                .clickableNoIndication(onBack)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Go Back", color = Color(0xFFD1D5DB), fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clickableNoIndication(onSignIn)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Back to Sign In", color = Color(0xFF60A5FA), fontSize = 13.sp)
    }
}

@Composable
private fun BackTextLink(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clickableNoIndication(onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("‹ $text", color = Color(0xFF6B7280), fontSize = 13.sp)
    }
}
