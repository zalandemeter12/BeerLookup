package hu.zalandemeter.beerlookup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.zalandemeter.beerlookup.databinding.ActivityBeerBinding
import hu.zalandemeter.beerlookup.network.BeerData
import hu.zalandemeter.beerlookup.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BeerActivity : AppCompatActivity(), BeerAdapter.OnBeerClickListener {

    private lateinit var binding: ActivityBeerBinding
    var adapter: BeerAdapter = BeerAdapter(this)
    private var beers: MutableList<BeerData> = mutableListOf()
    var currentPage = 1

    var searchName: String? = null
    var searchAbv: Double? = null
    var searchIbu: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        httpRequest(null, null, null, currentPage)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val pos =  (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val numItems: Int = binding.recyclerView.adapter?.itemCount ?: 0
                    if (pos >= numItems - 1) {
                        currentPage++
                        try {
                            httpRequest(searchName, searchAbv, searchIbu, currentPage)
                        } catch (e: Exception) {
                            Log.d("BeerLookup", "BOTTOM OVERSCROLL EXCEPTION")
                        }
                    }
                }
            }
        })

        binding.floatingActionButton.setOnClickListener {
            //open search dialog window
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            val dialog = SearchDialog(this)
            dialog.show()
            dialog.window?.setLayout(6 * width / 7, 2 * height / 5)
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    fun httpRequest(name :String?, abv: Double?, ibu: Double?, page: Int?) {
        binding.progressBar.visibility = View.VISIBLE
        NetworkManager.getBeers(name, abv, ibu, page)?.enqueue(object : Callback<List<BeerData>?> {
            override fun onResponse(
                call: Call<List<BeerData>?>,
                response: Response<List<BeerData>?>
            ) {
                Log.d("BEER", "onResponse: " + response.code())
                if (response.isSuccessful) {
                    adapter.addBeer(response.body()!!)
                    binding.progressBar.visibility = View.GONE
                    Log.d("BeerAdapter", "addBeers: ${beers.size}")
                } else {
                    Toast.makeText(this@BeerActivity, "Error: " + response.message(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                call: Call<List<BeerData>?>,
                throwable: Throwable
            ) {
                throwable.printStackTrace()
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@BeerActivity, "Network request error occured!", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBeerSelected(beer: BeerData) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@BeerActivity, BeerDetailsActivity::class.java)
        showDetailsIntent.putExtra(BeerDetailsActivity.EXTRA_ID, beer.id)
        startActivity(showDetailsIntent)
    }
}
