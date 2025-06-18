local.defaults.properties
secret.properties
위 이름으로 파일 두 개 만들어주시고,
두 파일 모두
KAKAO_MAP_KEY=YourAppKey
이런 형식으로 값 넣어주세요.
Key는 카카오 디벨로퍼스에서
프로젝트 만들면 받을 수 있습니다.


<< 앱 패키지 구성 >>

📦 app
 ┣ 📂 data
 ┃ ┣ 📂 network
 ┃ ┃ ┣ 📂 auth (인증 API)
 ┃ ┃ ┃ ┣ 📜 AuthClientProvider.kt (인증 클라이언트 설정, 토큰 관리)
 ┃ ┃ ┃ ┗ 📜 AuthService.kt (로그인/회원가입 API 인터페이스)
 ┃ ┃ ┣ 📂 converter (JSON 변환 어댑터)
 ┃ ┃ ┃ ┣ 📜 ArrayWrappingConverterFactory.kt (배열 래핑)
 ┃ ┃ ┃ ┗ 📜 WrappingTypeAdapterFactory.kt (JSON 루트 배열 래핑)
 ┃ ┃ ┣ 📂 core
 ┃ ┃ ┃ ┗ 📜 RetrofitClient.kt (메인 백엔드 Retrofit 클라이언트)
 ┃ ┃ ┣ 📂 kakao (카카오 로컬 API)
 ┃ ┃ ┃ ┣ 📜 KakaoAddressSearchService.kt (카카오 주소 검색 API)
 ┃ ┃ ┃ ┣ 📜 KakaoLocalApiService.kt (카카오 로컬 API 인터페이스)
 ┃ ┃ ┃ ┗ 📜 KakaoLocalRetrofitClient.kt (카카오 로컬 Retrofit 클라이언트)
 ┃ ┃ ┣ 📂 parking (주차장 API)
 ┃ ┃ ┃ ┣ 📜 ParkingLotApiService.kt (주차장 API 인터페이스)
 ┃ ┃ ┃ ┗ 📜 ParkingLotRetrofitClient.kt (주차장 Retrofit 클라이언트)
 ┃ ┃ ┗ 📜 ApiService.kt (리뷰 API 인터페이스)
 ┃ ┗ 📂 repository
 ┃ ┃ ┣ 📂 auth
 ┃ ┃ ┃ ┗ 📜 AuthManager.kt (인증 상태/토큰 관리)
 ┃ ┃ ┣ 📂 kakao
 ┃ ┃ ┃ ┗ 📜 KakaoLocalRepository.kt (카카오 로컬 데이터 처리)
 ┃ ┃ ┣ 📂 parking
 ┃ ┃ ┃ ┗ 📜 ParkingLotRepository.kt (주차장 데이터 통합 관리)
 ┃ ┃ ┗ 📂 review
 ┃ ┃ ┃ ┗ 📜 ReviewRepository.kt (리뷰 데이터 관리)
 ┣ 📂 domain
 ┃ ┗ 📂 model
 ┃ ┃ ┣ 📂 kakao
 ┃ ┃ ┃ ┣ 📜 Document.kt (카카오 응답 문서 DTO)
 ┃ ┃ ┃ ┗ 📜 KakaoLocalResponse.kt (카카오 로컬 응답 DTO)
 ┃ ┃ ┣ 📂 parking
 ┃ ┃ ┃ ┣ 📜 CombinedParkingLotInfo.kt (결합된 주차장 정보 DTO)
 ┃ ┃ ┃ ┣ 📜 ParkingLotDetailDto.kt (주차장 상세 DTO)
 ┃ ┃ ┃ ┗ 📂 request / response (주차장 요청/응답 DTOs)
 ┃ ┃ ┃ ┃ ┣ 📜 NearByParkinglotRequest.kt
 ┃ ┃ ┃ ┃ ┣ 📜 EmptyParkinglotResponse.kt
 ┃ ┃ ┃ ┃ ┣ 📜 FreeParkinglotResponse.kt
 ┃ ┃ ┃ ┃ ┣ 📜 NearByParkinglotResponse.kt
 ┃ ┃ ┃ ┃ ┗ 📜 RegionParkinglotResponse.kt
 ┃ ┃ ┗ 📂 ui
 ┃ ┃ ┃ ┗ 📜 ParkingLotUiState.kt (주차장 UI 상태)
 ┣ 📂 review (리뷰 관련 데이터 및 화면)
 ┃ ┣ 📜 ReviewData.kt (리뷰 DTO)
 ┃ ┗ 📜 ReviewFormScreens.kt (리뷰 작성/수정 UI)
 ┣ 📂 ui
 ┃ ┣ 📂 activity
 ┃ ┃ ┗ 📜 MainActivity.kt (메인 활동)
 ┃ ┣ 📂 component
 ┃ ┃ ┣ 📂 map
 ┃ ┃ ┃ ┣ 📜 KakaoMapScreen.kt (카카오 지도 화면)
 ┃ ┃ ┃ ┗ 📜 MapMarkerManager.kt (지도 마커 관리)
 ┃ ┃ ┣ 📜 DistrictDropdownMenu.kt (구 드롭다운 메뉴)
 ┃ ┃ ┗ 📜 VerticalFilterButtons.kt (수직 필터 버튼)
 ┃ ┣ 📂 dialog
 ┃ ┃ ┗ 📜 DistanceRadiusDialog.kt (거리 반경 선택 다이얼로그)
 ┃ ┣ 📂 navigation
 ┃ ┃ ┗ 📜 AppNavigation.kt (앱 내비게이션 그래프)
 ┃ ┗ 📂 screen
 ┃ ┃ ┣ 📂 auth
 ┃ ┃ ┃ ┗ 📜 LoginScreen.kt (로그인 화면)
 ┃ ┃ ┣ 📂 main
 ┃ ┃ ┃ ┗ 📜 MainScreen.kt (메인 주차장 지도 화면)
 ┃ ┃ ┗ 📂 review
 ┃ ┃ ┃ ┗ 📜 ReviewScreen.kt (리뷰 목록 화면)
 ┣ 📂 util
 ┃ ┣ 📜 Constants.kt (상수 정의)
 ┃ ┗ 📜 Extensions.kt (확장 함수)
 ┗ 📂 viewmodel (뷰모델)
   ┣ 📂 factory
   ┃ ┣ 📜 ParkingViewModelFactory.kt
   ┃ ┗ 📜 ReviewViewModelFactory.kt
   ┣ 📜 ParkingViewModel.kt
   ┗ 📜 ReviewViewModel.kt
