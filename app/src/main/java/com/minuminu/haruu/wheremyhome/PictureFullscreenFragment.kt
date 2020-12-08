package com.minuminu.haruu.wheremyhome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.minuminu.haruu.wheremyhome.utils.Utils

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class PictureFullscreenFragment : Fragment() {
    private lateinit var hideHandler: Handler
    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        activity?.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { v, e ->
        v.performClick()
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var btnCancel: Button? = null
    private var fullscreenDummyContent: TextView? = null
    private var fullscreenContent: ImageView? = null
    private var fullscreenContentControls: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_picture_fullscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideHandler = Handler(Looper.getMainLooper())

        visible = true

        btnCancel = view.findViewById(R.id.btn_cancel)
        fullscreenDummyContent = view.findViewById(R.id.fullscreen_dummy_content)
        fullscreenContent = view.findViewById(R.id.fullscreen_content)
        fullscreenContentControls = view.findViewById(R.id.fullscreen_content_controls)
        // Set up the user interaction to manually show or hide the system UI.
        fullscreenDummyContent?.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        btnCancel?.setOnTouchListener(delayHideTouchListener)
        btnCancel?.setOnClickListener {
            findNavController().popBackStack(R.id.ItemDetailsFragment, false)
        }

        arguments?.getString("pictureName")?.let {
            fullscreenContent?.apply {
                val imageFile = Utils.loadImageFile(requireContext(), it).let {
                    val metrics = resources.displayMetrics
                    val width = metrics.widthPixels.toFloat()
                    val height = metrics.heightPixels.toFloat()
                    Utils.resizeBitmap(it, width, height)
                }
                setImageBitmap(imageFile)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        btnCancel = null
        fullscreenDummyContent = null
        fullscreenContentControls = null
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        fullscreenContentControls?.visibility = View.GONE
        visible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
        fullscreenDummyContent?.fitsSystemWindows = true
        visible = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
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