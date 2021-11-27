package hu.zalandemeter.beerlookup
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.zalandemeter.beerlookup.network.BeerData
import hu.zalandemeter.beerlookup.databinding.BeerItemBinding

class BeerAdapter(private val listener: OnBeerClickListener) : RecyclerView.Adapter<BeerAdapter.BeerViewHolder>() {

    private val beerList = mutableListOf<BeerData>()

    interface OnBeerClickListener {
        fun onBeerSelected(beer: BeerData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val binding = BeerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return beerList.size
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val item = beerList[position]

        if (item.image_url != null) {
            Glide.with(holder.itemView.context)
                .load(item.image_url)
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.itemViewBinding.ivBeerImage)
        } else  {
            holder.itemViewBinding.ivBeerImage.setImageResource(R.drawable.beer_placeholder)
        }

        val splitName = item.name?.split(" ")
        var displayName: String? = splitName?.get(0)
        if(splitName?.size!! >= 2) {
            displayName = "${splitName[0]} ${splitName[1]}"
            if (splitName.size >= 3 && splitName[1] == "-") {
                displayName = "${splitName[0]} ${splitName[1]} ${splitName[2]}"
            }
        }

        holder.itemViewBinding.tvBeerName.text = displayName
        holder.itemViewBinding.tvAbv.text = "ABV:\t" + item.abv.toString()
        holder.itemViewBinding.tvIbu.text = "IBU:\t" + item.ibu.toString()
    }

    fun addBeer(beer: List<BeerData>) {
        val startPosition = beerList.size
        val endPosition = startPosition + beer.size
        beerList.addAll(beer)
        notifyItemRangeInserted(startPosition, endPosition)
    }

    fun clear() {
        val size = beerList.size
        Log.d("BeerAdapter", "clear: $size")
        beerList.clear()
        notifyItemRangeRemoved(0, size)
    }

    inner class BeerViewHolder(val itemViewBinding: BeerItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
        init {
            itemViewBinding.root.setOnClickListener {
                listener.onBeerSelected(beerList[adapterPosition])
            }
        }
    }
}
