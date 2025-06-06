//  com/example/parkinglot/repository/ParkingDetailRepository.kt
package com.example.parkinglot.repository

import android.util.Log
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.service.RetrofitClient          // ← 이미 만들어둔 싱글턴
import com.example.parkinglot.service.SeoulParkingService
import retrofit2.Response
import com.example.parkinglot.dto.response.NearByParkinglotResponse

class ParkingRepository(
    /** DI 를 쓰지 않는다면 RetrofitClient 로부터 직접 주입 */
    private val service: SeoulParkingService = RetrofitClient.seoulParkingService
) {

    private val TAG = "ParkingDetailRepo"

    /**
     * start/end 구간의 주차장 상세 목록을 받아 반환
     */
    suspend fun getParkingLotDetails(
        apiKey: String,
        startIndex: Int,
        endIndex: Int
    ): List<ParkingLotDetail> {

        /* ① HTTP 요청 */
        val httpRes: Response<NearByParkinglotResponse> =
            service.getParkingLots(apiKey, startIndex, endIndex)

        /* ② 성공 여부 확인 */
        if (!httpRes.isSuccessful) {
            Log.e(TAG, "❌ Seoul API HTTP ${httpRes.code()}")
            throw RuntimeException("Seoul API HTTP ${httpRes.code()}")
        }

        /* ③ body → rows 추출 (널-세이프) */
        val rows = httpRes.body()?.rows ?: emptyList()
        Log.d(TAG, "✅ rows=${rows.size}")
        return rows
    }
}
