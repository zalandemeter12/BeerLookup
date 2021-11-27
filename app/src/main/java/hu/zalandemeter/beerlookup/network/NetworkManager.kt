package hu.zalandemeter.beerlookup.network

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.punkapi.com/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val beerService: BeerService = retrofit.create(BeerService::class.java)

    fun getBeers(name: String?, abv: Double?, ibu: Double?, page: Int?): Call<List<BeerData>?>? {
        return beerService.getBeers(name, abv, ibu, page)
    }

    fun getBeer(id: Int): Call<List<BeerData>?>? {
        return beerService.getBeer(id)
    }
}