package com.wavecat.kotovstore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.wavecat.kotovstore.models.App
import com.wavecat.kotovstore.models.Info

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val workManager: WorkManager = WorkManager.getInstance(application)

    private val database: DatabaseReference by lazy {
        Firebase.database("https://kotovstore-e0ac2-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }

    val apps: MutableLiveData<List<App>> by lazy {
        MutableLiveData<List<App>>().also {
            loadApps()
        }
    }

    class DetailedInfo(val app: App, val info: Info)

    var currentApp: MutableLiveData<DetailedInfo?> = MutableLiveData()

    fun selectApp(app: App) {
        database.child("info").child(app.id.toString()).get()
            .addOnSuccessListener {
                currentApp.value = DetailedInfo(app, it.getValue<Info>()!!)
            }
    }

    fun unselectApp() {
        currentApp.value = null
    }

    fun installApp() {
        if (currentApp.value == null) return

        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<InstallWorker>()
                .addTag(currentApp.value!!.info.apk)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    workDataOf(
                        InstallWorker.URL to currentApp.value!!.info.apk
                    )
                )
                .build()

        workManager.enqueue(workRequest)
    }

    fun loadApps() {
        database.child("apps").get()
            .addOnSuccessListener {
                apps.value = it.getValue<List<App>>()
            }
    }
}