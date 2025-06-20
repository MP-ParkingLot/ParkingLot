//app/src/main/java/com/example/parkinglot/dto/network/parking/ParkingLotApiService.kt

package com.example.parkinglot.data.network.parking

import com.example.parkinglot.domain.model.parking.request.NearByParkinglotRequest
import com.example.parkinglot.domain.model.parking.response.EmptyParkinglotResponse
import com.example.parkinglot.domain.model.parking.response.FreeParkinglotResponse
import com.example.parkinglot.domain.model.parking.response.NearByParkinglotResponse
import com.example.parkinglot.domain.model.parking.response.RegionParkinglotResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ParkingLotApiService {
    @POST("/parkinglot")
    suspend fun getParkingLots(
        @Body req: NearByParkinglotRequest
    ): Response<NearByParkinglotResponse>

    @POST("/parkinglot/free")
    suspend fun getFreeParkingLots(
        @Body req: NearByParkinglotRequest
    ): Response<FreeParkinglotResponse>

    @POST("/parkinglot/empty")
    suspend fun getEmptyParkingLots(
        @Body req: NearByParkinglotRequest
    ): Response<EmptyParkinglotResponse>

    @GET("/parkinglot")
    suspend fun getRegionParkingLots(
        @Query("district") district: String
    ): Response<RegionParkinglotResponse>
}