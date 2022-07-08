package com.example.animaly.activities.ui.profil

import android.Manifest
import android.app.Activity
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.animaly.R
import com.example.animaly.activities.CompteActivity
import com.example.animaly.activities.HistoriqueActivity
import com.example.animaly.activities.LoginActivity
import com.example.animaly.activities.ResetPassActivity
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profil.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ProfilFragment : Fragment() {

    private lateinit var profilViewModel: ProfilViewModel

    lateinit var nom_prenom : TextView
    lateinit var picture : ImageView
    lateinit var  full_screen_picture_iv : ImageView
    private val CAMERA_REQUEST_CODE = 13
    private val FILE_NAME = "image"
    private val GALLERY_REQUEST_CODE = 14;
    private val PERMISSION_CODE = 1001;

    lateinit var filePhoto : File
    lateinit var photoPath : Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profilViewModel =
            ViewModelProvider(this).get(ProfilViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        val compteProfilBtn = root.findViewById<Button>(R.id.compte_profil_btn)
        compteProfilBtn.setOnClickListener {
            val intent = Intent(root.context, CompteActivity::class.java)
            startActivity(intent)
        }

        val resetPassBtn = root.findViewById<Button>(R.id.reset_pass_btn)
        resetPassBtn.setOnClickListener {
            val intent = Intent(root.context, ResetPassActivity::class.java)
            startActivity(intent)
        }

        val disconnectBtn = root.findViewById<Button>(R.id.deconnecter_btn)
        disconnectBtn.setOnClickListener {

            val sharedPref = activity?.getSharedPreferences(
                getString(R.string.user), Context.MODE_PRIVATE)

            if (sharedPref != null) {
                with (sharedPref.edit()) {
                    clear()
                    commit()
                }
            }

            UserSession.reset()

            val intent = Intent(root.context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity(root.context as Activity)

        }

        val historiqueBtn = root.findViewById<Button>(R.id.historique_commande_btn)
        historiqueBtn.setOnClickListener {
            val intent = Intent(root.context, HistoriqueActivity::class.java)
            startActivity(intent)

        }

         nom_prenom = root.findViewById<TextView>(R.id.nom_prenom_tv)
         picture = root.findViewById<ImageView>(R.id.profile_picture_iv)

        nom_prenom.text = UserSession.fullName()

        Picasso.get().load("http://10.0.2.2:3000/img/${UserSession.image}").into(picture)

        val fullScreenLayout = root.findViewById<FrameLayout>(R.id.fullScreenLayout)

        val change_picture = root.findViewById<Button>(R.id.change_picture_btn)
        val close_picture = root.findViewById<Button>(R.id.close_picture_btn)
        full_screen_picture_iv = root.findViewById<ImageView>(R.id.full_screen_picture_iv)

        Picasso.get().load("http://10.0.2.2:3000/img/${UserSession.image}").into(full_screen_picture_iv)

        picture.setOnClickListener{
            fullScreenLayout.visibility = View.VISIBLE

        }

        close_picture.setOnClickListener{

            fullScreenLayout.visibility = View.GONE

        }

        change_picture.setOnClickListener{
            val builder = activity?.let { it1 -> AlertDialog.Builder(it1) }
            builder?.setMessage(getString(R.string.selectionner_image))?.setCancelable(true)
                ?.setPositiveButton(getString(R.string.camera)) { dialog, id ->

                    chooseImageCamera();

                }?.setNegativeButton(getString(R.string.galerie)) { dialog, id ->

                if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                    chooseImageGallery();
                }
            }
            val alert = builder?.create()
            if (alert != null) {
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK)
                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLACK)
            }


        }


        return root
    }

    private fun uploadProfilePicture(profilePic : Bitmap) {
        val MEDIA_TYPE_IMAGE: MediaType = MediaType.parse("image/*")!!

        val file =  File(photoPath.path!!);

        val fileBody: RequestBody = RequestBody.create(
            MEDIA_TYPE_IMAGE,
            file
        )

        val multipartBody =  MultipartBody.Part.createFormData(FILE_NAME, file.name, fileBody)



        ApiClient.apiService.editProfilePicture(multipartBody, UserSession.email).enqueue(
            object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    t.printStackTrace()

                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {

                        val content = response.body()

                        Toast.makeText(activity,content.get("message").asString,Toast.LENGTH_LONG).show()

                        if(response.code() == 200)
                        {
                            full_screen_picture_iv.setImageBitmap(profilePic)
                            picture.setImageBitmap(profilePic)

                            UserSession.image = content.get("profilePicture").asString

                        }




                    }
                    else {
                        val content = response.body()

                        println(content)


                    }
                }
            }
        )
    }
    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun chooseImageCamera(){

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = getPhotoFile(FILE_NAME)

        val providerFile = activity?.let { FileProvider.getUriForFile(it,"com.example.animaly.fileprovider", filePhoto) }
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

        startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE)

    }
    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpeg", directoryStorage)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            photoPath = filePhoto.toUri()

            uploadProfilePicture(takenPhoto)


        }
        else if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            val parcelFileDescriptor: ParcelFileDescriptor? =
                data?.data?.let { activity?.contentResolver?.openFileDescriptor(it, "r") }
            val fileDescriptor: FileDescriptor = parcelFileDescriptor?.fileDescriptor!!
            val imageBmp = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()

            val file = File(activity?.cacheDir, "$FILE_NAME.jpeg")

            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
            imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
            photoPath = file.toUri()

            uploadProfilePicture(imageBmp)


        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }



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
                    Toast.makeText(activity,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onResume() {

        nom_prenom.text = UserSession.fullName()
        Picasso.get().load("http://10.0.2.2:3000/img/${UserSession.image}").into(picture)

        super.onResume()

    }
}