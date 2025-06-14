// app/src/main/java/com/example/parkinglot/repository/ParkingLotRepository.kt
package com.example.parkinglot.repository

// ... (existing imports, no change here)
import android.R.attr.y
import com.example.parkinglot.BuildConfig
import com.example.parkinglot.dto.request.NearByParkinglotRequest
import com.example.parkinglot.dto.response.Document // Already correct
import com.example.parkinglot.dto.response.KakaoLocalResponse // Already correct

import com.example.parkinglot.dto.response.EmptyParkinglotResponse
import com.example.parkinglot.dto.response.FreeParkinglotResponse
import com.example.parkinglot.dto.response.NearByParkinglotResponse
import com.example.parkinglot.dto.response.ParkingLotDetail
import com.example.parkinglot.dto.response.RegionParkinglotResponse
import com.example.parkinglot.service.KakaoLocalRetrofitClient // This is likely still being used if KakaoLocalRepository is not fully integrated
import com.example.parkinglot.service.ParkingLotRetrofitClient
import retrofit2.Response
import kotlin.collections.map

/**
 * 주차장 관련 데이터를 다양한 데이터 소스(사용자 정의 백엔드, 카카오 로컬 API)로부터 가져오는 레포지토리 클래스입니다.
 * ViewModel과 같은 상위 계층에서는 이 레포지토리를 통해 주차장 데이터를 요청하며,
 * 실제 데이터 소스(네트워크 통신)의 세부 구현은 이 클래스 내부에 캡슐화되어 있습니다.
 */
class ParkingLotRepository {

    // 사용자 정의 백엔드 API 서비스 인스턴스
    private val api = ParkingLotRetrofitClient.parkingLotApiService
    // 카카오 로컬 API 중 카테고리 검색을 위한 서비스 인스턴스 (PK6: 주차장)
    // IMPORTANT: If you are using KakaoLocalRepository, this 'local' instance should probably be from KakaoLocalRepository.
    // For now, I'm assuming 'local' still refers to KakaoLocalRetrofitClient.service (which returns Response<KakaoLocalResponse>)
    // If you implemented KakaoLocalRepository as a separate repository, you should inject it here instead of direct Retrofit client.
    private val local = KakaoLocalRetrofitClient.service // This refers to KakaoLocalApiService, which returns Response<KakaoLocalResponse>

    // Assuming you have a KakaoLocalRepository instance as well, if you're using it
    // private val kakaoLocalRepository = KakaoLocalRepository()


    // 카카오 로컬 API 중 주소 검색을 위한 서비스 인스턴스 (지오코딩/역지오코딩)
    private val kakaoAddressService = KakaoLocalRetrofitClient.addressSearchService


    /**
     * 카카오 로컬 API를 사용하여 특정 좌표 및 반경 내의 주차장 장소 정보를 검색합니다.
     * 이 함수는 [com.example.parkinglot.network.KakaoLocalApiService.searchPlacesByCategory]를 호출합니다.
     *
     * @param latitude 검색 중심 위치의 위도.
     * @param longitude 검색 중심 위치의 경도.
     * @param radius 검색 반경 (미터 단위, 기본값 2000m).
     * @return 카카오 로컬 API 응답에서 파싱된 [Document] 객체들의 리스트를 반환합니다.
     * 각 Document는 장소 이름, 도로명 주소, 위도, 경도 등의 정보를 포함합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
     */
    suspend fun getNearbyKakaoParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<Document> {
        // 카카오 REST API 키를 BuildConfig에서 안전하게 가져와 헤더로 전달
        val authHeader = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}" // KAKAO_REST_KEY 사용 (수정)

        // *** FIX START ***
        // The return type of local.searchPlacesByCategory is Response<KakaoLocalResponse>
        // So, we need to access .body() first.
        val kakaoResponse: KakaoLocalResponse? = local.searchPlacesByCategory(
            apiKey = authHeader,
            categoryGroupCode = "PK6", // 주차장 카테고리 코드 명시
            longitude = longitude, // Double 타입 유지
            latitude = latitude,   // Double 타입 유지
            radius = radius
        ).body() // <-- Call .body() here
        // *** FIX END ***


        // Check if the response body is null, if so, return empty list
        if (kakaoResponse == null) {
            throw RuntimeException("카카오 로컬 카테고리 검색 응답 본문이 null입니다.")
        }
        return kakaoResponse.documents ?: emptyList() // Now 'documents' is resolved on kakaoResponse
    }

    /**
     * 카카오 로컬 API를 사용하여 주소 문자열을 기반으로 주소 정보를 검색합니다 (지오코딩).
     * 이 함수는 백엔드에서 주차장 ID(주소/이름)만 주고 좌표는 주지 않는 경우,
     * 해당 주소의 좌표 및 상세 정보를 가져오는 데 사용됩니다.
     * 이 함수는 [com.example.parkinglot.network.KakaoLocalApiService.searchAddress]를 호출합니다.
     *
     * @param query 검색할 주소 문자열 (예: "서울 강남구 테헤란로 132").
     * @return 검색된 주소 정보 [Document] 객체들의 리스트를 반환합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
     */
    suspend fun searchParkingLotsByAddress(query: String): List<Document> {
        // 카카오 REST API 키를 BuildConfig에서 안전하게 가져와 헤더로 전달
        val authHeader = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}" // KAKAO_REST_KEY 사용 (수정)
        // *** FIX START ***
        val kakaoResponse: KakaoLocalResponse? = kakaoAddressService.searchAddress(
            apiKey = authHeader,
            query = query
        ).body() // <-- Call .body() here
        // *** FIX END ***

        if (kakaoResponse == null) {
            throw RuntimeException("카카오 주소 검색 응답 본문이 null입니다.")
        }
        return kakaoResponse.documents ?: emptyList()
    }


    /**
     * 카카오 로컬 API를 통해 얻은 주차장 장소 정보 (도로명 주소 또는 장소 이름)를 기반으로,
     * 사용자 정의 백엔드에서 해당 주차장들의 상세 상태 정보를 조회합니다.
     * 이 함수는 [com.example.parkinglot.network.ParkingLotApiService.getParkingLots]를 호출합니다.
     *
     * @param latitude 검색 중심 위치의 위도. (카카오 로컬 검색을 위한 파라미터)
     * @param longitude 검색 중심 위치의 경도. (카카오 로컬 검색을 위한 파라미터)
     * @param radius 검색 반경 (미터 단위, 기본값 2000m). (카카오 로컬 검색을 위한 파라미터)
     * @return [ParkingLotDetail] 객체들의 리스트를 반환합니다. 각 객체는 주차장의 상세 정보와 현재 상태를 포함합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
     */
    suspend fun getNearByParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<ParkingLotDetail> {
        // 1단계: 카카오 로컬 API를 통해 주변 주차장 장소 정보 (Document)를 가져옵니다.
        val kakaoDocs = getNearbyKakaoParkingLots(latitude, longitude, radius) // This calls the fixed function

        // 2단계: 가져온 카카오 문서에서 주차장 ID로 사용할 값을 추출합니다.
        // roadAddressName이 있으면 그것을 사용하고, 없으면 placeName을 사용합니다.
        val parkingLotAddresses = kakaoDocs.map { doc -> doc.roadAddressName.takeIf { it?.isNotBlank() == true } ?: doc.placeName }

        // 3단계: NearByParkinglotRequest DTO는 parkingLot 리스트만 받으므로, 이렇게 생성합니다.
        val req = NearByParkinglotRequest(parkingLot = parkingLotAddresses)
        val resp: Response<NearByParkinglotResponse> = api.getParkingLots(req)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 전체 주차장 상태 조회 실패: ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }

    /**
     * 특정 구 이름에 해당하는 주차장 정보를 사용자 정의 백엔드에서 조회합니다.
     * 이 함수는 [com.example.parkinglot.network.ParkingLotApiService.getRegionParkingLots]를 호출합니다.
     *
     * @param district 조회할 구의 이름 (예: "강남구").
     * @return [ParkingLotDetail] 객체들의 리스트를 반환합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
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
     * 카카오 로컬 API를 통해 얻은 주차장 장소 정보를 기반으로,
     * 사용자 정의 백엔드에서 '빈 자리'가 있는 주차장들의 상세 상태 정보를 조회합니다.
     * 이 함수는 [com.example.parkinglot.network.ParkingLotApiService.getEmptyParkingLots]를 호출합니다.
     *
     * @param latitude 검색 중심 위치의 위도.
     * @param longitude 검색 중심 위치의 경도.
     * @param radius 검색 반경 (미터 단위, 기본값 2000m).
     * @return '빈 자리'가 있는 [ParkingLotDetail] 객체들의 리스트를 반환합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
     */
    suspend fun getEmptyParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<ParkingLotDetail> {
        // 1단계: 카카오 로컬 API를 통해 주변 주차장 장소 정보 (Document)를 가져옵니다.
        val kakaoDocs = getNearbyKakaoParkingLots(latitude, longitude, radius) // This calls the fixed function

        // 2단계: 가져온 카카오 문서에서 주차장 ID로 사용할 값을 추출합니다.
        val parkingLotAddresses = kakaoDocs.map { doc -> doc.roadAddressName.takeIf { it?.isNotBlank() == true } ?: doc.placeName }

        // 3단계: NearByParkinglotRequest DTO는 parkingLot 리스트만 받으므로, 이렇게 생성합니다.
        val req = NearByParkinglotRequest(parkingLot = parkingLotAddresses)
        val resp: Response<EmptyParkinglotResponse> = api.getEmptyParkingLots(req)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 빈 자리 주차장 조회 실패: ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }

    /**
     * 카카오 로컬 API를 통해 얻은 주차장 장소 정보를 기반으로,
     * 사용자 정의 백엔드에서 '무료' 주차장들의 상세 상태 정보를 조회합니다.
     * 이 함수는 [com.example.parkinglot.network.ParkingLotApiService.getFreeParkingLots]를 호출합니다.
     *
     * @param latitude 검색 중심 위치의 위도.
     * @param longitude 검색 중심 위치의 경도.
     * @param radius 검색 반경 (미터 단위, 기본값 2000m).
     * @return '무료' 주차장 [ParkingLotDetail] 객체들의 리스트를 반환합니다.
     * @throws RuntimeException 네트워크 요청 실패 시 발생합니다.
     */
    suspend fun getFreeParkingLots(
        latitude: Double,
        longitude: Double,
        radius: Int = 2000
    ): List<ParkingLotDetail> {
        // 1단계: 카카오 로컬 API를 통해 주변 주차장 장소 정보 (Document)를 가져옵니다.
        val kakaoDocs = getNearbyKakaoParkingLots(latitude, longitude, radius) // This calls the fixed function

        // 2단계: 가져온 카카오 문서에서 주차장 ID로 사용할 값을 추출합니다.
        val parkingLotAddresses = kakaoDocs.map { doc -> doc.roadAddressName.takeIf { it?.isNotBlank() == true } ?: doc.placeName }

        // 3단계: NearByParkinglotRequest DTO는 parkingLot 리스트만 받으므로, 이렇게 생성합니다.
        val req = NearByParkinglotRequest(parkingLot = parkingLotAddresses)
        val resp: Response<FreeParkinglotResponse> = api.getFreeParkingLots(req)
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw RuntimeException("백엔드 무료 주차장 조회 실패: ${resp.code()} ${resp.message()} - ${errorBody}")
        }
        return resp.body()?.parkingLot ?: emptyList()
    }
}