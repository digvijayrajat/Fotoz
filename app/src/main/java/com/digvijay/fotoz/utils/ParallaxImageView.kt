package com.digvijay.fotoz.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View

import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

import com.digvijay.fotoz.R

class ParallaxImageView : AppCompatImageView {


    private val MAX_PARALLAX_OFFSET =
        context.resources.getDimension(R.dimen.parallax_image_view_offset).toInt()

    private var recyclerView_height = -1
    private val recyclerView_location = intArrayOf(-1, -1)

    internal var attached = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            heightMeasureSpec + context.resources.getDimension(R.dimen.parallax_image_view_offset).toInt()
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        attached = true

        val view = rootView.findViewWithTag<View>(RECYCLER_VIEW_TAG)
        if (view is RecyclerView) {
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!attached) {
                        recyclerView.removeOnScrollListener(this)
                        return
                    }

                    if (recyclerView_height == -1) {
                        recyclerView_height = recyclerView.height
                        recyclerView.getLocationOnScreen(recyclerView_location)
                    }

                    setParallaxTranslation()
                }
            })
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        attached = false
    }

    fun setParallaxTranslation() {
        if (recyclerView_height == -1) {
            return
        }

        val location = IntArray(2)
        getLocationOnScreen(location)

        val visible =
            location[1] + height > recyclerView_location[1] || location[1] < recyclerView_location[1] + recyclerView_height

        if (!visible) {
            return
        }

        val dy = (location[1] - recyclerView_location[1]).toFloat()

        val translationY = MAX_PARALLAX_OFFSET * dy / recyclerView_height.toFloat()
        setTranslationY(-translationY)
    }

    companion object {

        val RECYCLER_VIEW_TAG = "RECYCLER_VIEW_TAG"
    }
}
