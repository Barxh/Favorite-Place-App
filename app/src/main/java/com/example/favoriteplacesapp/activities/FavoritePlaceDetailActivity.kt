package com.example.favoriteplacesapp.activities

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.favoriteplacesapp.R
import com.example.favoriteplacesapp.databinding.ActivityFavoritePlaceDetailBinding
import com.example.favoriteplacesapp.models.FavoritePlaceModel

class FavoritePlaceDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityFavoritePlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritePlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetail)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.toolbarDetail.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }
        var model: FavoritePlaceModel? = null
        if(intent.hasExtra("model")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                model = intent.getSerializableExtra("model", FavoritePlaceModel::class.java)!!
            }else {
                model = intent.getSerializableExtra("model") as FavoritePlaceModel
            }
        }
        supportActionBar?.title = model?.title
        binding.imageViewDetail.setImageURI(Uri.parse(model?.image))
        binding.textViewDescription.text = model?.description
        binding.textViewLocation.text = model?.location
    }
}