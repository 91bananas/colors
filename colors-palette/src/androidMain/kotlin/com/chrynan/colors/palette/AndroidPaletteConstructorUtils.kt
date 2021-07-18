@file:Suppress("unused")

package com.chrynan.colors.palette

import android.graphics.Bitmap
import android.graphics.Rect
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

@ExperimentalUnsignedTypes
suspend fun Palette.Companion.generate(bitmap: Bitmap, maxColorCount: Int = 16): Palette {
    val scaledBitmap = scaleBitmapDown(bitmap = bitmap)

    val pixels = getPixelsFromBitmap(bitmap = scaledBitmap)

    if (scaledBitmap != bitmap) {
        scaledBitmap.recycle()
    }

    return Palette.generate(pixels = pixels, maxColorCount = maxColorCount)
}

private fun getPixelsFromBitmap(bitmap: Bitmap, region: Rect? = null): IntArray {
    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height
    val pixels = IntArray(bitmapWidth * bitmapHeight)

    bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)

    return if (region == null) {
        // If we don't have a region, return all of the pixels
        pixels
    } else {
        // If we do have a region, lets create a subset array containing only the region's
        // pixels
        val regionWidth = region.width()
        val regionHeight = region.height()

        // pixels contains all of the pixels, so we need to iterate through each row and
        // copy the regions pixels into a new smaller array
        val subsetPixels = IntArray(regionWidth * regionHeight)

        for (row in 0 until regionHeight) {
            System.arraycopy(
                pixels,
                (row + region.top) * bitmapWidth + region.left,
                subsetPixels,
                row * regionWidth,
                regionWidth
            )
        }

        subsetPixels
    }
}

private fun scaleBitmapDown(
    bitmap: Bitmap,
    resizeArea: Int = 112 * 112,
    resizeMaxDimension: Int = -1
): Bitmap {
    var scaleRatio = -1.0

    if (resizeArea > 0) {
        val bitmapArea = bitmap.width * bitmap.height

        if (bitmapArea > resizeArea) {
            scaleRatio = sqrt(resizeArea / bitmapArea.toDouble())
        }
    } else if (resizeMaxDimension > 0) {
        val maxDimension = max(bitmap.width, bitmap.height)

        if (maxDimension > resizeMaxDimension) {
            scaleRatio = resizeMaxDimension / maxDimension.toDouble()
        }
    }

    return if (scaleRatio <= 0) {
        // Scaling has been disabled or not needed so just return the Bitmap
        bitmap
    } else Bitmap.createScaledBitmap(
        bitmap,
        ceil(bitmap.width * scaleRatio).toInt(),
        ceil(bitmap.height * scaleRatio).toInt(),
        false
    )
}
