package com.example.clikeys

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.example.clikeys.databinding.FloatingPanelBinding

/**
 * 悬浮窗服务
 * 提供常驻悬浮按钮，点击展开修饰键面板（Esc、Ctrl、Alt 等）
 */
class FloatingWindowService : Service() {

    companion object {
        const val CHANNEL_ID = "clikeys_floating"
        const val NOTIF_ID = 1001
        var isCtrlMode = false
        var isAltMode = false

        fun updateCtrlState(ctrl: Boolean) {
            isCtrlMode = ctrl
            instance?.updateCtrlButton()
        }

        var instance: FloatingWindowService? = null
    }

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var panelView: View? = null
    private var isPanelShowing = false
    private var panelLayoutParams: WindowManager.LayoutParams? = null
    private var panelInitialX = 0
    private var panelInitialY = 0
    private var panelInitialTouchX = 0f
    private var panelInitialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Settings.canDrawOverlays(this)) {
            showFloatingButton()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CliKeys 悬浮窗",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "CliKeys 悬浮键服务通知"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CliKeys 悬浮键")
            .setContentText("点击通知栏切换悬浮窗")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun showFloatingButton() {
        if (floatingView != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // 悬浮按钮
        val button = Button(this).apply {
            text = "⎘"
            textSize = 18f
            setBackgroundColor(0xCC1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(8, 8, 8, 8)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 200
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(button, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = kotlin.math.abs(event.rawX - initialTouchX)
                    val dy = kotlin.math.abs(event.rawY - initialTouchY)
                    if (dx < 5 && dy < 5) {
                        togglePanel()
                    }
                    true
                }
                else -> false
            }
        }

        floatingView = button
        windowManager.addView(button, params)
    }

    private fun togglePanel() {
        if (isPanelShowing) {
            hidePanel()
        } else {
            showPanel()
        }
    }

    private fun showPanel() {
        if (panelView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val binding = FloatingPanelBinding.inflate(LayoutInflater.from(this))
        panelView = binding.root

        panelLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        // 设置拖动把手
        binding.panelDragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    panelInitialX = panelLayoutParams!!.x
                    panelInitialY = panelLayoutParams!!.y
                    panelInitialTouchX = event.rawX
                    panelInitialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    panelLayoutParams!!.x = panelInitialX + (event.rawX - panelInitialTouchX).toInt()
                    panelLayoutParams!!.y = panelInitialY + (event.rawY - panelInitialTouchY).toInt()
                    windowManager.updateViewLayout(panelView, panelLayoutParams)
                    true
                }
                else -> false
            }
        }

        // 设置按键监听
        binding.panelEsc.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_ESCAPE) }
        binding.panelCtrl.setOnClickListener {
            isCtrlMode = !isCtrlMode
            updateCtrlButton()
        }
        binding.panelAlt.setOnClickListener {
            isAltMode = !isAltMode
            updateCtrlButton()
        }
        binding.panelTab.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_TAB) }
        // 方向键
        binding.panelUp.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_DPAD_UP) }
        binding.panelDown.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_DPAD_DOWN) }
        binding.panelLeft.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_DPAD_LEFT) }
        binding.panelRight.setOnClickListener { sendKeyToIme(KeyEvent.KEYCODE_DPAD_RIGHT) }

        // 测试键：A
        binding.panelA.setOnClickListener {
            val ime = CliInputMethodService.instance
            if (ime != null) {
                val ic = ime.currentInputConnection
                ic?.commitText("A", 1)
            }
        }

        // 关闭面板按钮
        binding.panelClose.setOnClickListener { hidePanel() }

        // 切换键盘按钮
        binding.panelSwitchIme.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showInputMethodPicker()
        }

        updateCtrlButton()

        windowManager.addView(panelView, panelLayoutParams)
        isPanelShowing = true
    }

    private fun hidePanel() {
        panelView?.let {
            windowManager.removeView(it)
            panelView = null
        }
        isPanelShowing = false
    }

    fun updateCtrlButton() {
        val binding = panelView?.let { FloatingPanelBinding.bind(it) }
        binding?.panelCtrl?.setBackgroundResource(
            if (isCtrlMode) R.drawable.key_bg_ctrl_active else R.drawable.key_bg
        )
        binding?.panelAlt?.setBackgroundResource(
            if (isAltMode) R.drawable.key_bg_ctrl_active else R.drawable.key_bg
        )
    }

    /**
     * 通过 IME 发送按键（带当前 Ctrl/Alt 状态）
     */
    private fun sendKeyToIme(keyCode: Int) {
        val ime = CliInputMethodService.instance
        if (ime != null) {
            ime.sendModifiedKey(keyCode, isCtrl = isCtrlMode, isAlt = isAltMode)
            // 发送后重置一次性修饰键
            if (isCtrlMode || isAltMode) {
                isCtrlMode = false
                isAltMode = false
                updateCtrlButton()
            }
        }
    }

    private fun sendKeyToImeWithMeta(keyCode: Int, metaCtrl: Boolean = false, metaAlt: Boolean = false) {
        val ime = CliInputMethodService.instance
        if (ime != null) {
            ime.sendModifiedKey(keyCode, isCtrl = metaCtrl, isAlt = metaAlt)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager.removeView(it) }
        panelView?.let { windowManager.removeView(it) }
        instance = null
    }
}
