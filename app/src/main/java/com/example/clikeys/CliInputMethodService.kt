package com.example.clikeys

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.example.clikeys.databinding.InputViewBinding

/**
 * CliKeys 输入法服务
 * 提供 Esc、Ctrl、Alt、方向键等修饰键，供终端/CLI 使用
 */
class CliInputMethodService : InputMethodService() {

    private lateinit var binding: InputViewBinding

    // 修饰键状态
    private var isCtrlMode = false
    private var isAltMode = false
    private var isShiftMode = false

    override fun onCreateInputView(): View {
        binding = InputViewBinding.inflate(layoutInflater)
        setupKeys()
        updateKeyHighlights()
        return binding.root
    }

    private fun setupKeys() {
        // Esc
        binding.keyEsc.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_ESCAPE) }

        // Tab
        binding.keyTab.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_TAB) }

        // Ctrl 切换
        binding.keyCtrl.setOnClickListener {
            isCtrlMode = !isCtrlMode
            if (isCtrlMode) {
                isAltMode = false
                isShiftMode = false
            }
            updateKeyHighlights()
        }

        // Alt 切换
        binding.keyAlt.setOnClickListener {
            isAltMode = !isAltMode
            if (isAltMode) {
                isCtrlMode = false
                isShiftMode = false
            }
            updateKeyHighlights()
        }

        // Shift 切换
        binding.keyShift.setOnClickListener {
            isShiftMode = !isShiftMode
            if (isShiftMode) {
                isCtrlMode = false
                isAltMode = false
            }
            updateKeyHighlights()
        }

        // Home / End / PgUp / PgDn
        binding.keyHome.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_MOVE_HOME) }
        binding.keyEnd.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_MOVE_END) }
        binding.keyPgup.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_PAGE_UP) }
        binding.keyPgdn.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_PAGE_DOWN) }

        // 方向键
        binding.keyUp.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_DPAD_UP) }
        binding.keyDown.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN) }
        binding.keyLeft.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_DPAD_LEFT) }
        binding.keyRight.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT) }

        // 字母键 QWERTY 第一行
        binding.keyQ.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_Q) }
        binding.keyW.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_W) }
        binding.keyE.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_E) }
        binding.keyR.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_R) }
        binding.keyT.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_T) }
        binding.keyY.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_Y) }
        binding.keyU.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_U) }
        binding.keyI.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_I) }
        binding.keyO.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_O) }
        binding.keyP.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_P) }

        // 字母键 QWERTY 第二行
        binding.keyA.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_A) }
        binding.keyS.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_S) }
        binding.keyD.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_D) }
        binding.keyF.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_F) }
        binding.keyG.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_G) }
        binding.keyH.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_H) }
        binding.keyJ.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_J) }
        binding.keyK.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_K) }
        binding.keyL.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_L) }

        // 字母键 QWERTY 第三行
        binding.keyZ.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_Z) }
        binding.keyX.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_X) }
        binding.keyC.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_C) }
        binding.keyV.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_V) }
        binding.keyB.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_B) }
        binding.keyN.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_N) }
        binding.keyM.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_M) }

        // 切换键盘
        binding.keySwitch.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }

    private fun updateKeyHighlights() {
        if (!::binding.isInitialized) return

        binding.keyCtrl.setBackgroundResource(
            if (isCtrlMode) R.drawable.key_bg_ctrl_active else R.drawable.key_bg
        )
        binding.keyAlt.setBackgroundResource(
            if (isAltMode) R.drawable.key_bg_ctrl_active else R.drawable.key_bg
        )
        binding.keyShift.setBackgroundResource(
            if (isShiftMode) R.drawable.key_bg_ctrl_active else R.drawable.key_bg
        )
    }

    /**
     * 发送按键码到当前输入框
     */
    private fun sendKeyCode(keyCode: Int) {
        val ic = currentInputConnection ?: return
        var metaState = 0
        if (isCtrlMode) metaState = metaState or KeyEvent.META_CTRL_ON
        if (isAltMode) metaState = metaState or KeyEvent.META_ALT_ON
        if (isShiftMode) metaState = metaState or KeyEvent.META_SHIFT_ON

        val down = KeyEvent(0L, 0L, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
        val up = KeyEvent(0L, 0L, KeyEvent.ACTION_UP, keyCode, 0, metaState)
        ic.sendKeyEvent(down)
        ic.sendKeyEvent(up)

        // Ctrl/Alt 是一次性修饰键，发送后自动取消
        if (isCtrlMode || isAltMode) {
            isCtrlMode = false
            isAltMode = false
            updateKeyHighlights()
        }
    }

    /**
     * 发送带修饰符的按键（如 Ctrl+C）
     * 供悬浮窗调用
     */
    fun sendModifiedKey(keyCode: Int, isCtrl: Boolean = false, isAlt: Boolean = false) {
        val ic = currentInputConnection ?: return
        var metaState = 0
        if (isCtrl || isCtrlMode) metaState = metaState or KeyEvent.META_CTRL_ON
        if (isAlt || isAltMode) metaState = metaState or KeyEvent.META_ALT_ON
        if (isShiftMode) metaState = metaState or KeyEvent.META_SHIFT_ON

        val down = KeyEvent(0L, 0L, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
        val up = KeyEvent(0L, 0L, KeyEvent.ACTION_UP, keyCode, 0, metaState)
        ic.sendKeyEvent(down)
        ic.sendKeyEvent(up)
    }

    override fun onStartInputView(editorInfo: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        // 每次进入输入框时重置修饰键状态
        isCtrlMode = false
        isAltMode = false
        isShiftMode = false
        updateKeyHighlights()
    }

    companion object {
        var instance: CliInputMethodService? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
