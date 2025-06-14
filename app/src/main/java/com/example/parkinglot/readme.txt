AutoManager.kt에서 로그아웃 부분은 삭제하셔도 됩니다

AppNavigation 코드에는 설명했듯이 map 구현자분께서 작성한 Composable 함수의 이름을 대체 하시면 됩니다
AppNavHost()로 프로그램 실행하시면 됩니다

navController.navigate("review_list/해당_마커의_LocationID")
이 코드를 제대로 작성이 되어있다면 각 장소의 이름이 리뷰 창 윗 부분에 표시가 됩니다

각 장소별 리뷰 저장, 아이디별(USERID로 구분) 작성한 리뷰 저장되게 구성되었습니다

ApiService 서버 통신을 위한 Api 명세서
AppNavigation 앱의 흐름 관리
AuthManager 로그인 상태 관리
RetrofitClient 서버 통신용 클라이언트
ReviewData 리뷰 데이터
ReviewFormScreens 수정 삭제 UI
ReviewRepository 실제 서버 통신 수행
ReviewScreen 리뷰 메인 UI
ReviewViewModel UI와 데이터 로직 연결
ReviewViewModelFactory 리뷰 모델을 생성하는 팩토리

왕복 5시간이 걸려 오프라인으로 참여하지 못한 점 상당히 죄송합니다.
모르는 부분에 있어서 제 스스로 생각하고, 넘어간 것과 서버 연동도 고려하지 않은채 저 혼자 개인 컴퓨터에서 진행한 것 또한 정말 죄송합니다
그 죄송한 만큼 코드 구현 정말 열심히 했으니까 노여움 풀어주시길 바라겠습니다

윤아씨 정말 죄송하고, 제 문제 빨리 잡을 수 있게 도와주셔서 감사힙니다
승현씨 늦은 시간에 전화해도 받아주시고, 혼자 다른 길 걷고 있는데 따뜻하게 격려해줘서 감사합니다.
현승씨 늦은 시간에 연락해도 잘 받아주셔서 감사드리고, 각 장소마다 제가 보내준 코드 붙여넣으느라 힘드셨을텐데 불만 없이 해주셔서 감사합니다