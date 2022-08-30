package com.wavecat.kotovstore

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class InstallWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val fileUrl = inputData.getString(URL)

        val filePath =
            applicationContext.getExternalFilesDir("/cache")!!.absolutePath + "/" + fileUrl.hashCode()

        val connection = URL(fileUrl).openConnection() as HttpURLConnection
        connection.connect()

        val fileLength = connection.contentLength

        val input = connection.inputStream
        val output = FileOutputStream(filePath, false)

        val data = ByteArray(4096)
        var total = 0
        var count: Int

        var lastProgress = 0
        while ((input.read(data).also { count = it }) != -1) {
            total += count
            if (isStopped)
                return Result.success()
            if (fileLength > 0) {
                val progress: Int = total * 100 / fileLength
                if (progress != lastProgress)
                    setProgressAsync(
                        workDataOf(
                            PROGRESS to progress
                        )
                    )
                lastProgress = progress
            }
            output.write(data, 0, count)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                applicationContext,
                BuildConfig.APPLICATION_ID + ".provider",
                File(filePath)
            ),
            "application/vnd.android.package-archive"
        )

        applicationContext.startActivity(intent)

        return Result.success()
    }

    companion object {
        const val URL = "url"
        const val PROGRESS = "progress"
    }
}