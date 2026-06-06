package com.example.clikeys

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clikeys.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CliKeys"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d(TAG, "onCreate: 开始")
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            updateButtonStates()
            Log.d(TAG, "onCreate: setContentView 完成")

            // 步骤 1：启用输入法
            binding.btnEnableIme.setOnClickListener {
                Log.d(TAG, "btnEnableIme clicked")
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                Toast.makeText(this, "请在列表中勾选 CliKeys", Toast.LENGTH_LONG).show()
            }

            // 步骤 2：切换键盘
            binding.btnSwitchKeyboard.setOnClickListener {
                Log.d(TAG, "btnSwitchKeyboard clicked")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }

            // GitHub 链接 - 使用系统浏览器选择器
            binding.tvGithub.setOnClickListener {
                Log.d(TAG, "tvGithub clicked")
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Map9876/android-ctrl-esc-keyboard-app"))
                    val chooser = Intent.createChooser(intent, "请选择打开链接的浏览器")
                    startActivity(chooser)
                } catch (e1: Exception) {
                    Log.e(TAG, "Failed to open GitHub: ${e1.message}", e1)
                    Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show()
                }
            }

            Log.d(TAG, "onCreate: 完成")

        } catch (e: Exception) {
            Log.e(TAG, "onCreate 崩溃: ${e.message}", e)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        updateButtonStates()
    }

    private fun updateButtonStates() {
        try {
            // 检查输入法是否已启用
            val imeEnabled = isImeEnabled()
            binding.btnEnableIme.text = if (imeEnabled) "✓ 输入法已启用" else "启用输入法"

            // 更新切换键盘按钮状态
            binding.btnSwitchKeyboard.isEnabled = imeEnabled
            binding.btnSwitchKeyboard.text = if (imeEnabled) "切换键盘" else "请先启用输入法"
        } catch (e: Exception) {
            Log.e(TAG, "updateButtonStates error: ${e.message}", e)
        }
    }

    private fun isImeEnabled(): Boolean {
        val imeService = "$packageName/.CliInputMethodService"
        val enabledImes = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_INPUT_METHODS
        ) ?: return false
        return enabledImes.contains(imeService)
    }
}
