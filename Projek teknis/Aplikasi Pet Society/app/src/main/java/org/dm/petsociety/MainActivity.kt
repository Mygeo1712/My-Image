// File: org.dm.petsociety.MainActivity.kt

package org.dm.petsociety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.dm.petsociety.network.CloudinaryClient
import org.dm.petsociety.ui.theme.PetSocietyTheme
import org.dm.petsociety.viewmodel.AuthViewModel
import org.dm.petsociety.viewmodel.AuthState

// --- IMPORT VIEWMODELS ---
import org.dm.petsociety.viewmodel.ShelterViewModel
import org.dm.petsociety.viewmodel.ExpertViewModel
import org.dm.petsociety.viewmodel.ChatViewModel

// --- IMPORT SCREENS ---
import org.dm.petsociety.ui.screens.ForgotPasswordConfirmationScreen
import org.dm.petsociety.ui.screens.ForgotPasswordInputScreen
import org.dm.petsociety.ui.screens.LandingScreen
import org.dm.petsociety.ui.screens.LoginScreen
import org.dm.petsociety.ui.screens.RegisterScreen

// PET OWNER SCREENS
import org.dm.petsociety.ui.screens.petowner.feed.PetOwnerFeedScreen
import org.dm.petsociety.ui.screens.petowner.feed.CreatePostScreen
import org.dm.petsociety.ui.screens.petowner.feed.PostDetailScreen
import org.dm.petsociety.ui.screens.petowner.search.PetOwnerSearchScreen
import org.dm.petsociety.ui.screens.petowner.search.ExpertListScreen
import org.dm.petsociety.ui.screens.common.ChatListScreen
import org.dm.petsociety.ui.screens.petowner.chat.ChatDetailScreen
import org.dm.petsociety.ui.screens.petowner.profile.PetOwnerProfileScreen
import org.dm.petsociety.ui.screens.petowner.profile.PetListScreen
import org.dm.petsociety.ui.screens.petowner.playdate.PetOwnerPlaydateScreen
import org.dm.petsociety.ui.screens.petowner.adoption.AdoptionListScreen
import org.dm.petsociety.ui.screens.petowner.adoption.AdoptionDetailScreen

// SHELTER SCREENS
import org.dm.petsociety.ui.screens.shelter.ShelterHomeScreen
import org.dm.petsociety.ui.screens.shelter.AddPetScreen
import org.dm.petsociety.ui.screens.shelter.EditShelterProfileScreen
import org.dm.petsociety.ui.screens.shelter.EditPetScreen
import org.dm.petsociety.ui.screens.shelter.ShelterPetDetailScreen

// EXPERT SCREENS
import org.dm.petsociety.ui.screens.expert.ExpertHomeScreen
import org.dm.petsociety.ui.screens.expert.CreateServiceScreen
import org.dm.petsociety.ui.screens.expert.ExpertServiceDetailScreen
import org.dm.petsociety.ui.screens.expert.EditServiceScreen
import org.dm.petsociety.ui.screens.expert.EditExpertProfileScreen


class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val shelterViewModel: ShelterViewModel by viewModels()
    private val expertViewModel: ExpertViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CloudinaryClient.init(applicationContext)

        setContent {
            PetSocietyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PetSocietyAppNavigation(
                        authViewModel = authViewModel,
                        shelterViewModel = shelterViewModel,
                        expertViewModel = expertViewModel,
                        chatViewModel = chatViewModel
                    )
                }
            }
        }
    }
}

// --- FUNGSI ANIMASI ---
fun slideInFromRight(): EnterTransition = slideInHorizontally(tween(300)) { it } + fadeIn(tween(200))
fun slideOutToLeft(): ExitTransition = slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(100))
fun slideOutToRight(): ExitTransition = slideOutHorizontally(tween(300)) { it } + fadeOut(tween(100))
fun slideInFromLeft(): EnterTransition = slideInHorizontally(tween(300)) { -it } + fadeIn(tween(200))

@Composable
fun PetSocietyAppNavigation(
    authViewModel: AuthViewModel,
    shelterViewModel: ShelterViewModel,
    expertViewModel: ExpertViewModel,
    chatViewModel: ChatViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    fun getHomeRoute(role: String): String {
        return when (role) {
            "Pet Owner (Pemilik Hewan)" -> "home_owner_feed"
            "Shelter/Rescuer (Penyelamat)" -> "home_shelter"
            "Expert/Business (Dokter Hewan, Groomer, Pet Shop)" -> "home_expert"
            else -> "home_owner_feed"
        }
    }

    // --- AUTOLOGIN/SESI PERSISTEN LOGIC ---
    val startRoute: String

    if (!authState.isSessionChecked) {
        // Tampilkan loading saat sesi sedang diperiksa
        startRoute = "loading"
    } else {
        if (authState.isAuthenticated && authState.userRole.isNotEmpty()) {
            // Jika sudah terautentikasi dan role sudah dimuat
            startRoute = getHomeRoute(authState.userRole)
        } else {
            // Jika tidak ada sesi atau sesi sudah di-logout
            startRoute = "landing"
        }
    }

    // Tampilkan layar loading sementara saat checkCurrentUser belum selesai
    if (!authState.isSessionChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Blue)
        }
        return
    }
    // --- AKHIR AUTOLOGIN LOGIC ---

    NavHost(navController = navController, startDestination = startRoute) {

        // Rute Loading (Hanya untuk tampilan awal)
        composable("loading") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }

        // ==========================================
        // 1. AUTH & ONBOARDING
        // ==========================================
        composable("landing") {
            LandingScreen(onStartClick = { navController.navigate("login") })
        }

        composable("login",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    val homeRoute = getHomeRoute(role)
                    navController.navigate(homeRoute) { popUpTo("landing") { inclusive = true } }
                },
                onCreateAccountClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotPasswordInput") }
            )
        }

        composable("register",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") { popUpTo("login") { inclusive = true } }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable("forgotPasswordInput") {
            ForgotPasswordInputScreen(
                viewModel = authViewModel,
                onSendSuccess = { navController.navigate("forgotPasswordConfirmation") },
                onDismiss = { navController.popBackStack() }
            )
        }

        composable("forgotPasswordConfirmation") {
            ForgotPasswordConfirmationScreen(onAcceptClick = { navController.popBackStack("login", inclusive = false) })
        }

        // ==========================================
        // 2. PET OWNER ROUTES
        // ==========================================
        composable("home_owner_feed") { PetOwnerFeedScreen(navController, authViewModel) }
        composable("home_owner_search") { PetOwnerSearchScreen(navController) }

        composable("home_owner_chat") {
            ChatListScreen(
                navController = navController,
                viewModel = chatViewModel,
                role = "Pet Owner"
            )
        }

        composable("home_owner_profile") { PetOwnerProfileScreen(navController, authViewModel) }
        composable("pet_list_screen") { PetListScreen(navController, authViewModel) }
        composable("home_owner_playdate") { PetOwnerPlaydateScreen(navController, authViewModel) }
        composable("create_post_screen") { CreatePostScreen(navController) }

        // Post Detail
        composable("post_detail/{postId}", arguments = listOf(navArgument("postId") { type = NavType.StringType })) {
            val postId = it.arguments?.getString("postId")
            if (postId != null) PostDetailScreen(navController, postId, authViewModel)
        }

        // --- CHAT DETAIL (SHARED ROUTE - Digunakan semua role) ---
        composable("chat_detail/{chatId}/{companionName}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }, navArgument("companionName") { type = NavType.StringType })
        ) {
            val chatId = it.arguments?.getString("chatId")
            val name = it.arguments?.getString("companionName")
            if (chatId != null && name != null) {
                ChatDetailScreen(navController, chatId, name, authViewModel)
            }
        }

        // Adoption List (Pet Owner View)
        composable("adoption_list") {
            AdoptionListScreen(navController = navController)
        }

        // Adoption Detail
        composable(
            "adoption_detail/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            if (petId != null) {
                AdoptionDetailScreen(navController, petId)
            }
        }

        // Expert List (Pet Owner View)
        composable("expert_list_petowner") {
            ExpertListScreen(
                navController = navController,
                viewModel = expertViewModel
            )
        }

        // ==========================================
        // 3. SHELTER ROUTES
        // ==========================================

        composable("home_shelter") {
            ShelterHomeScreen(navController = navController, viewModel = shelterViewModel)
        }

        composable("shelter_add_pet",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            AddPetScreen(
                viewModel = shelterViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("shelter_edit_profile") {
            EditShelterProfileScreen(
                navController = navController,
                viewModel = shelterViewModel
            )
        }

        composable(
            "shelter_edit_pet/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            if (petId != null) {
                EditPetScreen(
                    navController = navController,
                    viewModel = shelterViewModel,
                    petId = petId
                )
            }
        }

        composable(
            "shelter_pet_detail/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            if (petId != null) {
                ShelterPetDetailScreen(navController, petId)
            }
        }

        // ==========================================
        // 4. EXPERT ROUTES
        // ==========================================

        composable("home_expert") {
            ExpertHomeScreen(navController, expertViewModel)
        }

        composable(
            "expert_add_service",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) {
            CreateServiceScreen(navController, expertViewModel)
        }

        composable(
            "expert_service_detail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            if (serviceId != null) {
                ExpertServiceDetailScreen(navController, serviceId)
            }
        }

        composable(
            "expert_edit_service/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            if (serviceId != null) {
                EditServiceScreen(
                    navController = navController,
                    viewModel = expertViewModel,
                    serviceId = serviceId
                )
            }
        }

        composable("expert_edit_profile") {
            EditExpertProfileScreen(
                navController = navController,
                viewModel = expertViewModel
            )
        }
    }
}