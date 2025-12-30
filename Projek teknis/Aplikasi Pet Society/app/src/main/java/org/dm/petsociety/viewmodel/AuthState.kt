package org.dm.petsociety.viewmodel

// File ini berisi semua Data Class yang merepresentasikan status (state) UI.
// Tujuannya agar bisa diimpor dan diakses oleh semua Screen.

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: String = "",
    val isLoading: Boolean = false
)

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false
)