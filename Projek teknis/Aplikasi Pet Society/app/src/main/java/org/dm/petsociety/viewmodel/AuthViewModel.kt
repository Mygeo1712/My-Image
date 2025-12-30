// File: org.dm.petsociety.viewmodel.AuthViewModel.kt

package org.dm.petsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

// Data Class AuthState (diasumsikan sudah didefinisikan)
data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null,
    val userName: String = "Pengguna",
    val userRole: String = "",
    val requiresRoleSelection: Boolean = false,
    val isSessionChecked: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // State ini yang menyimpan semua akun yang pernah login
    private val _availableGoogleAccounts = MutableStateFlow(listOf<String>())
    val availableGoogleAccounts: StateFlow<List<String>> = _availableGoogleAccounts.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Menggunakan Data Class yang diimpor dari AuthState.kt
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordState())
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState.asStateFlow()


    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _authState.update { it.copy(isLoading = true) }
        val user = auth.currentUser
        if (user != null) {
            _authState.update { it.copy(userId = user.uid, isAuthenticated = true) }
            fetchUserAndRole(user.uid)
        } else {
            _authState.update { it.copy(isLoading = false, isAuthenticated = false, isSessionChecked = true) }
        }
    }

    fun fetchUserAndRole(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val doc = db.collection("users").document(userId).get().await()

                    if (doc.exists()) {
                        val name = doc.getString("name") ?: "Pengguna"
                        val role = doc.getString("role") ?: "Owner"

                        _authState.update {
                            it.copy(
                                isLoading = false,
                                userName = name,
                                userRole = role,
                                requiresRoleSelection = false,
                                errorMessage = null,
                                isSessionChecked = true
                            )
                        }
                    } else {
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                requiresRoleSelection = true,
                                userName = auth.currentUser?.displayName ?: "Pengguna Baru",
                                isSessionChecked = true
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AuthVM", "Error fetching user details: ${e.message}")
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Gagal memuat data pengguna: ${e.message}",
                            isSessionChecked = true
                        )
                    }
                }
            }
        }
    }

    private suspend fun fetchUserRole(uid: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("users").document(uid).get().await()
                document.getString("role") ?: "Unknown"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching user role: ${e.message}")
                "Error"
            }
        }
    }


    // -------------------------
    //          LOGIN
    // -------------------------

    fun setEmail(email: String) { _loginState.update { it.copy(email = email, errorMessage = null) } }
    fun setPassword(password: String) { _loginState.update { it.copy(password = password, errorMessage = null) } }
    fun clearLoginError() { _loginState.update { it.copy(errorMessage = null) } }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {

            val email = _loginState.value.email
            val password = _loginState.value.password

            if (email.isBlank() || password.isBlank()) {
                _loginState.update { it.copy(errorMessage = "Email dan Password tidak boleh kosong.") }
                return@launch
            }

            _loginState.update { it.copy(isLoading = true, errorMessage = null) }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result.user?.uid
                        if (uid != null) {
                            viewModelScope.launch {
                                fetchUserAndRole(uid)

                                // Ingat akun jika Gmail
                                if (email.endsWith("@gmail.com", ignoreCase = true)) {
                                    rememberGoogleAccount(email) // <-- Menggunakan fungsi yang diperbaiki
                                }

                                val role = fetchUserRole(uid)
                                _loginState.update { it.copy(isLoading = false) }
                                onSuccess(role)
                            }
                        } else {
                            _loginState.update { it.copy(isLoading = false, errorMessage = "User UID not found.") }
                        }
                    } else {
                        _loginState.update { it.copy(isLoading = false, errorMessage = task.exception?.localizedMessage ?: "Login gagal.") }
                    }
                }
        }
    }


    // -------------------------
    //        REGISTER
    // -------------------------

    fun setRegisterName(name: String) { _registerState.update { it.copy(name = name) } }
    fun setRegisterEmail(email: String) { _registerState.update { it.copy(email = email) } }
    fun setRegisterPassword(password: String) { _registerState.update { it.copy(password = password) } }
    fun setConfirmPassword(confirmPassword: String) { _registerState.update { it.copy(confirmPassword = confirmPassword) } }
    fun setRole(role: String) { _registerState.update { it.copy(role = role) } }

    fun register(onFinish: (Boolean, String) -> Unit) {
        val state = _registerState.value

        if (state.password != state.confirmPassword) {
            onFinish(false, "Password dan Konfirmasi Password tidak cocok.")
            return
        }
        if (state.role.isEmpty()) {
            onFinish(false, "Mohon pilih Role Anda.")
            return
        }

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true) }

            auth.createUserWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { authTask ->
                    _registerState.update { it.copy(isLoading = false) }

                    if (authTask.isSuccessful) {
                        val uid = authTask.result.user?.uid

                        if (uid != null) {
                            val userProfile = hashMapOf(
                                "name" to state.name,
                                "email" to state.email,
                                "role" to state.role,
                                "created_at" to Timestamp.now()
                            )

                            db.collection("users").document(uid).set(userProfile)
                                .addOnSuccessListener {
                                    // Ingat akun setelah berhasil register
                                    if (state.email.endsWith("@gmail.com", ignoreCase = true)) {
                                        rememberGoogleAccount(state.email) // <-- Menggunakan fungsi yang diperbaiki
                                    }
                                    onFinish(true, "Registrasi berhasil dan profil disimpan.")
                                }
                                .addOnFailureListener { e ->
                                    auth.currentUser?.delete()
                                    onFinish(false, "Gagal simpan profil: ${e.message}")
                                }
                        } else {
                            onFinish(false, "Registrasi Auth berhasil, namun UID tidak ditemukan.")
                        }

                    } else {
                        onFinish(false, authTask.exception?.localizedMessage ?: "Registrasi gagal.")
                    }
                }
        }
    }


    // -------------------------
    //     FORGOT PASSWORD
    // -------------------------

    fun setForgotPasswordEmail(email: String) {
        _forgotPasswordState.update { it.copy(email = email) }
    }

    fun sendResetPasswordEmail(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _forgotPasswordState.update { it.copy(isLoading = true) }

            val email = _forgotPasswordState.value.email
            if (email.isEmpty()) {
                onError("Alamat email tidak boleh kosong.")
                _forgotPasswordState.update { it.copy(isLoading = false) }
                return@launch
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _forgotPasswordState.update { it.copy(isLoading = false) }

                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(task.exception?.localizedMessage ?: "Gagal mengirim permintaan reset password.")
                    }
                }
        }
    }


    // -------------------------
    //     GOOGLE SIGN-IN (AUTO-LOGIN LOGIC)
    // ------------------------
    fun handleGoogleSignIn(selectedEmail: String, onSuccess: (Boolean) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            delay(500)

            val isRegistered = availableGoogleAccounts.value.contains(selectedEmail)

            if (isRegistered) {
                _loginState.update {
                    it.copy(
                        email = selectedEmail,
                        password = "VALID_GGL_TOKEN", // Placeholder token
                        errorMessage = null
                    )
                }
                rememberGoogleAccount(selectedEmail)
                onSuccess(true)
            } else {
                onError("Akun Google belum terdaftar. Silakan lengkapi formulir pendaftaran.")
                onSuccess(false)
            }
        }
    }


    // -------------------------
    //   REMEMBER GOOGLE ACCOUNT (PERBAIKAN LOGIC)
    // -------------------------

    fun rememberGoogleAccount(email: String) {
        // PERBAIKAN KRITIS: Simpan SEMUA akun unik
        _availableGoogleAccounts.update { currentList ->
            if (!currentList.contains(email)) {
                currentList + email
            } else {
                currentList
            }
        }
    }


    // --- FUNGSI LAIN ---
    fun logout() {
        auth.signOut()
        _authState.value = AuthState(isSessionChecked = true)
    }

    fun clearError() {
        _authState.update { it.copy(errorMessage = null) }
    }
}