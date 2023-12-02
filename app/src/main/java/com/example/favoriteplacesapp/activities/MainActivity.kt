package com.example.favoriteplacesapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.favoriteplacesapp.adapters.FavoritePlaceAdapter
import com.example.favoriteplacesapp.database.DatabaseHandler
import com.example.favoriteplacesapp.databinding.ActivityMainBinding
import com.example.favoriteplacesapp.models.FavoritePlaceModel

class MainActivity : AppCompatActivity() {


    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){

            getFavoritePlacesList()
        }else{
            Log.e("ActivityResult", "Cancelled or Back pressed" )
        }
    }
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatingButtonAdd.setOnClickListener {
            resultLauncher.launch(Intent(this, AddFavoritePlace::class.java))
        }
        getFavoritePlacesList()
    }

    private fun getFavoritePlacesList(){
        val dbHandler = DatabaseHandler(this)
        val favoritePlaceList : ArrayList<FavoritePlaceModel> = dbHandler.getFavoritePlacesList()

        if(favoritePlaceList.size > 0){
            for(i in favoritePlaceList){

                binding.recycleViewFp.visibility = View.VISIBLE
                binding.textViewNoRecords.visibility = View.GONE
                setupFavoritePlacesRecycleView(favoritePlaceList)

            }
        }else{
            binding.recycleViewFp.visibility = View.GONE
            binding.textViewNoRecords.visibility = View.VISIBLE

        }
    }

    private fun setupFavoritePlacesRecycleView(favoritePlaceList : ArrayList<FavoritePlaceModel>){
        val placeAdapter = FavoritePlaceAdapter(favoritePlaceList)
        binding.recycleViewFp.layoutManager = LinearLayoutManager(this)
        binding.recycleViewFp.adapter = placeAdapter

        placeAdapter.setOnClickListener(object : FavoritePlaceAdapter.OnClickListener{
            override fun onClick(position: Int, model: FavoritePlaceModel) {
                val intent = Intent(this@MainActivity, FavoritePlaceDetailActivity::class.java)
                intent.putExtra("model",model)
                startActivity(intent)

            }

        })


    }

    
}