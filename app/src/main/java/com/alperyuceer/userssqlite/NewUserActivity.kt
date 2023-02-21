package com.alperyuceer.userssqlite

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alperyuceer.userssqlite.databinding.ActivityNewUserBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class NewUserActivity : AppCompatActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityNewUserBinding
    var selectedBitmap: Bitmap?=null
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        database = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)
        registerLaunch()
    }
    fun selectImage(view: View){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
            //rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@NewUserActivity,android.Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"Permission Needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    //izin iste
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)

                }).show()

            }else{
                //izin iste
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }else{
            //izin verildi
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    fun register(view: View){
        val kullaniciAdi =binding.kullaniciAdiEditText.text.toString()
        val adSoyad= binding.adSoyadEditText.text.toString()
        val memleket= binding.memleketEditText.text.toString()
        val dogumTarihi= binding.dogumTarihiEditText.text.toString()
        if (selectedBitmap!=null){

            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, kullaniciadi VARCHAR, adsoyad VARCHAR, memleket VARCHAR, dogumtarihi VARCHAR, image BLOB)")
                val sqlString = "INSERT INTO users (kullaniciadi, adsoyad, memleket, dogumtarihi, image) VALUES (?, ?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1,kullaniciAdi)
                statement.bindString(2,adSoyad)
                statement.bindString(3,memleket)
                statement.bindString(4,dogumTarihi)
                statement.bindBlob(5,byteArray)
                statement.execute()
            }catch (e:Exception){
                e.printStackTrace()
            }
            val intent = Intent(this@NewUserActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }
    private fun registerLaunch(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult!=null){
                    val imageData = intentFromResult.data
                    if (imageData!=null){
                        try {
                            if (Build.VERSION.SDK_INT>=28){
                                val source = ImageDecoder.createSource(this@NewUserActivity.contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.selectImageView.setImageBitmap(selectedBitmap)

                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@NewUserActivity.contentResolver,imageData)
                                binding.selectImageView.setImageBitmap(selectedBitmap)
                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                //izin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                //izin iste
                Toast.makeText(this@NewUserActivity,"Permission Denied",Toast.LENGTH_LONG).show()

            }
        }

    }
    fun makeSmallerBitmap(image:Bitmap,maxSize: Int):Bitmap{
        var width = image.width
        var height = image.height
        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio>1){
            //yatay
            width = maxSize
            val scaledHeight = width/bitmapRatio
            height = scaledHeight.toInt()

        }else{
            //dikey
            height = maxSize
            val scaledWidth = height*bitmapRatio
            width = scaledWidth.toInt()

        }
        return Bitmap.createScaledBitmap(image,width,height,true)

    }
}