package com.plcoding.videoplayercompose.data

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.plcoding.videoplayercompose.domain.MetaData
import com.plcoding.videoplayercompose.domain.MetaDataReader

// Fetching Metadata from content Uri

class MetaDataReaderImpl(
    private val app: Application
): MetaDataReader {

    // Nedd to make it nullable because the user can pick from the file browser
    // not a Content Uri but picks a File Uri or Resource Uri
    override fun getMetaDataFromUri(contentUri: Uri): MetaData? {

        // we want only a Content Uri
        if(contentUri.scheme != "content") {
            return null
        }

        // to retrieve files we use Content Resolver
        val fileName = app.contentResolver
            // Using queries to search the file database (meta data about files)
            .query(
                contentUri,
                // Selecting a specific Column - File Name Field in this case
                arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )
            ?.use { cursor ->
                // through cursor we get the column index of the column we want to read
                val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                // Since we know we only have one result
                // With display name which is a String
                cursor.getString(index)
            }
        return fileName?.let { fullFileName ->
            // Here we get only the file name which we wanted
            MetaData(
                fileName = Uri.parse(fullFileName).lastPathSegment ?: return null
            )
        }
    }
}