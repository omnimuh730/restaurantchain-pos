package com.mh.restaurantchainpos.pos.data

enum class AuthRoute { SignIn, SignUp, Lock, Pos }

data class AuthSession(
    val name: String = "Admin",
    val username: String = "admin",
)
