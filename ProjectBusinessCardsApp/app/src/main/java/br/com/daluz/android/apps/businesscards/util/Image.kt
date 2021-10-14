package br.com.daluz.android.apps.businesscards.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import br.com.daluz.android.apps.businesscards.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class Image {
    companion object {

        fun share(context: Context, view: View) {
            val bitmap = getScreenShotFromView(view)
            bitmap?.let {
                saveMediaToStorage(context, bitmap)
            }
        }

        private fun getScreenShotFromView(view: View): Bitmap? {
            var screenshot: Bitmap? = null
            try {
                screenshot =
                    Bitmap.createBitmap(
                        view.measuredWidth,
                        view.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                val canvas = Canvas(screenshot)
                view.draw(canvas)
            } catch (e: Exception) {
                Log.e("GFG", "Failed to capture screenshot because:" + e.message)
            }
            return screenshot
        }

        private fun saveMediaToStorage(context: Context, bitmap: Bitmap) {
            val fileName = "img${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            var imagePathUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    imagePathUri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    fos = imagePathUri?.let {
                        resolver.openOutputStream(it)
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                val imagesDirPath = File(
                    context.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES
                    ),
                    "images"
                )

                val imagesPath = File(imagesDirPath, fileName)

                imagePathUri = getUriForFile(
                    context,
                    "br.com.daluz.android.apps.businesscards.shareBusinessCardProvider",
                    imagesPath
                )

                fos = FileOutputStream(imagesDirPath)

            } else {
                // These for devices running on android < N
                val imagesDirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imagesPath = File(imagesDirPath, fileName)
                imagePathUri = Uri.fromFile(imagesPath)
                fos = FileOutputStream(imagesPath)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(
                    context,
                    context.getString(R.string.label_message_card_image_captured_sucessfull),
                    Toast.LENGTH_SHORT
                ).show()
            }

            shareIntent(context, imagePathUri)
            fos?.close()
        }

        private fun shareIntent(context: Context, imagePathUri: Uri?) {
            if (imagePathUri == null) return

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/jpeg"
                //setDataAndType(cardImageUri, "image/jpeg")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePathUri.toString()))
            }
            val pm: PackageManager = context.packageManager
            if (shareIntent.resolveActivity(pm) != null) {
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.resources.getText(R.string.label_share_to)
                    )
                )
            }
        }
    }
}