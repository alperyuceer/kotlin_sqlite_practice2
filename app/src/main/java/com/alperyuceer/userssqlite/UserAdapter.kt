package com.alperyuceer.userssqlite

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alperyuceer.userssqlite.databinding.RecyclerviewRowBinding

class UserAdapter(val userList:ArrayList<User>):RecyclerView.Adapter<UserAdapter.UserHolder>() {
    class UserHolder(val binding: RecyclerviewRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  UserHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.binding.rvKullaniciAdi.text = userList.get(position).userName
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CurrentUserActivity::class.java)
            intent.putExtra("id",userList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }
}