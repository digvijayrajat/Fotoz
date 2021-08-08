package com.digvijay.fotoz.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.digvijay.fotoz.R
import com.digvijay.fotoz.activity.AlbumActivity
import com.digvijay.fotoz.activity.PhotoDetailActivity
import com.digvijay.fotoz.component.PhoneMediaControl

import java.util.ArrayList

class FolderAdapter(photos: ArrayList<PhoneMediaControl.PhotoEntry>,
                    context: AlbumActivity,
                    albummID: Int
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    var context=context
    var photos =photos
    var albummID =albummID
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_image, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {


        holder.image.setTag("Image$i")

        val mPhotoEntry = photos.get(i)
        val path = mPhotoEntry.path
        if (path != null && path != "") {

            Glide.with(context).load("file://$path").into(holder.image)

        }

        holder.image.setOnClickListener {
            val mIntent = Intent(context, PhotoDetailActivity::class.java)
            val mBundle = Bundle()
            mBundle.putInt("Key_FolderID", albummID)
            mBundle.putInt("Key_ID", i)
            mIntent.putExtras(mBundle)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity, it,
                "Image$i"
            )
            context.startActivity(
                mIntent,
                options.toBundle()
            ) }


    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById<View>(R.id.album_image) as ImageView

        }
    }
}