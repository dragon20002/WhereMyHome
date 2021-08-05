package com.minuminu.haruu.wheremyhome.view.picturefullscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.utils.AppUtils

@Suppress("DEPRECATION")
@SuppressLint("ClickableViewAccessibility")
class PictureFullscreenActivity : AppCompatActivity() {
    private lateinit var hideHandler: Handler

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var showActionBar: Boolean = false
    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var btnCancel: Button? = null
    private var fullscreenDummyContent: TextView? = null
    private var fullscreenContent: ImageView? = null
    private var fullscreenContentControls: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_fullscreen)

        hideHandler = Handler(Looper.getMainLooper())

        showActionBar = true

        btnCancel = findViewById(R.id.btn_cancel)
        fullscreenDummyContent = findViewById(R.id.fullscreen_dummy_content)
        fullscreenContent = findViewById(R.id.fullscreen_content)
        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)
        // Set up the user interaction to manually show or hide the system UI.
        fullscreenDummyContent?.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        btnCancel?.setOnTouchListener(delayHideTouchListener)
        btnCancel?.setOnClickListener {
            finish()
        }

        intent.extras?.getString("pictureName")?.also {
            fullscreenContent?.apply {
                val imageFile = AppUtils.loadImageFile(this@PictureFullscreenActivity, it).let {
                    val metrics = resources.displayMetrics
                    val width = metrics.widthPixels.toFloat()
                    val height = metrics.heightPixels.toFloat()
                    AppUtils.resizeBitmap(it, width, height)
                }
                setImageBitmap(imageFile)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        btnCancel = null
        fullscreenDummyContent = null
        fullscreenContentControls = null
    }

    private fun toggle() {
        if (showActionBar) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        fullscreenContentControls?.visibility = View.GONE
        showActionBar = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
        fullscreenDummyContent?.fitsSystemWindows = true
        showActionBar = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        supportActionBar?.show()
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}
