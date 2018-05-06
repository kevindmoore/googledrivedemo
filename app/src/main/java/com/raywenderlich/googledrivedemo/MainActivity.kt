package com.raywenderlich.googledrivedemo

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import java.io.File


/**
 * Copyright (c) 2018 Razeware LLC
 * Main Activity. Used to start Google Drive
 */
class MainActivity : AppCompatActivity(), ServiceListener {
  enum class ButtonState {
    LOGGED_OUT,
    LOGGED_IN
  }
  lateinit var googleDriveService: GoogleDriveService
  var state = ButtonState.LOGGED_OUT
  lateinit var loginButton : Button
  lateinit var logoutButton : Button
  lateinit var pickFilesButton : Button
  lateinit var statusView : TextView

  fun setButtons() {
    when (state) {
      ButtonState.LOGGED_OUT -> {
        statusView.text = getString(R.string.status_logged_out)
        pickFilesButton.isEnabled = false
        logoutButton.isEnabled = false
        loginButton.isEnabled = true
      }
      else -> {
        statusView.text = getString(R.string.status_logged_in)
        pickFilesButton.isEnabled = true
        logoutButton.isEnabled = true
        loginButton.isEnabled = false

      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    googleDriveService = GoogleDriveService(this, GoogleDriveConfig(getString(R.string.source_google_drive), GoogleDriveService.documentMimeTypes))
    googleDriveService.serviceListener = this
    statusView = findViewById(R.id.status)
    loginButton = findViewById(R.id.login)
    logoutButton = findViewById(R.id.logout)
    pickFilesButton = findViewById(R.id.start)
    loginButton.setOnClickListener {
      googleDriveService.auth()
      state = ButtonState.LOGGED_IN
      setButtons()
    }
    pickFilesButton.setOnClickListener {
      googleDriveService.pickFiles(null)
    }
    logoutButton.setOnClickListener {
      googleDriveService.logout()
      state = ButtonState.LOGGED_OUT
      setButtons()
    }
    setButtons()
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
    } else {
      Snackbar.make(findViewById(R.id.main_layout), R.string.not_open_file, Snackbar.LENGTH_LONG).show()
    }
  }

  override fun cancelled() {
    Snackbar.make(findViewById(R.id.main_layout), R.string.status_user_cancelled, Snackbar.LENGTH_LONG).show()
  }

  override fun handleError(exception: Exception) {
    Snackbar.make(findViewById(R.id.main_layout), R.string.status_error, Snackbar.LENGTH_LONG).show()
    state = ButtonState.LOGGED_OUT
    setButtons()
  }

}
