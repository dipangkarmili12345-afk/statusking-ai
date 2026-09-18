package com.statusking.ai.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.statusking.ai.data.local.SampleData
import com.statusking.ai.model.FontStyleType
import com.statusking.ai.model.PosterConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

sealed class ExportResult {
    data class Success(val uri: Uri, val filePath: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}

class ExportManager(private val context: Context) {

    suspend fun exportPosterBitmap(config: PosterConfig, isPremium: Boolean): Bitmap = withContext(Dispatchers.Default) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        var drewCustomImage = false
        if (!config.imageUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(config.imageUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val sourceBitmap = android.graphics.BitmapFactory.decodeStream(stream)
                    if (sourceBitmap != null) {
                        drawCenterCropBitmap(canvas, sourceBitmap, width, height)
                        // Add stylish dark overlay for text readability
                        val overlayPaint = Paint().apply {
                            color = Color.parseColor("#80000000")
                        }
                        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
                        drewCustomImage = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!drewCustomImage) {
            val gradientList = SampleData.GRADIENTS.getOrElse(config.gradientIndex) { SampleData.GRADIENTS[0] }
            val colors = gradientList.map { it.toInt() }.toIntArray()
            val gradient = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                colors, null, Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { shader = gradient }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // Draw subtle decorative ambient glow
            val glowPaint = Paint().apply {
                color = Color.parseColor("#15FFFFFF")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(width * 0.2f, height * 0.25f, 300f, glowPaint)
            canvas.drawCircle(width * 0.85f, height * 0.75f, 400f, glowPaint)
        }

        // 2. Draw Decorative Emoji / Sticker
        if (!config.emoji.isNullOrEmpty()) {
            val emojiPaint = Paint().apply {
                textSize = 90f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(config.emoji, width / 2f, height * 0.28f, emojiPaint)
        }

        // 3. Draw Main Status Text
        val typeface = when (config.fontIndex) {
            1 -> Typeface.SERIF
            2 -> Typeface.MONOSPACE
            3 -> Typeface.SANS_SERIF // or creative style
            else -> Typeface.SANS_SERIF
        }

        val style = when {
            config.isBold && config.isItalic -> Typeface.BOLD_ITALIC
            config.isBold -> Typeface.BOLD
            config.isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        val finalTypeface = Typeface.create(typeface, style)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.textColor.toInt()
            this.typeface = finalTypeface
            textSize = config.fontSizeSp * 2.8f
            setShadowLayer(16f, 4f, 6f, Color.parseColor("#99000000"))
        }

        val alignment = when (config.textAlign) {
            0 -> Layout.Alignment.ALIGN_NORMAL
            2 -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }

        val textWidth = (width * 0.82f).toInt()
        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(config.text, 0, config.text.length, textPaint, textWidth)
                .setAlignment(alignment)
                .setLineSpacing(12f, 1.25f)
                .setIncludePad(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                config.text, textPaint, textWidth,
                alignment, 1.25f, 12f, true
            )
        }

        canvas.save()
        // Center text vertically, offset by user drag/controls
        val textHeight = staticLayout.height
        val startY = (height - textHeight) / 2f + (config.textOffsetY * 2.5f)
        val startX = (width - textWidth) / 2f + (config.textOffsetX * 2.5f)

        if (config.rotationAngle != 0f) {
            canvas.rotate(config.rotationAngle, width / 2f, height / 2f)
        }

        canvas.translate(startX, startY)
        staticLayout.draw(canvas)
        canvas.restore()

        // 4. Draw StatusKing AI Watermark (unless premium user or toggled off with premium)
        val shouldShowWatermark = config.watermarkEnabled && !isPremium
        if (shouldShowWatermark) {
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#99000000")
                this.style = Paint.Style.FILL
            }
            val watermarkText = "👑 StatusKing AI"
            val watermarkPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 34f
                this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val pillRect = RectF(
                width / 2f - 220f,
                height - 180f,
                width / 2f + 220f,
                height - 100f
            )
            canvas.drawRoundRect(pillRect, 40f, 40f, pillPaint)
            canvas.drawText(watermarkText, width / 2f, height - 130f, watermarkPaint)
        }

        bitmap
    }

    private fun drawCenterCropBitmap(canvas: Canvas, source: Bitmap, targetWidth: Int, targetHeight: Int) {
        val srcWidth = source.width
        val srcHeight = source.height

        val srcRatio = srcWidth.toFloat() / srcHeight.toFloat()
        val targetRatio = targetWidth.toFloat() / targetHeight.toFloat()

        val srcRect: Rect = if (srcRatio > targetRatio) {
            // Source is wider, crop horizontal
            val newWidth = (srcHeight * targetRatio).toInt()
            val left = (srcWidth - newWidth) / 2
            Rect(left, 0, left + newWidth, srcHeight)
        } else {
            // Source is taller, crop vertical
            val newHeight = (srcWidth / targetRatio).toInt()
            val top = (srcHeight - newHeight) / 2
            Rect(0, top, srcWidth, top + newHeight)
        }

        val dstRect = Rect(0, 0, targetWidth, targetHeight)
        canvas.drawBitmap(source, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
    }

    suspend fun saveToGallery(bitmap: Bitmap, title: String = "StatusKing_${System.currentTimeMillis()}"): ExportResult = withContext(Dispatchers.IO) {
        try {
            val filename = "${title}.png"
            var fos: OutputStream? = null
            var imageUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/StatusKingAI")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    imageUri = uri
                    fos = resolver.openOutputStream(uri)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos!!)
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/StatusKingAI"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                imageUri = Uri.fromFile(image)
            }

            fos?.flush()
            fos?.close()

            if (imageUri != null) {
                ExportResult.Success(imageUri, "Pictures/StatusKingAI/$filename")
            } else {
                ExportResult.Error("Could not create image file")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ExportResult.Error("Save failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun shareStatus(bitmap: Bitmap, text: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "statusking_share_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "$text\n\n✨ Created via StatusKing AI")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Status to WhatsApp, Instagram & more").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
