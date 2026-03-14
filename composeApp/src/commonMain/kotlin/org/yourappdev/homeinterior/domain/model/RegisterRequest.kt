package org.yourappdev.homeinterior.domain.model

data class RegisterRequest(
    val fullname: String,
    val email: String,
    val password: String
)
