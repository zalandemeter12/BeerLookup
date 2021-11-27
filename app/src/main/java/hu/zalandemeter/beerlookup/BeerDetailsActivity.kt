package hu.zalandemeter.beerlookup

import android.os.Bundle
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.zalandemeter.beerlookup.databinding.ActivityDetailsBinding
import hu.zalandemeter.beerlookup.network.BeerData
import hu.zalandemeter.beerlookup.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeerDetailsActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "extra.id"
    }

    private lateinit var binding: ActivityDetailsBinding
    private var beer: BeerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            binding.progressBar.visibility = View.VISIBLE
            NetworkManager.getBeer(id)?.enqueue(object :
                Callback<List<BeerData>?> {
                override fun onResponse(
                    call: Call<List<BeerData>?>,
                    response: Response<List<BeerData>?>
                ) {
                    Log.d("BEERdetails", "onResponse: " + response.code())
                    if (response.isSuccessful) {
                        beer = response.body()!![0]
                        binding.progressBar.visibility = View.GONE

                        binding.tvBeerName.text = beer?.name

                        if (beer!!.image_url != null) {
                            Glide.with(this@BeerDetailsActivity)
                                .load(beer!!.image_url)
                                .transition(DrawableTransitionOptions().crossFade())
                                .into(binding.ivBeerImage)
                        } else  {
                            binding.ivBeerImage.setImageResource(R.drawable.beer_placeholder)
                        }

                        binding.tvBeerDescription.text = beer!!.description
                        binding.tvTagline.text = beer!!.tagline
                        binding.tvBeerDescription.justificationMode = JUSTIFICATION_MODE_INTER_WORD

                    } else {
                        Toast.makeText(this@BeerDetailsActivity, "Error: " + response.message(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<BeerData>?>,
                    throwable: Throwable
                ) {
                    throwable.printStackTrace()
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@BeerDetailsActivity, "Network request error occured!", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}