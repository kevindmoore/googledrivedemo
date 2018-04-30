package com.raywenderlich.googledrivedemo

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.webkit.MimeTypeMap
import android.widget.Button
import java.io.File


class MainActivity : AppCompatActivity(), ServiceListener {
  lateinit var googleDriveService: GoogleDriveService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    googleDriveService = GoogleDriveService(this, GoogleDriveConfig(getString(R.string.source_google_drive), GoogleDriveService.documentMimeTypes))
    googleDriveService.serviceListener = this
    findViewById<Button>(R.id.start).setOnClickListener {
      googleDriveService.auth()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    googleDriveService.onActivityResult(requestCode, resultCode, data)
  }


  override fun fileDownloaded(file: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    val apkURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file)
    val uri = Uri.fromFile(file)
    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    intent.setDataAndType(apkURI, mimeType)
    intent.flags = FLAG_GRANT_READ_URI_PERMISSION
    if (intent.resolveActivity(packageManager) != null) {
      startActivity(intent)
    }
  }

  override fun cancelled() {
  }

  override fun handleError(exception: Exception) {
  }

}
