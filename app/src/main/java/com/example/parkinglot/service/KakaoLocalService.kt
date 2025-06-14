////app/src/main/java/com/example/parkinglot/service/KakaoLocalService.kt
//
//package com.example.parkinglot.service
//
//import com.google.gson.annotations.SerializedName
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Query
//
//interface KakaoLocalService {
//
//    /** 1-1. 카테고리 검색 (주차장: PK6) */
//    @GET("v2/local/search/category.json")
//    suspend fun searchParkingLots(
//        @Query("category_group_code") category: String = "PK6",
//        @Query("x") longitude: Double,
//        @Query("y") latitude:  Double,
//        @Query("radius") radius:     Int,
//        @Header("Authorization") apiKey: String  // "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"
//    ): Response<CategorySearchResponse>
//
//    /** 1-2. 좌표→주소 변환 (필요시) */
//    @GET("v2/local/geo/coord2address.json")
//    suspend fun coord2address(
//        @Query("x") longitude: Double,
//        @Query("y") latitude:  Double,
//        @Header("Authorization") apiKey: String
//    ): Response<Coord2AddressResponse>
//}
//
///** search-by-category 응답 DTO */
//data class CategorySearchResponse(
//    val documents: List<Document>
//) {
//    data class Document(
//        @SerializedName("id")                  val id:             String?,  // 내부 POI ID
//        @SerializedName("place_name")          val placeName:      String,
//        @SerializedName("road_address_name")   val roadAddress:    String,
//        @SerializedName("x")                   val longitudeStr:   String,
//        @SerializedName("y")                   val latitudeStr:    String
//    ) {
//        val longitude: Double get() = longitudeStr.toDoubleOrNull() ?: 0.0
//        val latitude:  Double get() = latitudeStr.toDoubleOrNull()  ?: 0.0
//    }
//}
//
///** coord2address 응답 DTO */
//data class Coord2AddressResponse(
//    val documents: List<AddressDocument>
//) {
//    data class AddressDocument(
//        val address: Address
//    ) {
//        data class Address(
//            val address_name: String
//        )
//    }
//}
