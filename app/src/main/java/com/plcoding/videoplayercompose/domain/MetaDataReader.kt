package com.plcoding.videoplayercompose.domain

import android.net.Uri

// Fetching Metadata from content Uri

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}
