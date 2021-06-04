/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.frame

import android.annotation.SuppressLint
import android.app.Application
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.microsoft.office.outlook.magnifierlib.R

@SuppressLint("ClickableViewAccessibility")
class FrameViewer(
    context: Application,
    private val refreshRate: Float,
    private val mediumPercentage: Float,
    private val lowPercentage: Float
) {
    @SuppressLint("InflateParams")
    private val textView: TextView = LayoutInflater.from(context).inflate(R.layout.frame_view, null) as TextView
    private val windowManager: WindowManager = textView.context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    init {

        val minWidth: Int = (textView.lineHeight + textView.totalPaddingTop + textView.totalPaddingBottom + textView.paint.fontMetrics.bottom.toInt())
        textView.minWidth = minWidth

        val params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
        )
        params.gravity = DEFAULT_GRAVITY
        params.x = POSITION_X
        params.y = POSITION_L

        windowManager.addView(textView, params)

        textView.setOnTouchListener(MovingTouchListener(params, windowManager))

        textView.isHapticFeedbackEnabled = false
    }

    fun display(frameCount: Int) {
        textView.post {
            when {
                frameCount > refreshRate * mediumPercentage -> {
                    textView.setBackgroundResource(R.drawable.fps_good)
                }
                frameCount > refreshRate * lowPercentage -> {
                    textView.setBackgroundResource(R.drawable.fps_medium)
                }
                else -> {
                    textView.setBackgroundResource(R.drawable.fps_bad)
                }
            }
            textView.text = frameCount.toString()
        }
    }

    fun show() {
        textView.visibility = View.VISIBLE
    }

    fun hide() {
        textView.visibility = View.GONE
        windowManager.removeView(textView)
    }

    class MovingTouchListener(
        private val params: WindowManager.LayoutParams,
        private val windowManager: WindowManager
    ) : OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        override fun onTouch(
            v: View,
            event: MotionEvent
        ): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(v, params)
                }
            }
            return false
        }
    }

    companion object {
        const val DEFAULT_GRAVITY = Gravity.TOP or Gravity.START
        const val POSITION_X = 200
        const val POSITION_L = 600
    }
}