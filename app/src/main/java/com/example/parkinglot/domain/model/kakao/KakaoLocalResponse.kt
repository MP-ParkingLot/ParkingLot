//app/src/main/java/com/example/parkinglot/domain/model/kakao/KakaoLocalResponse.kt

package com.example.parkinglot.domain.model.kakao

import com.google.gson.annotations.SerializedName

data class KakaoLocalResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("documents")
    val documents: List<Document>
)

data class Meta(
    // 검색된 장소 이름과 일치하는 장소 이름이 여러 개 있는 경우에 대한 정보
    @SerializedName("same_name")
    val sameName: SameName?,

    // 현재 페이지에서 조회 가능한 결과의 수
    @SerializedName("pageable_count")
    val pageableCount: Int,

    // 검색 결과의 전체 총 개수(중복 포함)
    @SerializedName("total_count")
    val totalCount: Int,

    // 현재 검색 결과가 마지막 페이지인지에 대한 여부
    // true이면 더 이상 다음 페이지가 없음을 의미
    @SerializedName("is_end")
    val isEnd: Boolean
)

data class SameName(
    // 지역 리스트
    val region: List<String>,
    @SerializedName("keyword")

    // 키워드
    val keyword: String,
    @SerializedName("selected_region")

    // 인식된 지역 리스트 중 현재 검색에 사용된 지역 정보
    val selectedRegion: String
)