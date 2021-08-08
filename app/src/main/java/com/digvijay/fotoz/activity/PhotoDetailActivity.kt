package com.digvijay.fotoz.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.SharedElementCallback
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.digvijay.fotoz.R
import com.digvijay.fotoz.adapter.InfoRecyclerViewAdapter
import com.digvijay.fotoz.component.PhoneMediaControl
import com.digvijay.fotoz.fragment.DetailsFragment
import com.digvijay.fotoz.fragment.GalleryFragment
import com.digvijay.fotoz.utils.DepthTransformation
import kotlinx.android.synthetic.main.activity_photopreview.*
import com.digvijay.fotoz.utils.MessageEvent
import com.google.android.material.snackbar.Snackbar
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.EventBus
import java.util.*


class PhotoDetailActivity : AppCompatActivity() {
    var photos: List<PhoneMediaControl.PhotoEntry>? = null


    internal val EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position"
    internal val EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position"

    private val mCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(
            names: MutableList<String>,
            sharedElements: MutableMap<String, View>
        ) {
            if (mIsReturning) {
                val sharedElement = mCurrentDetailsFragment!!.albumImage
                if (sharedElement == null) {
                    names.clear()
                    sharedElements.clear()
                } else if (mStartingPosition != mCurrentPosition) {
                    names.clear()
                    names.add(sharedElement.transitionName)
                    sharedElements.clear()
                    sharedElements[sharedElement.transitionName] = sharedElement
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private var mCurrentDetailsFragment: DetailsFragment? = null
    private var mCurrentPosition: Int = 0
    private var mStartingPosition: Int = 0
    private var mIsReturning: Boolean = false
    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photopreview)
        postponeEnterTransition()



        var fade = Fade()

        fade.excludeTarget(tool_bar, true)
        fade.excludeTarget(bottom_bar, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade
        setEnterSharedElementCallback(mCallback)

        mStartingPosition = intent.getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0)
        if (savedInstanceState == null) {
            mCurrentPosition = mStartingPosition
        } else {
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION)
        }

        val mBundle = intent.extras

        var folderPosition = mBundle!!.getInt("Key_FolderID",0)
        var current = mBundle.getInt("Key_ID")

        photos = GalleryFragment.albumsSorted[folderPosition].photos
        tvtitle.setText(" "+current+" / "+ ((photos as ArrayList<PhoneMediaControl.PhotoEntry>?)?.size))

        val pager = findViewById<View>(R.id.vp_base_app) as ViewPager
        pager.adapter = DetailsFragmentPagerAdapter(supportFragmentManager)
        pager.currentItem = current
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                mCurrentPosition = position
                tvtitle.setText(" "+mCurrentPosition+" / "+ ((photos as ArrayList<PhoneMediaControl.PhotoEntry>?)?.size))

            }
        })

        pager.setPageTransformer(true,DepthTransformation())
        Handler().postDelayed({
            fullScreen()

        }, 2000)

        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if(pager.currentItem<photos!!.size-1)
                        pager.currentItem=pager.currentItem+1
                    else{
                        timer.cancel()
                        timer.purge()
                    }
                }

            }
        }

        if(mBundle.getBoolean("Slide",false )){

            timer.schedule(timerTask, 400, 3000)
        }

    }
    fun bottomBarOnClicks(v: View) {
        val d = (v as ImageView).drawable
        if (d is Animatable) {
            (d as Animatable).start()
            Handler().postDelayed(
                { bottomBarAction(v) },
                ((200 * 2) ).toLong()
            )
        } else {
            bottomBarAction(v)
        }
    }

    private fun bottomBarAction(v: ImageView) {
        when (v.id) {
            R.id.info_button -> showInfoDialog()
            R.id.share_button -> sharePhoto()
            R.id.edit_button -> editPhoto()
            R.id.delete_button -> showDeleteDialog()
            R.id.back_button -> onBackPressed()
            else -> {
            }
        }
    }
    fun sharePhoto() {
        val uri = Uri.parse(photos?.get(mCurrentPosition)?.path)

        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)

        startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))
    }

    fun editPhoto() {
        val uri = Uri.parse(photos?.get(mCurrentPosition)?.path)

        val intent =  Intent(Intent.ACTION_EDIT)
        intent.setDataAndType(uri, "image/*")
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, null))


    }
    fun showDeleteDialog() {


        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Do you want to Delete this file ?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> deletePhoto()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Attention")
        alert.show()

    }

    fun deletePhoto() {


        var snackbar = Snackbar.make(bg, "Delete feature coming soon", Snackbar.LENGTH_LONG)
        snackbar.show()
        //Toast.makeText(this,"delete feature coming soon",Toast.LENGTH_LONG).show()
    }
    private var infoDialog: AlertDialog? = null

    fun showInfoDialog() {
        val adapter = InfoRecyclerViewAdapter()
        val exifSupported = adapter.exifSupported(this, photos?.get(mCurrentPosition)?.path)

        val rootView:View = LayoutInflater.from(this)
            .inflate(
                R.layout.info_dialog_layout, findViewById(R.id.bg) as ViewGroup, false
            )

        val loadingBar = rootView.findViewById(R.id.progress_bar) as ProgressBar
        loadingBar.setVisibility(View.VISIBLE)
        val dialogLayout = rootView.findViewById(R.id.dialog_layout) as View
        dialogLayout.setVisibility(View.GONE)

        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.details))
            .setView(rootView)
            .setPositiveButton(R.string.close, null)
            .setOnDismissListener(DialogInterface.OnDismissListener { infoDialog = null })

        infoDialog = builder.create()
        infoDialog!!.show()

        adapter.retrieveData(photos?.get(mCurrentPosition)?.path, false,
            object : InfoRecyclerViewAdapter.OnDataRetrievedCallback {
                override fun getContext(): Context {
                    return this@PhotoDetailActivity
                }


                override fun onDataRetrieved() {
                    this@PhotoDetailActivity.runOnUiThread(Runnable {
                        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
                        val layoutManager = LinearLayoutManager(this@PhotoDetailActivity)
                        recyclerView.setLayoutManager(layoutManager)
                        recyclerView.setAdapter(adapter)

                        val scrollIndicatorTop = rootView.findViewById(R.id.scroll_indicator_top) as View
                        val scrollIndicatorBottom = rootView.findViewById(R.id.scroll_indicator_bottom) as View

                        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                scrollIndicatorTop.setVisibility(
                                    if (recyclerView.canScrollVertically(-1))
                                        View.VISIBLE
                                    else
                                        View.INVISIBLE
                                )

                                scrollIndicatorBottom.setVisibility(
                                    if (recyclerView.canScrollVertically(1))
                                        View.VISIBLE
                                    else
                                        View.INVISIBLE
                                )
                            }
                        })

                        loadingBar.setVisibility(View.GONE)
                        dialogLayout.setVisibility(View.VISIBLE)
                    })
                }

                override fun failed() {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_PAGE_POSITION, mCurrentPosition)
    }

    override fun onPause() {
        super.onPause()

        timer.cancel()
        timer.purge()
    }
    override fun finishAfterTransition() {
        mIsReturning = true
        if(notImmersive) {
            tool_bar.visibility = View.GONE
            bottom_bar.visibility=View.GONE
        }
        val data = Intent()
        data.putExtra(EXTRA_STARTING_ALBUM_POSITION, mStartingPosition)
        data.putExtra(EXTRA_CURRENT_ALBUM_POSITION, mCurrentPosition)
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }

    private inner class DetailsFragmentPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return DetailsFragment().newInstance(position, mStartingPosition,
                photos!!.get(position).path
            )
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            mCurrentDetailsFragment = `object` as DetailsFragment
        }

        override fun getCount(): Int {
            return photos!!.size
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {/* Do something */

        if(event.ev==0)
            onBackPressed()
        else {
            timer.cancel()
            timer.purge()
            fullScreen()
        }
    }

    var notImmersive=true
    fun fullScreen() {


        showUI(!notImmersive)

        if(notImmersive) {
            var colorFrom = Color.WHITE
            var colorTo = Color.BLACK
            var colorAnimation = ValueAnimator.ofObject( ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(200)
            colorAnimation.addUpdateListener {
                bg.setBackgroundColor(it.animatedValue as Int)

                window.setStatusBarColor(it.animatedValue as Int)
                window.navigationBarColor = (it.animatedValue as Int)
            }
            colorAnimation.start()
            notImmersive=false

        }
        else{

            var colorFrom = Color.BLACK
            var colorTo = Color.WHITE
            var colorAnimation = ValueAnimator.ofObject( ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(200)
            colorAnimation.addUpdateListener {
                bg.setBackgroundColor(it.animatedValue as Int)

                window.setStatusBarColor(it.animatedValue as Int)
                window.navigationBarColor = (it.animatedValue as Int)
            }
            colorAnimation.start()
            notImmersive=true

        }


    }

    private fun showUI(show: Boolean) {
        val toolbar_translationY = (if (show) 0 else -tool_bar.getHeight()).toFloat()
        val bottomBar_translationY = (if (show)
            0
        else
            (bottom_bar as View).height).toFloat()
        tool_bar.animate()
            .translationY(toolbar_translationY)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        (bottom_bar as View).animate()
            .translationY(bottomBar_translationY)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }


    companion object {


        private val STATE_CURRENT_PAGE_POSITION = "state_current_page_position"
    }
}
