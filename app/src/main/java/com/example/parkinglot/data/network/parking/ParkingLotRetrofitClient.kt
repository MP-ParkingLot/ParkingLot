//app/src/main/java/com/example/parkinglot/dto/network/parking/ParkingLotRetrofitClient.kt

package com.example.parkinglot.data.network.parking

import com.example.parkinglot.domain.model.parking.response.EmptyParkinglotResponse
import com.example.parkinglot.domain.model.parking.response.FreeParkinglotResponse
import com.example.parkinglot.domain.model.parking.response.NearByParkinglotResponse
import com.example.parkinglot.domain.model.parking.ParkingLotDetail
import com.example.parkinglot.domain.model.parking.response.RegionParkinglotResponse
import com.example.parkinglot.data.network.converter.ArrayWrappingAdapterFactory
import com.example.parkinglot.ui.adapter.ParkingLotDetailAdapter
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ParkingLotRetrofitClient {
    private const val BASE_URL = "http://52.78.160.90"

    private val gson = GsonBuilder()
        // 1) wrapper DTO 마다 배열 감싸기 어댑터 등록
        .registerTypeAdapterFactory(
            ArrayWrappingAdapterFactory(
                NearByParkinglotResponse::class.java,
                "parkingLot"
            )
        )
        .registerTypeAdapterFactory(
            ArrayWrappingAdapterFactory(
                EmptyParkinglotResponse::class.java,
                "parkingLot"
            )
        )
        .registerTypeAdapterFactory(
            ArrayWrappingAdapterFactory(
                FreeParkinglotResponse::class.java,
                "parkingLot"
            )
        )
        .registerTypeAdapterFactory(
            ArrayWrappingAdapterFactory(
                RegionParkinglotResponse::class.java,
                "parkingLot"
            )
        )
        // 2) ParkingLotDetail DTO 전용 어댑터 등록
        .registerTypeAdapter(
            ParkingLotDetail::class.java,
            ParkingLotDetailAdapter()
        )
        .create()

    val parkingLotApiService: ParkingLotApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ParkingLotApiService::class.java)
    }
}