# CliKeys - Android Ctrl/Esc 键盘

手机使用ctrl和Esc键的键盘app。主要用于解决以下问题:

1. 比如在cloudflare workers在线网页代码编辑器上全选代码；
2. 还有cnb.cool上的vscode code server网页版里安装codebuddy cli这种交互式命令行ai agent，使用/config时候会需要用户按Esc键退出的问题，还有在各种网页版 shell / terminal等等需要tab补全代码的问题。

主要用于应急使用，因为手机只能选择一个键盘使用，该键盘样式比较丑 。

Using Ctrl and Esc keys

## 应用截图

### App 主页

![App 主页](docs-image/Screenshot_20260607_040250_com_example_clikeys_MainActivity.jpg)

### 键盘页面

![键盘页面](docs-image/Screenshot_20260607_033658_com_tencent_mm_MMWebViewUI.jpg)

---

## 功能特性

- **Ctrl 键**：切换模式，按下后高亮，此时按字母键即发送 Ctrl+字母（如 Ctrl+C、Ctrl+V、Ctrl+A 等）
- **Esc 键**：发送 Escape 键
- **Alt 键**：切换模式，配合字母键发送 Alt+字母
- **Shift 键**：切换模式，配合字母键发送大写字母
- **Tab 键**：发送 Tab 键（用于命令行补全）
- **方向键**：上、下、左、右
- **Home/End/PgUp/PgDn**：光标移动键
- **完整 QWERTY 键盘**：支持 26 个字母键输入

## 安装说明

1. 从 [Releases](https://github.com/Map9876/android-ctrl-esc-keyboard-app/releases) 下载最新 APK 文件
2. 在手机上安装 APK（需要允许安装未知来源应用）
3. 打开 App，按照步骤操作：
   - **步骤 1**：启用 CliKeys 输入法（设置 → 语言和输入法 → 勾选 CliKeys）
   - **步骤 2**：切换键盘（点击按钮调出系统输入法选择器）

**注意**：荣耀手机用户启用输入法后需要重启手机才能使用。

## 使用方法

1. 打开任意输入框（如浏览器搜索框、终端应用等）
2. 点击屏幕底部导航栏最右侧的"键盘"图标
3. 选择"CliKeys"
4. 输入法从屏幕底部弹出，显示完整键盘

~~### 悬浮窗模式（可选）~~

~~1. 在 App 主页点击"允许悬浮窗"授予权限~~
~~2. 点击"启动悬浮键"启动悬浮窗~~
~~3. 悬浮窗显示在屏幕右上角，点击展开修饰键面板~~
~~4. 按住面板顶部灰色横条可拖动面板位置~~

> **注**：悬浮窗模式已移除。因为手机同时只能使用一个输入法，悬浮窗无法与输入法同时使用，因此该功能已删除。

## 使用场景

### 场景 1：Cloudflare Workers 在线代码编辑器

在手机浏览器上使用 Cloudflare Workers 的在线代码编辑器时，需要全选代码（Ctrl+A）进行复制或编辑。使用 CliKeys 输入法，按下 Ctrl 键后按 A 键即可发送 Ctrl+A 全选命令。

### 场景 2：CNB.cool VS Code Code Server

在 cnb.cool 上的 VS Code Code Server 网页版里安装 CodeBuddy CLI 这种交互式命令行 AI Agent，使用 `/config` 命令时会需要用户按 Esc 键退出。使用 CliKeys 输入法，直接点击 Esc 按钮即可。

### 场景 3：网页版 Shell/Terminal

在各种网页版 Shell/Terminal 中，需要 Tab 键补全代码。使用 CliKeys 输入法，点击 Tab 按钮即可触发命令补全。

## 项目主页

GitHub: https://github.com/Map9876/android-ctrl-esc-keyboard-app

---

## AI 开发提示词

说明: 以下是ai开发的prompt提示词:

```
创建一个 Android 输入法应用，名为 CliKeys，用于提供 Ctrl、Esc、Alt、Shift 等修饰键功能，主要用于手机上的网页版终端/代码编辑器。

主要功能需求：
1. 作为 Android 输入法（InputMethodService）实现，可以从系统键盘选择器切换使用
2. 提供完整的修饰键支持：Ctrl、Esc、Alt、Shift、Tab
3. 提供方向键：上、下、左、右
4. 提供 Home、End、PgUp、PgDn 光标移动键
5. Ctrl/Alt 为切换模式：按下后高亮，此时按字母键发送 Ctrl+字母/Alt+字母
6. 提供完整 QWERTY 字母键盘，支持 26 个字母输入
7. 悬浮窗模式：可选的悬浮按钮，点击展开修饰键面板
8. 悬浮窗可拖动，通过顶部拖动把手控制位置

UI 设计要求：
- 白色主题，浅色背景
- 按钮背景为白色，文字为深灰色
- 按动按钮时有颜色变化反馈（按下变灰）
- 活跃修饰键（Ctrl/Alt/Shift）按下后显示浅蓝色背景
- 键盘背景为纯白色

技术实现要点：
- 使用 Kotlin 语言开发
- 使用 ViewBinding 进行视图绑定
- 支持 Android 7.0+（API 24+）
- 前台服务实现悬浮窗功能
- 系统悬浮窗权限（SYSTEM_ALERT_WINDOW）

使用场景：
- Cloudflare Workers 在线代码编辑器全选代码（Ctrl+A）
- CNB.cool VS Code Code Server 网页版中使用 CodeBuddy CLI 时按 Esc 退出
- 各种网页版 Shell/Terminal 中使用 Tab 补全命令

目标仓库：github.com/map9876/android-ctrl-esc-keyboard
```

---

## 许可证

本项目为开源项目，欢迎使用和修改。
