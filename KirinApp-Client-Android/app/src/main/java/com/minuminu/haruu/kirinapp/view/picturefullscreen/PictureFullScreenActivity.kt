package com.minuminu.haruu.kirinapp.view.picturefullscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.minuminu.haruu.kirinapp.R
import com.minuminu.haruu.kirinapp.utils.AppUtils
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
@SuppressLint("ClickableViewAccessibility")
class PictureFullScreenActivity : AppCompatActivity() {
    companion object {
        private const val MIN_SCALE_FACTOR: Float = 1.0f
        private const val MAX_SCALE_FACTOR: Float = 10.0f
    }

    private var imageViewportWidth: Float = 0f
    private var imageViewportHeight: Float = 0f
    private var imagePivot: Pair<Float, Float>? = null
    private var imageScaleFactor: Float = 1.0f

    private var fullscreenDummyContent: TextView? = null
    private var fullscreenContent: ImageView? = null

    private var gestureDetector: GestureDetector? = null
    private var scaleGestureDetector: ScaleGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_fullscreen)

        window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        supportActionBar?.hide()

        fullscreenDummyContent = findViewById(R.id.fullscreen_dummy_content)
        fullscreenContent = findViewById(R.id.fullscreen_content)
        intent.extras?.getString("pictureName")?.also {
            fullscreenContent?.let { imageView ->
                val imageFile = AppUtils.loadImageFile(this@PictureFullScreenActivity, it)

                val metrics = imageView.resources.displayMetrics
                val factorX = metrics.widthPixels * 1f / imageFile.width
                val factorY = metrics.heightPixels * 1f / imageFile.height
                // ???????????? ??? ?????? ????????? ??????
                val factor = min(factorX, factorY)
                imageViewportWidth = imageFile.width * factor
                imageViewportHeight = imageFile.height * factor

                Log.d(
                    PictureFullScreenActivity::class.simpleName,
                    "load image ${imageFile.width} ${imageFile.height}"
                )

                imageView.setImageBitmap(imageFile)
            }
        }

        gestureDetector = GestureDetector(
            applicationContext,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (e1 == null || e2 == null)
                        return false

                    if (imagePivot == null) {
                        // ????????? ????????? ????????? ??? ????????? ????????????????????? ?????? ?????????
                        fullscreenContent?.let {
                            imagePivot = Pair(it.pivotX, it.pivotY)
                        }
                    }

                    imagePivot?.let {
                        // ????????? ????????? ????????????
                        imagePivot = moveImageView(
                            it, distanceX, distanceY
                        )
                    }
                    return true
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    val nextScaleFactor = scaleImageView(imageScaleFactor * 2f)

                    if (nextScaleFactor == MAX_SCALE_FACTOR && imageScaleFactor == nextScaleFactor) {
                        fullscreenContent?.let {
                            it.scaleX = 1f
                            it.scaleY = 1f
                        }
                        imageScaleFactor = 1f
                    } else {
                        imageScaleFactor = nextScaleFactor
                    }
                    return true
                }
            }
        )
        scaleGestureDetector = ScaleGestureDetector(
            applicationContext,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    detector?.let {
                        imageScaleFactor = scaleImageView(imageScaleFactor * it.scaleFactor)
                    }
                    return true
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            when {
                it.pointerCount > 1 -> scaleGestureDetector?.onTouchEvent(event)
                else -> gestureDetector?.onTouchEvent(event)
            }
        } ?: false
    }

    private fun moveImageView(
        prevPivot: Pair<Float, Float>,
        distX: Float,
        distY: Float
    ): Pair<Float, Float>? {

        fullscreenContent?.let { imageView ->
            val displayMetrics = imageView.context.resources.displayMetrics
            // 1. ????????? ??????
            // [?????????] = ([?????????] ?? [??????????????????]) / ([????????????] ?? [????????????])
            val offsetX =
                (distX * imageViewportWidth) / displayMetrics.widthPixels
            val offsetY =
                (distY * imageViewportHeight) / displayMetrics.heightPixels

            // ???????????? ?????? ??????
            val pivotX = prevPivot.first + offsetX
            val pivotY = prevPivot.second + offsetY
            Log.d(PictureFullScreenActivity::class.java.simpleName, "pivot $pivotX $pivotY")

            // 2. ????????? ?????? ??????
            // [?????? ?????????????????? ??????] = [??????????????????] - ([????????????] ?? [????????????])
            val maxDistX =
                imageViewportWidth - displayMetrics.widthPixels / imageScaleFactor
            val maxDistY =
                imageViewportHeight - displayMetrics.heightPixels / imageScaleFactor

            // ???????????? ????????? ?????? ??????
            var rangedPivotX = max(
                (displayMetrics.widthPixels - maxDistX) / 2,
                min(pivotX, (displayMetrics.widthPixels + maxDistX) / 2)
            )
            var rangedPivotY = max(
                (displayMetrics.heightPixels - maxDistY) / 2,
                min(pivotY, (displayMetrics.heightPixels + maxDistY) / 2)
            )

            // 3. ????????? ?????? ??????2
            // [??????????????????] < ([????????????] ?? [????????????]) ?????? ??????????????? ?????? ???????????? ????????? ??????
            if (imageViewportWidth * imageScaleFactor < displayMetrics.widthPixels.toFloat()) {
                // center horizontal
                rangedPivotX = displayMetrics.widthPixels.toFloat() / 2
            }
            if (imageViewportHeight * imageScaleFactor < displayMetrics.heightPixels.toFloat()) {
                // center vertical
                rangedPivotY = displayMetrics.heightPixels.toFloat() / 2
            }

            imageView.pivotX = rangedPivotX
            imageView.pivotY = rangedPivotY
            return Pair(rangedPivotX, rangedPivotY)
        }
        return null
    }

    private fun scaleImageView(factor: Float): Float {
        val rangedFactor =
            max(MIN_SCALE_FACTOR, min(factor, MAX_SCALE_FACTOR))

        fullscreenContent?.let { imageView ->
            imageView.scaleX = rangedFactor
            imageView.scaleY = rangedFactor

            // ????????? ?????? ??????
            // [??????????????????] < ([????????????] ?? [????????????]) ?????? ??????????????? ?????? ???????????? ????????? ??????
            val displayMetrics = imageView.context.resources.displayMetrics
            if (imageViewportWidth * imageScaleFactor < displayMetrics.widthPixels.toFloat()) {
                // center horizontal
                imageView.pivotX = displayMetrics.widthPixels.toFloat() / 2
            }
            if (imageViewportHeight * imageScaleFactor < displayMetrics.heightPixels.toFloat()) {
                // center vertical
                imageView.pivotY = displayMetrics.heightPixels.toFloat() / 2
            }
        }

        return rangedFactor
    }

}
