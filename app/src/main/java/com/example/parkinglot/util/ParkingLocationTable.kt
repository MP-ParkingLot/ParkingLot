// app/src/main/java/com/example/parkinglot/util/ParkingLocationTable.kt

package com.example.parkinglot.util

object ParkingLocationTable {

    val idToName : Map<String,String> = mapOf(
        "종로구 세종로 80-1" to "세종로 공영주차장(시)",
        "종로구 훈정동 2-0" to "종묘주차장 공영주차장(시)",
        "중구 을지로5가 40-3" to "훈련원공원 공영주차장(시)",
        "용산구 한남동 728-27" to "한강진역 공영주차장(시)",
        "용산구 한강로2가 12-9" to "용산주차빌딩 공영주차장(시)",
        "중랑구 신내동 647-0" to "봉화산역(남) 공영주차장(시)",
        "강북구 우이동 105-2" to "우이동 공영주차장(시)",
        "마포구 마포동 36-1" to "마포유수지(시)",
        "양천구 신정동 943-25" to "웃우물 공영주차장(시)",
        "양천구 목동 908-26" to "목동체비지 공영주차장(시)",
        "강서구 방화동 829-1" to "방화역(서) 공영주차장(시)",
        "강서구 방화동 830-4" to "방화역(동) 공영주차장(시)",
        "강서구 방화동 845-0" to "개화산역 공영주차장(시)",
        "강서구 가양동 1488-12" to "가양3동 공영주차장(시)",
        "강서구 가양동 1480-10" to "가양역 공영주차장(시)",
        "강남구 일원동 716-2" to "일원역 공영주차장(시)",
        "서초구 방배동 507-1" to "사당노외 공영주차장(시)",
        "강남구 수서동 735-0" to "수서역북 공영주차장(시)",
        "송파구 신천동 14-0" to "신천유수지 공영주차장(시)",
        "송파구 장지동 600-2" to "복정역 공영주차장(시)",
        "중구 필동2가 83-1" to "남산한옥마을 공영주차장(시)",
        "도봉구 도봉동 282-26" to "도봉산 공영주차장(시)",
        "강동구 암사동 473-3" to "볕우물 공영주차장(시)",
        "강동구 천호동 455-0" to "천호역 공영주차장(시)",
        "양천구 목동 905-32" to "목동노외 공영주차장(시)",
        "구로구 구로동 810-3" to "구로디지털단지역 공영주차장(시)",
        "강남구 개포동 1266-0" to "논현로22길(시)",
        "은평구 진관동 66-30" to "구파발역 공영주차장(시)",
        "관악구 신림동 1467-3" to "봉천복개3 공영주차장(시)",
        "금천구 독산동 1081-4" to "독산동 금천교 공영주차장(시)",
        "종로구 관수동 91-4" to "청계2(북2) 공영주차장(시)",
        "종로구 예지동 140-1" to "청계3(북1) 공영주차장(시)",
        "구로구 구로동 414-13" to "가마산고가밑 공영주차장(시)",
        "중구 주교동 51-3" to "청계3(남) 공영주차장(시)",
        "중구 신당동 217-91 0" to "청계6(청평화) 공영주차장(시)",
        "중구 남대문로4가 24-1" to "남대문 화물 공영주차장(시)",
        "동대문구 신설동 114-29" to "신설동 공영주차장(시)",
        "영등포구 당산동3가 370-3" to "영등포구청역 공영주차장(시)",
        "송파구 신천동 27-0" to "잠실역 공영주차장(시)",
        "동대문구 장안동 286-9" to "장안2동 공영주차장(시)",
        "용산구 용산동2가 1-1497" to "해방촌노외 공영주차장(시)",
        "영등포구 당산동3가 2-1" to "당산노외 공영주차장(시)",
        "강동구 명일동 47-9" to "명일파출소 공영주차장(시)",
        "용산구 용산동6가 69-11" to "동작대교 공영주차장(시)",
        "강서구 개화동 664-0" to "개화역 공영주차장(시)",
        "관악구 신림동 498-0" to "신대방역 공영주차장(시)",
        "관악구 신림동 1677-5" to "구로디지털단지역(상) 공영주차장(시)",
        "강남구 대치동 514-1" to "학여울 공영주차장(시)",
        "강남구 일원동 722-1" to "일원터널 공영주차장(시)",
        "도봉구 창동 330-0" to "창동역(서) 공영주차장(시)",
        "노원구 월계동 50-9" to "석계역 공영주차장(시)",
        "구로구 구로동 94-1" to "동구로 공영주차장(시)",
        "종로구 적선동 140-0" to "적선동 주차장(시)",
        "영등포구 신길동 812-0" to "해군본부앞 공영주차장(시)",
        "금천구 가산동 691-3" to "가산동 금천교 공영주차장(시)",
        "영등포구 당산동5가 9-15" to "당산고가밑 공영주차장(시)",
        "중구 을지로3가 282-8" to "을지로 공영주차장(시)",
        "중구 남대문로5가 450-0" to "남산공원 소월로 관광버스전용 주차장(시)",
        "종로구 종로2가 38-4" to "탑골공원 관광버스전용 주차장(시)",
        "구로구 구로동 120-3" to "대림노외 공영주차장(시)",
        "도봉구 도봉동 288-19" to "도봉산역 공영주차장(시)",
        "중구 신당동 251-7" to "동대문 공영주차장(시)",
        "종로구 서린동 63-0" to "서울글로벌센터 공영주차장(시)",
        "구로구 천왕동 280-11" to "천왕역 공영주차장(시)",
        "중구 신당동 222-3" to "DDP북측 마장로 관광버스전용 주차장(시)",
        "중구 삼각동 111-1" to "동국제강 공영주차장(시)",
        "중구 을지로7가 2-36" to "DDP동측(양쪽) 관광버스전용 주차장(시)",
        "중구 남창동 282-10" to "남대문 초입 관광버스전용 주차장(시)",
        "중구 남창동 51-4" to "남대문 시장 관광버스전용 주차장(시)",
        "영등포구 영등포동4가 123-0" to "영남 공영주차장(시)",
        "종로구 창신동 436-87" to "청계7가 공영주차장(시)",
        "종로구 인의동 107-1" to "동순라길(종묘) 관광버스전용 주차장(시)",
        "종로구 관철동 13-14" to "청계1(북) 공영주차장(시)",
        "종로구 종로5가 458-2" to "청계5(북1) 공영주차장(시)",
        "종로구 종로5가 458-2" to "청계5(동호로_북) 공영주차장(시)",
        "종로구 예지동 140-1" to "청계3(창경궁로_북1) 공영주차장(시)",
        "종로구 관수동 91-4" to "청계2(돈화문로) 공영주차장(시)",
        "종로구 사직동 240-0" to "사직로3 관광버스 주차허용 구간(시)",
        "종로구 청운동 15-57" to "창의문로 관광버스 주차허용 구간(시)",
        "종로구 내자동 35-0" to "사직로1 관광버스 주차허용 구간(시)",
        "종로구 관철동 13-14" to "관철동 공영주차장(시)",
        "종로구 종로6가 289-49" to "청계6(북) 공영주차장(시)",
        "종로구 관수동 91-4" to "청계2(북1) 공영주차장(시)",
        "종로구 와룡동 2-86" to "창경궁로1 관광버스 주차허용 구간(시)",
        "종로구 내자동 131-0" to "사직로2 관광버스 주차허용 구간(시)",
        "노원구 상계동 111-568" to "당고개위 공영주차장(시)",
        "송파구 신천동 11-1" to "성내역(잠실나루역) 공영주차장(시)",
        "동작구 동작동 316-3" to "동작대교(위) 공영주차장(시)",
        "강남구 신사동 668-0" to "압구정고가1 공영주차장(시)",
        "강남구 압구정동 435-0" to "동호대교(남) 공영주차장(시)",
        "강남구 신사동 668-0" to "압구정고가2 공영주차장(시)",
        "구로구 구로동 73-9" to "대림역2 공영주차장(시)",
        "구로구 구로동 69-5" to "제방도로 공영주차장(시)",
        "강서구 공항동 796-0" to "방화로 공영주차장(시)",
        "중구 방산동 4-47 0" to "청계5(동호로_남) 공영주차장(시)",
        "중구 주교동 123-1 0" to "청계3(창경궁로_남) 공영주차장(시)",
        "마포구 성산동 117-1" to "성산로 관광버스 주차허용 구간(시)",
        "중구 을지로6가 17-442 0" to "청계5(남) 공영주차장(시)",
        "중구 을지로6가 17-442 0" to "청계5가 공영주차장(시)",
        "중구 신당동 217-91 0" to "청계6(신평화시장앞) 공영주차장(시)",
        "중구 신당동 217-91 0" to "청계6(동평화) 공영주차장(시)",
        "중구 방산동 52-1" to "청계5(방산동) 공영주차장(시)",
        "중구 남대문로3가 13-3 0" to "남대문시장3번게이트 이륜차 주차장(시)",
        "중구 남대문로5가 541-0 0" to "서울스퀘어빌딩앞 이륜차 전용주차장(시)",
        "중구 남대문로4가 3-6 0" to "남대문시장2번게이트앞 이륜차 주차장(시)",
        "중구 회현동1가 1-19" to "소파길노상 공영주차장(시)",
        "중구 인현동1가 87-10" to "마른내길 공영주차장(시)",
        "중구 회현동1가 1-16" to "남산파출소 공영주차장(시)",
        "중구 남창동 205-107" to "남산공원 소파로 관광버스전용 주차장(시)",
        "중구 흥인동 162-1 0" to "청계8가 공영주차장(시)",
        "중구 방산동 4-47 0" to "청계3(동호로) 공영주차장(시)",
        "중구 회현동1가 1-15" to "소파로케이블카앞 관광버스전용 주차장(시)",
        "성동구 상왕십리동 12-47" to "청계8가(남) 공영주차장(시)",
        "영등포구 문래동3가 54-12" to "문래1동 공영주차장(시)",
        "영등포구 영등포동1가 111-1" to "영등포로터리 공영주차장(시)",
        "용산구 한남동 9-19" to "장충단로(한남광장) 관광버스전용 주차장(시)",
        "영등포구 양평동5가 44-1" to "양평동(동) 공영주차장(시)",
        "영등포구 여의도동 8-1" to "여의서로 공영주차장(시)",
        "영등포구 대림동 808-24" to "대림역 공영주차장(시)",
        "금천구 시흥동 139-2" to "남부여성발전센터 공영주차장(시)",
        "강서구 마곡동 739-0" to "신방화역 공영주차장(시)",
        "은평구 역촌동 41-8" to "진흥로 공영주차장(시)",
        "구로구 온수동 51-26" to "온수역(남) 공영주차장(시)",
        "성동구 옥수동 401-1" to "옥수역 공영주차장(시)",
        "중구 을지로3가 296-24" to "돈화문로 공영주차장(시)",
        "강북구 미아동 458-7" to "도봉로 15길 공영주차장(시)",
        "영등포구 여의도동 5-0" to "원효대교 남단 공영주차장(시)",
        "강서구 가양동 1457-1" to "가양라이품 공영주차장(시)",
        "중구 오장동 69-3" to "마른내로 공영주차장(시)",
        "종로구 와룡동 2-86" to "창경궁로2 관광버스 주차허용 구간(시)",
        "중구 을지로7가 112-3" to "APM플레이스 앞 관광버스 주차허용 구간(시)",
        "중구 태평로2가 113-1" to "세종대로3 관광버스 승하차 허용 구간(시)",
        "마포구 합정동 5-12" to "토정로 관광버스 주차허용 구간(시)",
        "서대문구 남가좌동 318-11" to "모래내로 관광버스 주차허용 구간(시)",
        "동대문구 신설동 109-2" to "청계8가(북) 공영주차장(시)",
        "동작구 신대방동 431-3" to "보라매상업 공영주차장(시)",
        "중구 태평로1가 60-22" to "세종대로1 관광버스 승하차 허용 구간(시)",
        "중구 정동 5-5" to "세종대로2 관광버스 승하차 허용 구간(시)",
        "강남구 대치동 512-1" to "영동6교밑 공영주차장(시)",
        "중구 예관동 22-1" to "배오개길 공영주차장(시)",
        "양천구 신월동 411-1" to "신월4동 공영주차장(시)",
        "용산구 문배동 12-1" to "삼각지고가밑 공영주차장(시)",
        "중구 예장동 8-172" to "남산소파길 공영주차장(시)",
        "중구 예장동 4-1" to "남산예장 관광버스전용 주차장(시)",
        "동대문구 장안동 392-3" to "장안1동 공영주차장(시)",
        "중랑구 면목동 168-2" to "면목유수지 공영주차장(시)",
        "중구 을지로5가 40-2" to "훈련원공원 앞 관광버스 전용 주차장(시)",
        "중구 신당동 204-92" to "DDP 유어스빌딩 앞 관광버스 주차허용 구간(시)",
        "용산구 이촌동 173-1" to "성촌공원 남측 관광버스 주차허용 구간(시)",
        "마포구 상암동 1695-0" to "성암로 관광버스 주차허용 구간(시)",
        "용산구 이태원동 56-4" to "이태원 입구 관광버스 승하차 허용 구간(시)",
        "중구 충무로1가 52-47" to "신세계면세점 명동점 앞 관광버스 승하차 허용 구간(시)",
        "종로구 궁정동 15-0" to "효자로 관광버스 주차허용 구간(시)",
        "영등포구 여의도동 2-11" to "여의도공원 공영주차장(시)",
        "성동구 성수동2가 273-15" to "유료주차장(시)",
        "강남구 일원동 722-1" to "일원터널(시)",
        "성동구 마장동 459-6" to "마장동 공영주차장(시)",
        "마포구 상암동 481-94 " to "난지중앙로노상공영주차장(시)",
        "양천구 목동 905-32" to "목동노외 공영주차장(시)",
        "강서구 방화동 824-0" to "방화우체국(시)",
        "광진구 화양동 63-2" to "화양동공영주차장(시)",
        "서초구 반포동 115-5" to "반포한강공원 반포2주차장(시)",
        "종로구 명륜3가 53-0" to "성균관 유림회관(시)",
        "서초구 반포동 118-3" to "반포천 공영주차장(파미에)(시)",
        "중구 중림동 155-1" to "중림종합복지센터 부설주차장(구_중림(제5))(시)",
        "중구 회현동1가 1-16" to "남산파출소공영주차장(시)",
        "용산구 한강로2가 2-39" to "용산구 한강로2가 2-39(시)",
        "강남구 신사동 625-13" to "삼성유료주차장(시)",
        "강동구 암사동 139-2" to "서울암사동유적 주차장(시)",
        "강서구 방화동 845-0" to "test(시)",
        "광진구 중곡동 230-12" to "중곡1동 공영주차장(시)",
        "구로구 구로동 454-1" to "구로2동 소공원 공영주차장(시)",
        "성북구 성북동 237-3" to "성북동길 공영주차장(시)",
        "강남구 개포동 1-1" to "강남구 개포동 1-1(시)"
    )

    // 주소에서 "구" 정보를 추출하여 매핑합니다.
    val idToDistrict: Map<String, String> = run {
        val mutableMap = mutableMapOf<String, String>()
        val districtRegex = Regex("서울 (\\S+구)") // "서울 XX구" 형태의 구 추출
        val seoulDistricts = listOf(
            "종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구",
            "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구",
            "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구",
            "서초구", "강남구", "송파구", "강동구"
        )

//        idToLatLng.keys.forEach { address ->
//            var foundDistrict: String? = null
//            // 주소 문자열에서 직접 "XX구"를 찾습니다.
//            for (district in seoulDistricts) {
//                if (address.contains(district)) {
//                    foundDistrict = district
//                    break
//                }
//            }
//            // 만약 주소에 명시적으로 구가 없으면,
//            // 예외적으로 몇몇 주소를 기반으로 추론하거나, 필요하다면 API를 사용하여 구를 찾아야 합니다.
//            // 현재 주어진 데이터에서는 대부분 구 이름이 명확하게 포함되어 있습니다.
//
//            mutableMap[address] = foundDistrict ?: "알 수 없음" // 구를 찾지 못할 경우 "알 수 없음"으로 표시
//        }
        mutableMap.toMap()
    }
}