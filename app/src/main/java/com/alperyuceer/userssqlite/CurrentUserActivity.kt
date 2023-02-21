package com.alperyuceer.userssqlite

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alperyuceer.userssqlite.databinding.ActivityCurrentUserBinding

class CurrentUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentUserBinding
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)

        val intent = intent
        val selectedId =intent.getIntExtra("id",1)
        val cursor = database.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(selectedId.toString()))
        val userNameIx = cursor.getColumnIndex("kullaniciadi")
        val nameIx = cursor.getColumnIndex("adsoyad")
        val yearIx = cursor.getColumnIndex("dogumtarihi")
        val memleketIx = cursor.getColumnIndex("memleket")
        val imageIx = cursor.getColumnIndex("image")

        while (cursor.moveToNext()){
            binding.currentUserNameTextView.text = cursor.getString(userNameIx)
            binding.currentNameView.text = cursor.getString(nameIx)
            binding.currentMemleketView.text = cursor.getString(memleketIx)
            binding.currentDogumTarihView.text = cursor.getString(yearIx)

            val byteArray = cursor.getBlob(imageIx)
            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            binding.imageView.setImageBitmap(bitmap)
        }
        cursor.close()
    }
}