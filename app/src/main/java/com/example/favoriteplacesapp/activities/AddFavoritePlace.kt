package com.example.favoriteplacesapp.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.favoriteplacesapp.databinding.ActivityAddFavoritePlaceBinding
import com.karumi.dexter.Dexter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.favoriteplacesapp.R
import com.example.favoriteplacesapp.database.DatabaseHandler
import com.example.favoriteplacesapp.models.FavoritePlaceModel
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddFavoritePlace : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var binding: ActivityAddFavoritePlaceBinding
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode == Activity.RESULT_OK){

            val contentURI = result.data?.data
            try{
                val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                binding.imageViewDetail.setImageBitmap(selectedImageBitmap)
                saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)

            }catch(e: IOException){
                e.printStackTrace()
            }

        }
    }

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude: Double = 0.0

    private var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode == Activity.RESULT_OK){

            val thumbnail : Bitmap = result.data!!.extras!!.get("data") as Bitmap

            saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
            binding.imageViewDetail.setImageBitmap(thumbnail)



        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFavoritePlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddPlace)

        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.toolbarAddPlace.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            updateDateInView()
        }
        updateDateInView()
        binding.etDate.setOnClickListener(this@AddFavoritePlace)

        binding.tvAddImage.setOnClickListener(this)

        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.et_date -> {
                DatePickerDialog(this@AddFavoritePlace, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery",
                "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which->
                    when(which){
                        0 ->choosePhotoFromGallery()
                        1 ->takePhotoFromCamera()
                    }

                }
                pictureDialog.show()
            }
            R.id.btn_save ->{
                when{
                    binding.etTitle.text.isNullOrEmpty()->
                        Toast.makeText(this, "Please, enter a title", Toast.LENGTH_LONG).show()
                    binding.etDescription.text.isNullOrEmpty()->
                        Toast.makeText(this, "Please, enter a description", Toast.LENGTH_LONG).show()
                    binding.etLocation.text.isNullOrEmpty()->
                        Toast.makeText(this, "Please, enter a location", Toast.LENGTH_LONG).show()
                    saveImageToInternalStorage == null ->{
                        Toast.makeText(this, "Please, select an image", Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        val favoritePlaceModel = FavoritePlaceModel(
                            0,
                            binding.etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding.etDescription.text.toString(),
                            binding.etDate.text.toString(),
                            binding.etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val dbHandler = DatabaseHandler(this)
                        val addFavoritePlace = dbHandler.addHappyPlace(favoritePlaceModel)

                        if(addFavoritePlace > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }


            }

        }

    }

    private fun takePhotoFromCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            Toast.makeText(this@AddFavoritePlace, "Now you can select image from Gallery",Toast.LENGTH_LONG).show()
                        }
                        if(report.isAnyPermissionPermanentlyDenied){

                            for( i in report.grantedPermissionResponses)
                                Log.d("Hello", "onPermissionsChecked: inside let "+i.permissionName)

                            for(i in report.deniedPermissionResponses)
                                Log.d("Hello", "onPermissionsChecked: denied" + i.permissionName)
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            resultLauncherCamera.launch(cameraIntent)

                        }


                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationDialogForPermissions()
                }

            }).check()
        }

    }

    private fun choosePhotoFromGallery() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            Toast.makeText(this@AddFavoritePlace, "Now you can select image from Gallery",Toast.LENGTH_LONG).show()
                        }
                        if(report.isAnyPermissionPermanentlyDenied){

                            for( i in report.grantedPermissionResponses)
                                Log.d("Hello", "onPermissionsChecked: inside let "+i.permissionName)

                            for(i in report.deniedPermissionResponses)
                                Log.d("Hello", "onPermissionsChecked: denied" + i.permissionName)
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            resultLauncher.launch(galleryIntent)

                        }


                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationDialogForPermissions()
                }

            }).check()
        }

    }

    private fun showRationDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions required"+
        " for this feature. It can be enabled under the Applications Settings")
            .setPositiveButton("GO TO SETTINGS"){
                _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }


            }.setNegativeButton("Cancel"){
                dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy."
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText( sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object{
        private const val GALLERY = 1
        private const val IMAGE_DIRECTORY = "FavoritePlacesImage"
    }
}