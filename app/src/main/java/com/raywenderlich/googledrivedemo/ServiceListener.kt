package com.raywenderlich.googledrivedemo

import com.google.android.gms.drive.DriveId
import java.io.File

/**
 * Listeners of Services will get notified when the file has been downloaded, will get a list of
 * files to display and will be notified that there was an error
 */
interface ServiceListener {
    fun currentFiles(currentPath : String, files : List<DriveId>)
    fun fileDownloaded(file : File)
    fun cancelled()
    fun handleError(exception : Exception)
}