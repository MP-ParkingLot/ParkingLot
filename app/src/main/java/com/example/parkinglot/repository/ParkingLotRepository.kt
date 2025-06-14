// app/src/main/java/com/example/parkinglot/repository/ParkingLotRepository.kt
package com.example.parkinglot.repository

// ... (existing imports, no change here)
import com.example.parkinglot.BuildConfig
import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.*
import com.example.parkinglot.service.KakaoLocalRetrofitClient     // KakaoLocalApiService 래퍼
import com.example.parkinglot.service.ParkingLotRetrofitClient
import retrofit2.Response

/**
 * 주차장 관련 데이터를 다양한 데이터 소스(사용자 정의 백엔드, 카카오 로컬 API)로부터 가져오는 레포지토리 클래스입니다.
 * ViewModel과 같은 상위 계층에서는 이 레포지토리를 통해 주차장 데이터를 요청하며,
 * 실제 데이터 소스(네트워크 통신)의 세부 구현은 이 클래스 내부에 캡슐화되어 있습니다.
 */
class ParkingLotRepository {

    /* ───────────────────────────────────────────────
       "A 방법" : 주차장 상태 + 카카오 카테고리 좌표
       ─────────────────────────────────────────────── */
    data class NearbyParkingLotsResult(
        val details : List<ParkingLotDetail>,                  // 실시간 상태
        val coordMap: Map<String, Pair<Double, Double>>        // addr / place → (lat,lon)
    )

    /** 0) Retrofit 서비스 핸들들 */
    private val api              = ParkingLotRetrofitClient.parkingLotApiService
    private val local            = KakaoLocalRetrofitClient.service          // KakaoLocalApiService
    private val kakaoAddrService = KakaoLocalRetrofitClient.addressSearchService


    /* ───────────────────────────────────────────────
       1.  좌표 → 구 이름 (coord2address)
       ─────────────────────────────────────────────── */
    suspend fun getDistrictByCoords(
        latitude : Double,
        longitude: Double
    ): String? {
        val auth = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
        val resp = local.coord2address(
            apiKey    = auth,
            longitude = longitude,
            latitude  = latitude
        )
        if (!resp.isSuccessful) return null

        val document = resp.body()?.documents?.firstOrNull() ?: return null

        // 1순위: 도로명 주소의 region2depthName에서 "구" 이름 직접 추출
        document.roadAddressDetail?.region2depthName?.let { region2 ->
            if (region2.endsWith("구")) return region2
        }

        // 2순위: 지번 주소의 region2depthName에서 "구" 이름 직접 추출
        document.addressDetail?.region2depthName?.let { region2 ->
            if (region2.endsWith("구")) return region2
        }

        // 3순위: 전체 주소명에서 정규식으로 "구" 추출
        val addressName = document.roadAddressDetail?.addressName
            ?: document.addressDetail?.addressName
            ?: document.addressName
            ?: return null

        return Regex("""\s(\S+구)\s""").find(addressName)
            ?.groupValues
            ?.getOrNull(1)
    }

    /*  편의 alias – ViewModel 에서 바로 호출할 수 있게 두 개 이름 유지 */
    suspend fun coord2district(lat: Double, lon: Double): String? =
        getDistrictByCoords(lat, lon)


    /* ───────────────────────────────────────────────
       2.  "구" 이름으로 상태 + 좌표 세트 얻기
       ─────────────────────────────────────────────── */
    suspend fun getDistrictParkingLotsWithCoords(
        district: String
    ): NearbyParkingLotsResult {

        android.util.Log.d("ParkingRepo", "=== 구별 주차장 조회 시작: $district ===")

        /* 2-1) 백엔드에서 상태 */
        val stateResp = api.getRegionParkingLots(district)
        if (!stateResp.isSuccessful) {
            val err = stateResp.errorBody()?.string()
            android.util.Log.e("ParkingRepo", "백엔드 구별 주차장 조회 실패($district): ${stateResp.code()} $err")
            throw RuntimeException("백엔드 구별 주차장 조회 실패($district): ${stateResp.code()} $err")
        }
        val details = stateResp.body()?.parkingLot ?: emptyList()
        android.util.Log.d("ParkingRepo", "백엔드에서 가져온 주차장 개수: ${details.size}")
        details.take(3).forEach { lot ->
            //android.util.Log.d("ParkingRepo", "주차장 예시: ${lot.id} - 현재: ${lot.currentParkingNum}/${lot.totalParkingNum}")
        }

        /* 2-2) 카카오 주소검색 → 좌표 매핑 */
        val coordMap = mutableMapOf<String, Pair<Double, Double>>()
        val auth     = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"

        android.util.Log.d("ParkingRepo", "카카오 주소검색 시작...")
        var successCount = 0
        var failCount = 0

        details.forEach { lot ->
            val docs = kakaoAddrService
                .searchAddress(apiKey = auth, query = lot.id)
                .body()
                ?.documents
            val best = docs?.firstOrNull()

            if (best != null) {
                val lat = best.y.toDoubleOrNull() ?: 0.0
                val lon = best.x.toDoubleOrNull() ?: 0.0
                if (lat != 0.0 && lon != 0.0) {
                    coordMap[lot.id] = lat to lon
                    successCount++
                    android.util.Log.v("ParkingRepo", "좌표 성공: ${lot.id} -> ($lat, $lon)")
                } else {
                    failCount++
                    android.util.Log.w("ParkingRepo", "좌표 파싱 실패: ${lot.id} -> lat=$lat, lon=$lon")
                }
            } else {
                failCount++
                android.util.Log.w("ParkingRepo", "주소 검색 실패: ${lot.id}")
            }
        }

        android.util.Log.d("ParkingRepo", "좌표 매핑 완료 - 성공: $successCount, 실패: $failCount")
        android.util.Log.d("ParkingRepo", "=== 구별 주차장 조회 완료 ===")

        return NearbyParkingLotsResult(details, coordMap)
    }


    /* ───────────────────────────────────────────────
       3.  현재 좌표 기준 전체(반경) 주차장 상태 + 좌표
       ─────────────────────────────────────────────── */
    suspend fun getNearByParkingLotsWithCoords(
        latitude : Double,
        longitude: Double,
        radius   : Int = 2000
    ): NearbyParkingLotsResult {

        /* 3-1) 카카오 PK6 카테고리 검색 → 좌표 */
        val auth = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
        val kakaoResp = local
            .searchPlacesByCategory(
                apiKey            = auth,
                categoryGroupCode = "PK6",
                longitude         = longitude,
                latitude          = latitude,
                radius            = radius
            )
            .body()
            ?: throw RuntimeException("카카오 카테고리 검색 응답이 null입니다.")

        val coordMap = kakaoResp.documents.associate { doc ->
            val key = doc.roadAddressName.takeIf { !it.isNullOrBlank() } ?: doc.placeName
            key to (doc.y.toDouble() to doc.x.toDouble())
        }

        /* 3-2) 백엔드에서 상태 */
        val req  = NearByParkinglotRequest(coordMap.keys.toList())
        val resp = api.getParkingLots(req)
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string()
            throw RuntimeException("백엔드 전체 주차장 상태 조회 실패: ${resp.code()} $err")
        }

        return NearbyParkingLotsResult(resp.body()?.parkingLot ?: emptyList(), coordMap)
    }


    /* ───────────────────────────────────────────────
       ▼▼▼ 아래 기존 메서드들은 타입/순서 오류만 수정 ▼▼▼
       ─────────────────────────────────────────────── */

    suspend fun getNearbyKakaoParkingLots(
        latitude : Double,
        longitude: Double,
        radius   : Int = 2000
    ): List<Document> {
        val auth = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
        val kakaoResponse = local
            .searchPlacesByCategory(
                apiKey            = auth,
                categoryGroupCode = "PK6",
                longitude         = longitude,
                latitude          = latitude,
                radius            = radius
            )
            .body()
            ?: throw RuntimeException("카카오 로컬 카테고리 검색 응답 본문이 null입니다.")
        return kakaoResponse.documents
    }

    suspend fun searchParkingLotsByAddress(query: String): List<Document> {
        val auth = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
        val kakaoResponse = kakaoAddrService
            .searchAddress(apiKey = auth, query = query)
            .body()
            ?: throw RuntimeException("카카오 주소 검색 응답 본문이 null입니다.")
        return kakaoResponse.documents
    }

    /**
     * 특정 '구' 주차장 상세 상태 조회
     */
    suspend fun getParkingLotsByDistrict(district: String): List<ParkingLotDetail> {
        val resp: Response<RegionParkinglotResponse> = api.getRegionParkingLots(district)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 구별 주차장 조회 실패 ($district): ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }

    /**
     * 주변 '빈 자리' 주차장 상태 조회
     */
    suspend fun getEmptyParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<ParkingLotDetail> {
        val kakaoDocs = getNearbyKakaoParkingLots(latitude, longitude, radius)
        val parkingLotAddresses = kakaoDocs.map { doc ->
            doc.roadAddressName.takeIf { it?.isNotBlank() == true } ?: doc.placeName
        }

        val req = NearByParkinglotRequest(parkingLot = parkingLotAddresses)
        val resp: Response<EmptyParkinglotResponse> = api.getEmptyParkingLots(req)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 빈 자리 주차장 조회 실패: ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }

    /**
     * 주변 '무료' 주차장 상태 조회
     */
    suspend fun getFreeParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<ParkingLotDetail> {
        val kakaoDocs = getNearbyKakaoParkingLots(latitude, longitude, radius)
        val parkingLotAddresses = kakaoDocs.map { doc ->
            doc.roadAddressName.takeIf { it?.isNotBlank() == true } ?: doc.placeName
        }

        val req = NearByParkinglotRequest(parkingLot = parkingLotAddresses)
        val resp: Response<FreeParkinglotResponse> = api.getFreeParkingLots(req)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 무료 주차장 조회 실패: ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }
}