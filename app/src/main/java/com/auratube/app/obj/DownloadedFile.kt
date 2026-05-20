package com.auratube.app.obj

import android.graphics.Bitmap
import com.auratube.app.api.obj.Streams

data class DownloadedFile(
    val name: String,
    val size: Long,
    var metadata: Streams? = null,
    var thumbnail: Bitmap? = null
)
