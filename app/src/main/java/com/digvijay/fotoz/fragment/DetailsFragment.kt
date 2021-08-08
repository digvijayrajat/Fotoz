package com.digvijay.fotoz.fragment

import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.bumptech.glide.Glide
import com.digvijay.fotoz.R
import com.github.chrisbanes.photoview.OnSingleFlingListener
import com.github.chrisbanes.photoview.PhotoView
import com.digvijay.fotoz.utils.MessageEvent
import com.github.chrisbanes.photoview.OnPhotoTapListener
import org.greenrobot.eventbus.EventBus






class DetailsFragment : Fragment() {


    private var mStartingPosition: Int = 0
    private var mAlbumPosition: Int = 0
    internal var photoEntry: String? = null
    internal lateinit var ivContent: ImageView


    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on the screen.
     */
    val albumImage: ImageView?
        get() = if (isViewInBounds(activity!!.window.decorView, ivContent)) {
            ivContent
        } else null

    public fun newInstance(position: Int, startingPosition: Int, photoEntry: String): DetailsFragment {
        val args = Bundle()
        args.putInt(ARG_ALBUM_IMAGE_POSITION, position)
        args.putInt(ARG_STARTING_ALBUM_IMAGE_POSITION, startingPosition)
        args.putString(ARG_STARTING_ALBUM_IMAGE_PATH, photoEntry)
        val fragment = DetailsFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStartingPosition = arguments!!.getInt(ARG_STARTING_ALBUM_IMAGE_POSITION)
        mAlbumPosition = arguments!!.getInt(ARG_ALBUM_IMAGE_POSITION)
        photoEntry = arguments!!.getString(ARG_STARTING_ALBUM_IMAGE_PATH)

    }

    private val SWIPE_MIN_DISTANCE = 60
    private val SWIPE_THRESHOLD_VELOCITY = 100
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.view_photopreview, container, false)

        ivContent = rootView.findViewById<View>(R.id.iv_content_vpp) as PhotoView
        ivContent.transitionName = "Image$mAlbumPosition"

        Glide.with(context!!).load("file://" + photoEntry!!).dontAnimate().into(ivContent)

        (ivContent as PhotoView).getAttacher()
            .setOnSingleFlingListener(OnSingleFlingListener { e1, e2, velocityX, velocityY ->
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    return@OnSingleFlingListener false
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    return@OnSingleFlingListener false
                }
                if (e1.y - e2.y > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    EventBus.getDefault().post(MessageEvent(0))

                    return@OnSingleFlingListener true
                } else if (e2.y - e1.y > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    EventBus.getDefault().post(MessageEvent(0))

                    return@OnSingleFlingListener true
                }
                false
            })

        (ivContent as PhotoView).setOnPhotoTapListener(OnPhotoTapListener { view, x, y ->
            EventBus.getDefault().post(MessageEvent(0))
        })
        startPostponedEnterTransitio()



        return rootView
    }

    fun startPostponedEnterTransitio() {
        ivContent.getViewTreeObserver()
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    ivContent.getViewTreeObserver().removeOnPreDrawListener(this)
                    activity!!.startPostponedEnterTransition()
                    return true
                }
            })

    }
    companion object {

        private val ARG_ALBUM_IMAGE_POSITION = "arg_album_image_position"
        private val ARG_STARTING_ALBUM_IMAGE_POSITION = "arg_starting_album_image_position"
        private val ARG_STARTING_ALBUM_IMAGE_PATH = "path_of_image"

        /**
         * Returns true if {@param view} is contained within {@param container}'s bounds.
         */
        private fun isViewInBounds(container: View, view: View): Boolean {
            val containerBounds = Rect()
            container.getHitRect(containerBounds)
            return view.getLocalVisibleRect(containerBounds)
        }
    }


}
