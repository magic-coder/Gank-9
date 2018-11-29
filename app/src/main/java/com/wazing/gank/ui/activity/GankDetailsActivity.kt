package com.wazing.gank.ui.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.wazing.gank.R
import com.wazing.gank.bean.Gank
import com.wazing.gank.utils.checkSelfPermission
import com.wazing.gank.utils.interceptBitmap
import com.wazing.gank.utils.saveImage
import kotlinx.android.synthetic.main.activity_gank_details.*
import org.jetbrains.anko.toast

class GankDetailsActivity : AppCompatActivity() {

    companion object {
        private const val KEY_GANK = "gank"

        fun newInstance(context: Context, gank: Gank) =
                with(Intent(context, GankDetailsActivity::class.java)) {
                    putExtra(KEY_GANK, gank)
                }
    }

    private val gank: Gank by lazy { intent.getParcelableExtra(KEY_GANK) as Gank }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.enableSlowWholeDocumentDraw()
        setContentView(R.layout.activity_gank_details)
        initToolbar()
        initWebSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_share -> with(Intent(Intent.ACTION_SEND)) {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "分享")
            putExtra(Intent.EXTRA_TEXT, "${gank.desc}\n${gank.url}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(this, "分享"))
            return@with true
        }
        R.id.action_refresh -> {
            web_view.reload()
            true
        }
        R.id.action_copy_url -> {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = ClipData.newRawUri("Label", Uri.parse(gank.url))
            toast("链接已复制到剪切板")
            true
        }
        R.id.action_share_image -> {
            checkSelfPermission({
                web_view.interceptBitmap()?.saveImage(this, "${gank.desc}.jpg") {
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        FileProvider.getUriForFile(this,
                                "$packageName.fileprovider", it)
                    else Uri.fromFile(it)
                    val shareIntent = with(Intent(Intent.ACTION_SEND)) {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        Intent.createChooser(this, "长图分享")
                        return@with this
                    }
                    startActivity(shareIntent)
                }
            }, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 1)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {
            web_view.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        if (web_view != null) {
            web_view.settings.javaScriptEnabled = true
            web_view.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (web_view != null) {
            web_view.settings.javaScriptEnabled = false
            web_view.onPause()
        }
    }

    override fun onDestroy() {
        // 在Activity销毁WebView的时候,先让WebView加载null内容,然后移除WebView,再销毁WebView,最后置空
        with(web_view) {
            loadDataWithBaseURL(null, "", "text/html",
                    "utf-8", null)
            clearHistory()
            (this as ViewGroup).removeView(this)
            destroy()
        }
        super.onDestroy()
    }

    private fun initToolbar() {
        toolbar.title = gank.desc
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initWebSettings() {
        with(web_view.settings) {
            useWideViewPort = true               // 将图片调整到适合webView的大小
            loadWithOverviewMode = true          // 缩放至屏幕的大小
            setSupportZoom(true)                 // 支持缩放，默认为true 是下面那个的前提
            builtInZoomControls = true           // 设置内置的缩放控件。若为false，则该WebView不可缩放
            displayZoomControls = false          // 隐藏原生的缩放控件
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // 关闭webView中缓存
            allowFileAccess = true                          // 设置可以访问文件
            javaScriptCanOpenWindowsAutomatically = true    // 支持通过JS打开新窗口
            loadsImagesAutomatically = true                 // 支持自动加载图片
            defaultTextEncodingName = "UTF-8"               // 设置编码格式
        }

        with(web_view) {
            loadUrl(gank.url)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    view.loadUrl(request.url.toString())
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    progress_bar.progress = newProgress
                    progress_bar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
                }

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    toolbar.title = title
                }
            }
        }
        with(progress_bar) {
            progress = 0
            max = 100
            visibility = View.VISIBLE
        }
    }

}