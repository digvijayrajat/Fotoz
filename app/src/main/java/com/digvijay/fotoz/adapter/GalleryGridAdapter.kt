package com.digvijay.fotoz.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digvijay.fotoz.R
import com.digvijay.fotoz.activity.AlbumActivity
import com.digvijay.fotoz.component.PhoneMediaControl

import java.util.ArrayList


class AlbumAdapter(

    var albumsSorted: ArrayList<PhoneMediaControl.AlbumEntry>,
    private val context: Context
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_picker_album_layout, parent, false)
        return ViewHolder(v)
    }
    var lastPosition=0

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {


        val albumEntry = albumsSorted[i]

        val animation = AnimationUtils.loadAnimation(
            context,
            if (i > lastPosition)
                R.anim.up_from_bottom
            else
                R.anim.down_from_top
        )

        holder.itemView.startAnimation(animation)
        lastPosition = i

        if (albumEntry.coverPhoto != null && albumEntry.coverPhoto.path != null) {
            Glide.with(context).load("file://" + albumEntry.coverPhoto.path)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image)

        } else {
            holder.image.setImageResource(R.drawable.nophotos)
        }

        holder.name.text = albumEntry.bucketName
        holder.count.text = ""+albumEntry.photos.size+" items";
        holder.cardView.setOnClickListener {
            val mIntent = Intent(context, AlbumActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("Key_ID", i.toString() + "")
            mBundle.putString("Key_Name", albumsSorted[i].bucketName + "")
            mIntent.putExtras(mBundle)

            context.startActivity(mIntent)
            (context as Activity).overridePendingTransition( R.anim.slide_in_up, android.R.anim.fade_out );

        }
    }

    override fun getItemCount(): Int {
        return albumsSorted.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var name: TextView
        var count: TextView
        var cardView: CardView

        init {
            image = itemView.findViewById<View>(R.id.media_photo_image) as ImageView
            count = itemView.findViewById<View>(R.id.album_count) as TextView
            name = itemView.findViewById<View>(R.id.album_name) as TextView

            cardView = itemView.findViewById<View>(R.id.card) as CardView

        }
    }
}