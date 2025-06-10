package com.example.parkinglot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val reviewRepository = remember { ReviewRepository { AuthManager.accessToken } }
    val currentUser by AuthManager.currentUser.collectAsState()

    // TODO (로그인 담당자): 실제 로그인 화면이 완성되면 이 LaunchedEffect는 삭제되어야 합니다.
    // 현재는 테스트를 위해 앱 시작 시 가상 로그인을 수행합니다.
    LaunchedEffect(Unit) {
        AuthManager.onLoginSuccess("FAKE_ACCESS_TOKEN_FOR_TESTING", UserInfo(1L, "정우준"))
    }

    NavHost(
        navController = navController,
        startDestination = "review_list/건국대학교 서울캠퍼스주차장"
    ) {
        composable(
            route = "review_list/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: "알 수 없는 장소"
            val viewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(reviewRepository, currentUser))

            ReviewScreen(
                viewModel = viewModel,
                locationId = locationId,
                onNavigateBack = { /* TODO: 지도 화면으로 돌아가는 로직 */ },
                onNavigateToWriteReview = { navController.navigate("write_review") },
                onNavigateToUpdateReview = { review ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("review_to_update", review)
                    navController.navigate("update_review")
                }
            )
        }

        composable(route = "write_review") {
            val parentEntry = remember(it) { navController.getBackStackEntry("review_list/{locationId}") }
            val viewModel: ReviewViewModel = viewModel(viewModelStoreOwner = parentEntry)
            WriteReviewScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }

        composable(route = "update_review") {
            val parentEntry = remember(it) { navController.getBackStackEntry("review_list/{locationId}") }
            val viewModel: ReviewViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val reviewToUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<Review>("review_to_update")
            if (reviewToUpdate != null) {
                UpdateDeleteScreen(reviewToUpdate, viewModel, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
