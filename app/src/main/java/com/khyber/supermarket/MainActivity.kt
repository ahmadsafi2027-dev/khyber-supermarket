package com.khyber.supermarket

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.ValueCallback
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val backupFile by lazy { File(filesDir, "khyber-auto-backup.json") }
    private val fileChooserRequest = 4102

    inner class BackupBridge {
        @JavascriptInterface
        fun saveBackup(json: String) {
            try {
                val temp = File(filesDir, "khyber-auto-backup.tmp")
                temp.writeText(json, Charsets.UTF_8)
                if (!temp.renameTo(backupFile)) {
                    backupFile.writeText(json, Charsets.UTF_8)
                    temp.delete()
                }
            } catch (_: Exception) { }
        }

        @JavascriptInterface
        fun loadBackup(): String = try {
            if (backupFile.exists()) backupFile.readText(Charsets.UTF_8) else ""
        } catch (_: Exception) { "" }

        @JavascriptInterface
        fun shareBackup(json: String) {
            runOnUiThread {
                try {
                    val dir = File(cacheDir, "backups").apply { mkdirs() }
                    val file = File(dir, "Khyber-Supermarket-Backup.json")
                    file.writeText(json, Charsets.UTF_8)
                    val uri = FileProvider.getUriForFile(
                        this@MainActivity,
                        "$packageName.fileprovider",
                        file
                    )
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_SUBJECT, "پشتیبان سوپرمارکت خیبر")
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(send, "ذخیره فایل پشتیبان"))
                } catch (_: Exception) {
                    Toast.makeText(this@MainActivity, "ساخت فایل پشتیبان انجام نشد", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.fileChooserCallback?.onReceiveValue(null)
                this@MainActivity.fileChooserCallback = filePathCallback
                return try {
                    val intent = fileChooserParams?.createIntent()
                        ?: Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                        }
                    startActivityForResult(intent, fileChooserRequest)
                    true
                } catch (_: Exception) {
                    this@MainActivity.fileChooserCallback = null
                    filePathCallback?.onReceiveValue(null)
                    false
                }
            }
        }
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(BackupBridge(), "KhyberBackup")
        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileChooserRequest) {
            val callback = fileChooserCallback ?: return
            val result = if (resultCode == RESULT_OK && data != null) {
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            } else null
            callback.onReceiveValue(result)
            fileChooserCallback = null
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}
