package com.example.nibbletest.other

import android.content.ContentResolver
import android.net.Uri
import java.util.Locale

fun Long.formatMilliseconds(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

fun Int.resourceToUri(): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .path(this.toString())
        .build()
}