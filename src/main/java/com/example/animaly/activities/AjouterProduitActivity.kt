package com.example.animaly.activities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.animaly.R
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AjouterProduitActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 13
    private val FILE_NAME = "image"
    private val GALLERY_REQUEST_CODE = 14;
    private val PERMISSION_CODE = 1001;

    lateinit var image  : ImageView
    lateinit var filePhoto : File
    lateinit var photoPath : Uri

    var photo_state : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_produit)

        val titre = findViewById<EditText>(R.id.titre_et)
        val prix = findViewById<EditText>(R.id.prix_et)
        val desc = findViewById<EditText>(R.id.desc_et)
         image = findViewById<ImageView>(R.id.add_img_iv)

        image.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.selectionner_image))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.camera)) { dialog, id ->

                    chooseImageCamera();

                }
                .setNegativeButton(getString(R.string.galerie)) { dialog, id ->

                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CODE)
                    } else{
                        chooseImageGallery();
                    }
                }
            val alert = builder.create()
            alert.show()

            val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.BLACK)
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.BLACK)

        })

        val ajouter = findViewById<Button>(R.id.ajouter_btn)
        ajouter.setOnClickListener {

            if (titre.text.toString().isEmpty())
            {
                titre.error = getString(R.string.champ_vide)
                return@setOnClickListener

            }

            if (prix.text.toString().isEmpty())
            {
                prix.error = getString(R.string.champ_vide)
                return@setOnClickListener

            }


            if (desc.text.toString().isEmpty())
            {
                desc.error = getString(R.string.champ_vide)
                return@setOnClickListener

            }

            if (!photo_state)
               {
                   Toast.makeText(this, R.string.aucune_image, Toast.LENGTH_LONG).show()

                   return@setOnClickListener
               }

            val MEDIA_TYPE_IMAGE: MediaType = MediaType.parse("image/*")!!

            val file =  File(photoPath.path!!);

            val fileBody: RequestBody = RequestBody.create(
                MEDIA_TYPE_IMAGE,
                file
            )

            val multipartBody =  MultipartBody.Part.createFormData(FILE_NAME, file.name, fileBody)


            ApiClient.apiService.ajouterProduit(multipartBody, UserSession.id, titre.text.toString(), prix.text.toString().toDouble(), desc.text.toString()).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            Toast.makeText(this@AjouterProduitActivity,content.get("message").asString,Toast.LENGTH_LONG).show()

                            finish()


                        }
                        else {
                            val content = response.body()

                            println(content)


                        }
                    }
                }
            )


        }
        val annuler = findViewById<Button>(R.id.annuler_btn)
        annuler.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun chooseImageCamera(){

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = getPhotoFile(FILE_NAME)

        val providerFile = FileProvider.getUriForFile(this,"com.example.animaly.fileprovider", filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

        startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE)

    }
    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpeg", directoryStorage)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            image.setImageBitmap(takenPhoto)
            photoPath = filePhoto.toUri()
            photo_state = true
        }
        else if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            val parcelFileDescriptor: ParcelFileDescriptor? =
                data?.data?.let { contentResolver.openFileDescriptor(it, "r") }
            val fileDescriptor: FileDescriptor = parcelFileDescriptor?.fileDescriptor!!
            val imageBmp = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()

            val file = File(cacheDir, "$FILE_NAME.jpeg")

            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
            imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()

            image.setImageBitmap(imageBmp)
            photoPath = file.toUri()
            photo_state = true

        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        println(photoPath)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}