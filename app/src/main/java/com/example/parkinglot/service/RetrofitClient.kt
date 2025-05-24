import com.example.parkinglot.service.SeoulParkingService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.dev.seoul.go.kr/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val seoulParkingService: SeoulParkingService =
        retrofit.create(SeoulParkingService::class.java)
}
