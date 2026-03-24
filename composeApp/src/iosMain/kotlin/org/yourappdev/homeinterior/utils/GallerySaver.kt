package org.yourappdev.homeinterior.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
actual suspend fun saveImageToGallery(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String
): Boolean {
    return try {
        val uiImage: UIImage? = when {
            imageBytes != null && imageBytes.isNotEmpty() -> {
                imageBytes.usePinned { pinned ->
                    val nsData = NSData.dataWithBytes(
                        pinned.addressOf(0),
                        imageBytes.size.toULong()
                    )
                    UIImage(data = nsData)
                }
            }
            imageUrl != null && imageUrl.startsWith("/") -> {
                val url = NSURL.fileURLWithPath(imageUrl)
                val data = NSData.dataWithContentsOfURL(url) ?: return false
                UIImage(data = data)
            }
            imageUrl != null -> {
                val url = NSURL.URLWithString(imageUrl) ?: return false
                val data = NSData.dataWithContentsOfURL(url) ?: return false
                UIImage(data = data)
            }
            else -> return false
        }

        uiImage ?: return false

        UIImageWriteToSavedPhotosAlbum(uiImage, null, null, null)

        // ✅ iOS ko thoda time do save karne ka
        kotlinx.coroutines.delay(500)
        true  // ✅ Assume success

    } catch (e: Exception) {
        println("❌ iOS Save failed: ${e.message}")
        false
    }

}