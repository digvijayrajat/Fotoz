package com.digvijay.fotoz.fragment

import java.util.ArrayList


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.digvijay.fotoz.adapter.AlbumAdapter
import com.digvijay.fotoz.R
import com.digvijay.fotoz.activity.AlbumActivity
import com.digvijay.fotoz.component.PhoneMediaControl

class GalleryFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var mContext: Context? = null
    private var listAdapter: AlbumAdapter? = null
    private var mGridLayoutManager: GridLayoutManager? = null
    internal lateinit var mScaleGestureDetector: ScaleGestureDetector

    private lateinit var mview:View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = this.activity
        val v = inflater.inflate(R.layout.fragment_gallery, null)
        initializeView(v)
        return v
    }

    private fun initializeView(v: View) {
        recyclerView = v.findViewById(R.id.grid_view)
        /*todo emptyView = (TextView)v.findViewById(R.id.searchEmptyView);
       ;*/
        listAdapter = AlbumAdapter(albumsSorted, mContext!!)
        recyclerView!!.layoutManager = GridLayoutManager(this.mContext, 2)
        recyclerView!!.adapter = listAdapter

        mview=v.findViewById(R.id.view)

        LoadAllAlbum()
    }


    private fun LoadAllAlbum() {
        val mediaControl = PhoneMediaControl()
        mediaControl.loadalbumphoto = PhoneMediaControl.loadAlbumPhoto { albumsSorted_ ->
            albumsSorted = ArrayList(albumsSorted_)


            loadRecycler()
        }
        PhoneMediaControl.loadGalleryPhotosAlbums(mContext, 0)
    }


    var spancount=2

    private fun loadRecycler() {

        listAdapter =
            AlbumAdapter(albumsSorted, mContext!!)

        if (recyclerView != null) {

            mGridLayoutManager = GridLayoutManager(context, 2)


            recyclerView!!.layoutManager = mGridLayoutManager

            recyclerView!!.adapter = listAdapter
            if (listAdapter != null) {
                listAdapter!!.notifyDataSetChanged()
            }
            mScaleGestureDetector = ScaleGestureDetector(
                context,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        if (detector.currentSpan > 200 && detector.timeDelta > 200) {
                            if (detector.currentSpan - detector.previousSpan < -1) {
                                if (spancount == 1) {
                                    spancount=2
                                    animateRecyclerLayoutChange(spancount)
                                    return true
                                } else if (spancount == 2) {

                                    spancount=3
                                    animateRecyclerLayoutChange(spancount)
                                    return true
                                }
                            } else if (detector.currentSpan - detector.previousSpan > 1) {
                                if (spancount == 3) {
                                    spancount=2
                                    animateRecyclerLayoutChange(spancount)
                                    return true
                                } else if (spancount == 2) {
                                    spancount=1
                                    animateRecyclerLayoutChange(spancount)
                                    return true
                                }
                            }
                        }
                        return false
                    }

                })

            //set touch listener on recycler view
            recyclerView!!.setOnTouchListener { v, event ->
                mScaleGestureDetector.onTouchEvent(event)

                false
            }
        }
    }



    fun animateRecyclerLayoutChange(layoutSpanCount:Int) {
        (recyclerView!!.layoutManager as GridLayoutManager).spanCount= spancount
        (recyclerView!!.layoutManager as GridLayoutManager).requestLayout()
        recyclerView!!.adapter!!.notifyItemRangeChanged(0, AlbumActivity.photos.size-1)

        recyclerView!!.setOnTouchListener(null)
        Handler().postDelayed(
            {
                recyclerView!!.setOnTouchListener { v, event ->
                    mScaleGestureDetector.onTouchEvent(event)

                    false
                } },
            ((200 * 2) ).toLong()
        )
    }


    companion object {


        var albumsSorted = ArrayList<PhoneMediaControl.AlbumEntry>()
    }


}
