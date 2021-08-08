package com.digvijay.fotoz.activity


import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.view.*
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.digvijay.fotoz.ApplicationOwnGallery
import com.digvijay.fotoz.R
import com.digvijay.fotoz.adapter.FolderAdapter
import com.digvijay.fotoz.component.PhoneMediaControl
import com.digvijay.fotoz.fragment.GalleryFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_album.*

import java.util.ArrayList


class AlbumActivity : AppCompatActivity() {


    internal val EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position"
    internal val EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position"
    private var mTmpReenterState: Bundle? = null

    private var toolbar: Toolbar? = null
    private lateinit var recyclerView: RecyclerView

    private var itemWidth = 100
    private var listAdapter: FolderAdapter? = null
    private var AlbummID = 0
    private var mGridLayoutManager: GridLayoutManager? = null
    var spancount=2

    internal lateinit var mScaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        setExitSharedElementCallback(mCallback)

        var fade = Fade()

        fade.excludeTarget(tool_bar, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade



        initializeActionBar()
        initializeView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeActionBar() {

        val mBundle = intent.extras
        val nameAlbum = mBundle!!.getString("Key_Name")
        AlbummID = Integer.parseInt(mBundle.getString("Key_ID")!!)
        albumsSorted = GalleryFragment.albumsSorted

        photos = albumsSorted!![AlbummID].photos

        tvtitle.setText( ""+nameAlbum )
        back_button.setOnClickListener { onBackPressed() }
    }

    private fun initializeView() {
        recyclerView = findViewById<View>(R.id.grid_view) as RecyclerView
        listAdapter = FolderAdapter(photos, this@AlbumActivity, AlbummID)

        val position = 0
        val columnsCount = 2
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemWidth =
            (ApplicationOwnGallery.displaySize.x - (columnsCount + 1) * ApplicationOwnGallery.dp(4f)) / columnsCount
        recyclerView.adapter = listAdapter







        listAdapter!!.notifyDataSetChanged()
        recyclerView.scrollToPosition(position)

        LoadAllAlbum()
    }
    override fun  finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in,R.anim.slide_in_top);
    }

    private fun LoadAllAlbum() {

        if (listAdapter != null) {
            listAdapter!!.notifyDataSetChanged()
        }




        mGridLayoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = mGridLayoutManager

        recyclerView.adapter = listAdapter
        if (listAdapter != null) {
            listAdapter!!.notifyDataSetChanged()
        }
        mScaleGestureDetector = ScaleGestureDetector(
            this,
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



        var scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fab.show()
                }
                super.onScrollStateChanged(recyclerView, newState)

            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                if (dy > 0 ||dy<0 && fab.isShown())
                {
                    fab.hide()
                }
            }

        }
        recyclerView.addOnScrollListener(scrollListener)

        //set touch listener on recycler view
        recyclerView.setOnTouchListener { v, event ->
            mScaleGestureDetector.onTouchEvent(event)

            false
        }




    }

    fun bottomBarOnClickss(v: View) {
        val d = (v as ImageView).drawable
        if (d is Animatable) {
            (d as Animatable).start()

            Handler().postDelayed(
                {
                    v.setImageResource(R.drawable.pause_to_play_avd)
                    (d as Animatable).start()
                },
                ((200 ) ).toLong()

            )
            Handler().postDelayed(
                {

                    val mIntent = Intent(this, PhotoDetailActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putInt("Key_FolderID", AlbummID)
                    mBundle.putInt("Key_ID",0 )
                    mBundle.putBoolean("Slide",true )
                    mIntent.putExtras(mBundle)

                    startActivity(
                        mIntent)


                },
                ((200 * 2) ).toLong()
            )
        } else {

        }
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }


    fun animateRecyclerLayoutChange(layoutSpanCount:Int) {
        recyclerView.setOnTouchListener(null)

        (recyclerView!!.layoutManager as GridLayoutManager).spanCount= spancount
        (recyclerView!!.layoutManager as GridLayoutManager).requestLayout()
        recyclerView.adapter!!.notifyItemRangeChanged(0, photos.size-1)

        Handler().postDelayed(
            {
                recyclerView.setOnTouchListener { v, event ->
                    mScaleGestureDetector.onTouchEvent(event)

                    false
                } },
            ((200 * 2) ).toLong()
        )
    }



    companion object {

        var albumsSorted: ArrayList<PhoneMediaControl.AlbumEntry>? = null
        var photos = ArrayList<PhoneMediaControl.PhotoEntry>()
    }


    private val mCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(
            names: MutableList<String>,
            sharedElements: MutableMap<String, View>
        ) {
            if (mTmpReenterState != null) {
                val startingPosition = mTmpReenterState!!.getInt(EXTRA_STARTING_ALBUM_POSITION)
                val currentPosition = mTmpReenterState!!.getInt(EXTRA_CURRENT_ALBUM_POSITION)
                if (startingPosition != currentPosition) {
                    val newTransitionName = "Image$currentPosition"
                    val newSharedElement = recyclerView.findViewWithTag(newTransitionName) as View
                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)
                        sharedElements.clear()
                        sharedElements[newTransitionName] = newSharedElement
                    }
                }

                mTmpReenterState = null
            } else {
                val navigationBar = findViewById<View>(android.R.id.navigationBarBackground)
                val statusBar = findViewById<View>(android.R.id.statusBarBackground)
                if (navigationBar != null) {
                    names.add(navigationBar.transitionName)
                    sharedElements[navigationBar.transitionName] = navigationBar
                }
                if (statusBar != null) {
                    names.add(statusBar.transitionName)
                    sharedElements[statusBar.transitionName] = statusBar
                }
            }
        }
    }
    override fun onActivityReenter(requestCode: Int, data: Intent) {
        super.onActivityReenter(requestCode, data)
        mTmpReenterState = Bundle(data.extras)
        val startingPosition = mTmpReenterState!!.getInt(EXTRA_STARTING_ALBUM_POSITION)
        val currentPosition = mTmpReenterState!!.getInt(EXTRA_CURRENT_ALBUM_POSITION)
        if (startingPosition != currentPosition) {
            recyclerView.scrollToPosition(currentPosition)
        }
        postponeEnterTransition()
        recyclerView!!.getViewTreeObserver()
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView!!.getViewTreeObserver().removeOnPreDrawListener(this)
                    // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                    recyclerView!!.requestLayout()
                    startPostponedEnterTransition()
                    return true
                }
            })
    }


}
