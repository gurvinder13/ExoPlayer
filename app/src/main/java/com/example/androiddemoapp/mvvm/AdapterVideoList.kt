package com.example.androiddemoapp.mvvm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.androiddemoapp.R
import java.util.*

class AdapterVideoList internal constructor(var context: Context, videosList: ArrayList<ModelVideo>) :
        RecyclerView.Adapter<AdapterVideoList.MyViewHolder>() {
    var videosList = ArrayList<ModelVideo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_video, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = videosList[position]
        holder.tv_title.text = item.title
        holder.tv_duration.text = item.duration
        Glide.with(context).load(item.data).into(holder.imgView_thumbnail)
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, Activity_Player::class.java)
            intent.putExtra("videoId", item.id)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videosList.size
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var imgView_thumbnail: ImageView
        var tv_title: TextView
        var tv_duration: TextView

        init {
            tv_title = itemView.findViewById(R.id.tv_title)
            tv_duration = itemView.findViewById(R.id.tv_duration)
            imgView_thumbnail = itemView.findViewById(R.id.imageView_thumbnail)
        }
    }

    fun filterList(filteredList: ArrayList<ModelVideo>) {
        videosList = filteredList
        notifyDataSetChanged()
    }

    init {
        this.videosList = videosList
    }
}