//  com/example/parkinglot/service/SeoulParkingService.kt
package com.example.parkinglot.service

import com.example.parkinglot.dto.response.NearByParkinglotResponse
import retrofit2.Response                      // ★ 서버 HTTP 코드·헤더를 보기 위해
import retrofit2.http.GET
import retrofit2.http.Path

interface SeoulParkingService {

    @GET("{apiKey}/xml/GetParkingInfo/{start}/{end}/")
    suspend fun getParkingLots(               // ★ ViewModel·Repo 에서 그대로 부릅니다
        @Path("apiKey") apiKey: String,
        @Path("start")  start : Int,
        @Path("end")    end   : Int
    ): Response<NearByParkinglotResponse>     // ★ 반드시 Response<> 로 감쌈
}
