package com.example.parkinglot

import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
//        composable(route = "map") {
//            MapScreen(//지도 담당자가 만든 composable 함수의 이름으로 변경해야 합니다,
//                // 현재는 composable로 선언한 지도가 없고, loacationid와 연동이 되지 않아서 빨간색으로 표시됩니다
//                onNavigateToReview = { locationId ->
//                    navController.navigate("review_list/$locationId")
//                }
//            )
//        }

        composable(
            route = "review_list/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: "알 수 없는 장소"
            val viewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(reviewRepository, currentUser))

            ReviewScreen(
                viewModel = viewModel,
                locationId = locationId,
                onNavigateBack = { navController.popBackStack() },
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
