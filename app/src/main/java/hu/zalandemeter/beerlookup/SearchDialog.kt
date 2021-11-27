package hu.zalandemeter.beerlookup

import android.app.Dialog
import android.os.Bundle
import hu.zalandemeter.beerlookup.databinding.DialogSearchBinding

class SearchDialog(beerActivity: BeerActivity) : Dialog(beerActivity) {

    private lateinit var binding: DialogSearchBinding
    private val activity = beerActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.search_title)

        binding.button.setOnClickListener {
            if (binding.etName.text .isNotEmpty()) {
                activity.searchName = binding.etName.text.toString()
            } else {
                activity.searchName = null
            }
            if (binding.etAbv.text.isNotEmpty()) {
                activity.searchAbv = binding.etAbv.text.toString().toDouble()
            } else {
                activity.searchAbv = null
            }
            if(binding.etIbu.text.isNotEmpty()) {
                activity.searchIbu = binding.etIbu.text.toString().toDouble()
            } else {
                activity.searchIbu = null
            }
            activity.adapter.clear()
            activity.currentPage = 1
            activity.httpRequest(activity.searchName, activity.searchAbv, activity.searchIbu, activity.currentPage)
            dismiss()
        }
    }
}