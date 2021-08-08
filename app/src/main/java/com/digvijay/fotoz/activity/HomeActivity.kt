package com.digvijay.fotoz.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.content.pm.ResolveInfo
import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.util.Util
import com.digvijay.fotoz.R
import com.digvijay.fotoz.fragment.GalleryFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.*



class HomeActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var toolbar: Toolbar? = null
    private var Drawer: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var currentFragment: Fragment? = null

    private val currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mContext = this
        initializeActionBar()
        initialCalling()
    }


    override fun onBackPressed() {
        if (Drawer?.isDrawerOpen(Gravity.LEFT)!!) {
            Drawer?.closeDrawer(Gravity.LEFT)
        } else {
            super.onBackPressed()
        }
    }




    private fun initializeActionBar() {
        toolbar = findViewById(R.id.tool_bar) as Toolbar
        toolbar!!.setTitle("")
        setSupportActionBar(toolbar)


        Drawer = findViewById(R.id.DrawerLayout) as DrawerLayout
        mDrawerToggle = object : ActionBarDrawerToggle(
            this, Drawer, toolbar,
            R.string.openDrawer, R.string.closeDrawer
        ) {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                if(!camera.isOrWillBeHidden){
                    camera.hide()
                }
            }
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

            }

            override fun onDrawerClosed(drawerView: View) {
                camera.show()
                super.onDrawerClosed(drawerView)
            }

        }
        Drawer!!.setDrawerListener(mDrawerToggle)
        (mDrawerToggle as ActionBarDrawerToggle).syncState()

        (mDrawerToggle as ActionBarDrawerToggle).getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));

    }

    private fun closeDrware() {
        if (Drawer!!.isDrawerOpen(Gravity.LEFT)) {
            Drawer!!.closeDrawer(Gravity.LEFT)
        }
    }

    private fun initialCalling() {
        fragmentManager = supportFragmentManager

        fragmentTransaction = fragmentManager!!.beginTransaction()

        getFragment(0)
        attachedFragment()

        so.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra(
                    "url",
                    " https://stackoverflow.com/users/5133135/digvijay-singh?tab=profile"
                ))
        }

        linkedin.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra(
                    "url",
                    "https://www.linkedin.com/in/digvijayrajat/"
                ))
        }
        demo.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra(
                    "url",
                    " https://youtu.be/xK3YUFVuM6w"
                ))
        }


    }


    private fun attachedFragment() {
        try {
            if (currentFragment != null) {
                if (fragmentTransaction!!.isEmpty()) {
                    fragmentTransaction?.add(
                        R.id.fragment_container,
                        currentFragment!!,
                        "" + currentFragment.toString()
                    )
                    fragmentTransaction?.commit()
                    toolbar?.setTitle("Fotoz")
                } else {
                    fragmentTransaction = fragmentManager!!.beginTransaction()
                    fragmentTransaction!!.replace(
                        R.id.fragment_container,
                        currentFragment!!,
                        "" + currentFragment.toString()
                    )
                    fragmentTransaction!!.commit()
                    toolbar?.setTitle("Fotoz")
                }

            }
            closeDrware()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }


    private fun getFragment(postion: Int) {
        when (postion) {
            0 -> currentFragment = GalleryFragment()
            else -> {
            }
        }
    }



    fun bottomBarOnClick(v: View) {
        val d = (v as FloatingActionButton).drawable
        if (d is Animatable ) {
            (d as Animatable).start()
            Handler().postDelayed(
                { bottomBarAction(v) },
                ((400 * 2) ).toLong()
            )
        } else {
            bottomBarAction(v)
        }
    }

    private fun bottomBarAction(v: ImageView) {
        var Intent3=   Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(Intent3);   }

}
