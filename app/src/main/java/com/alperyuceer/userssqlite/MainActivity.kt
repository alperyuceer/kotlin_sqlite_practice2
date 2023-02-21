package com.alperyuceer.userssqlite

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperyuceer.userssqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var userList: ArrayList<User>

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        userList = ArrayList<User>()
        val adapter = UserAdapter(userList)
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        try {
            val database = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT * FROM users",null)
            val idIx = cursor.getColumnIndex("id")
            val userNameIx = cursor.getColumnIndex("kullaniciadi")
            while (cursor.moveToNext()){
                val id = cursor.getInt(idIx)
                val userName = cursor.getString(userNameIx)
                val user = User(id,userName)
                userList.add(user)

            }
            cursor.close()


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_new_user_item){
            val intent = Intent(this@MainActivity,NewUserActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}