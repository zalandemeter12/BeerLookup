package hu.zalandemeter.beerlookup.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BeerService {
    @GET("/v2/beers")
    fun getBeers(
        @Query("beer_name") beerName: String?,
        @Query("abv_gt") abv: Double?,
        @Query("ibu_gt") ibu: Double?,
        @Query("page") page: Int? = 1
    ): Call<List<BeerData>?>?

    @GET("/v2/beers")
    fun getBeer(
        @Query("ids") id: Int?
    ): Call<List<BeerData>?>?
}
