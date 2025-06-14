// app/src/main/java/com/example/parkinglot/AppNavigation.kt
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
import com.example.parkinglot.repository.ParkingLotRepository
import com.example.parkinglot.ui.component.MainScreen
import com.example.parkinglot.viewmodel.ParkingViewModel
import com.example.parkinglot.viewmodel.ParkingViewModelFactory
import android.net.Uri

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    /* 리뷰 관련 의존성 (예: Retrofit) */
    val reviewRepository = remember { ReviewRepository { AuthManager.accessToken } }
    val currentUser      by AuthManager.currentUser.collectAsState()

    NavHost(navController, startDestination = "map") {

        /** ───────── 지도 화면 ───────── */
        composable("map") {
            /* ParkingViewModel 을 그래프-스코프 내에서 생성 */
            val parkingVm: ParkingViewModel = viewModel(
                factory = ParkingViewModelFactory(ParkingLotRepository())
            )

            MainScreen(
                viewModel = parkingVm,
                onNavigateToReview = { rawId ->
                    /* 공백·한글 등이 포함된 경로 파라미터 인코딩 */
                    val encoded = Uri.encode(rawId)
                    navController.navigate("review_list/$encoded")
                }
            )
        }

        /** ───────── 리뷰 목록 ───────── */
        composable(
            "review_list/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.StringType })
        ) { backStack ->
            val locationId = backStack.arguments?.getString("locationId") ?: ""
            val reviewVm: ReviewViewModel = viewModel(
                factory = ReviewViewModelFactory(reviewRepository, currentUser)
            )

            ReviewScreen(
                viewModel              = reviewVm,
                locationId            = locationId,
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToWriteReview = { navController.navigate("write_review") },
                onNavigateToUpdateReview = { review ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("review_to_update", review)
                    navController.navigate("update_review")
                }
            )
        }

        /** ───────── 리뷰 작성 ───────── */
        composable("write_review") {
            val parent = remember(it) {
                navController.getBackStackEntry("review_list/{locationId}")
            }
            val reviewVm: ReviewViewModel = viewModel(viewModelStoreOwner = parent)
            WriteReviewScreen(
                viewModel      = reviewVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        /** ───────── 리뷰 수정/삭제 ───────── */
        composable("update_review") {
            val parent = remember(it) {
                navController.getBackStackEntry("review_list/{locationId}")
            }
            val reviewVm: ReviewViewModel = viewModel(viewModelStoreOwner = parent)
            val reviewToUpdate = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Review>("review_to_update")
            reviewToUpdate?.let {
                UpdateDeleteScreen(
                    review          = it,
                    viewModel       = reviewVm,
                    onNavigateBack  = { navController.popBackStack() }
                )
            }
        }
    }
}
