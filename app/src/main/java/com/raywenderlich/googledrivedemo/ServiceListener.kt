package com.raywenderlich.googledrivedemo

import java.io.File

/**
 * Copyright (c) 2018 Razeware LLC
 * Listeners of Services will get notified when the file has been downloaded, will get a list of
 * files to display and will be notified that there was an error
 */
interface ServiceListener {
  fun fileDownloaded(file: File)
  fun cancelled()
  fun handleError(exception: Exception)
}