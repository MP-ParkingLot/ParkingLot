//app/src/main/java/com/example/parkinglot/domain/model/kakao/Document.kt

package com.example.parkinglot.domain.model.kakao

import com.google.gson.annotations.SerializedName

data class Document(
    // 전체 지번 주소 (주소 객체 내의 address_name과 다를 수 있음)
    @SerializedName("address_name")
    val addressName: String? = null, // 실제 JSON에 따라 null 가능성 추가
    @SerializedName("category_group_code")
    val categoryGroupCode: String? = null,
    @SerializedName("category_group_name")
    val categoryGroupName: String? = null,
    @SerializedName("category_name")
    val categoryName: String? = null,
    val distance: String? = null,
    val id: String,
    val phone: String? = null,

    // 장소명,업체명
    @SerializedName("place_name")
    val placeName: String,

    // 장소 상세 페이지 URL
    @SerializedName("place_url")
    val placeUrl: String,

    // 전체 도로명 주소 (도로명 주소 객체 내의 address_name과 다를 수 있음)
    @SerializedName("road_address_name")
    val roadAddressName: String? = null, // 실제 JSON에 따라 null 가능성 추가

    @SerializedName("x") // 경도
    val x: String,
    @SerializedName("y") // 위도
    val y: String,

    // ★★★ 추가: 중첩된 'address' 객체 파싱을 위한 필드
    @SerializedName("address")
    val addressDetail: AddressDetail? = null, // 필드 이름 'address' 대신 'addressDetail' 사용 권장 (Kotlin 키워드 충돌 방지)

    // ★★★ 추가: 중첩된 'road_address' 객체 파싱을 위한 필드
    @SerializedName("road_address")
    val roadAddressDetail: RoadAddressDetail? = null // 필드 이름 'roadAddress' 대신 'roadAddressDetail' 사용 권장
)

// ★★★ 추가: 지번 주소 상세 정보를 위한 데이터 클래스
data class AddressDetail(
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("region_1depth_name")
    val region1depthName: String,
    @SerializedName("region_2depth_name")
    val region2depthName: String, // "강남구"와 같은 구 이름이 여기에 있을 가능성 높음
    @SerializedName("region_3depth_name")
    val region3depthName: String,
    @SerializedName("h_code")
    val hCode: String,
    @SerializedName("b_code")
    val bCode: String,
    @SerializedName("mountain_yn")
    val mountainYn: String,
    @SerializedName("main_address_no")
    val mainAddressNo: String,
    @SerializedName("sub_address_no")
    val subAddressNo: String? = null,
    @SerializedName("zip_code")
    val zipCode: String? = null
)

// ★★★ 추가: 도로명 주소 상세 정보를 위한 데이터 클래스
data class RoadAddressDetail(
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("region_1depth_name")
    val region1depthName: String,
    @SerializedName("region_2depth_name")
    val region2depthName: String, // "강남구"와 같은 구 이름이 여기에 있을 가능성 높음
    @SerializedName("region_3depth_name")
    val region3depthName: String,
    @SerializedName("road_name")
    val roadName: String,
    @SerializedName("underground_yn")
    val undergroundYn: String,
    @SerializedName("main_building_no")
    val mainBuildingNo: String,
    @SerializedName("sub_building_no")
    val subBuildingNo: String? = null,
    @SerializedName("building_name")
    val buildingName: String,
    @SerializedName("zone_no")
    val zoneNo: String,
    @SerializedName("x")
    val x: String,
    @SerializedName("y")
    val y: String
)