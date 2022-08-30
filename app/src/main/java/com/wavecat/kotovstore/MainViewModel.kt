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

    var currentApp: MutableLiveData<App> = MutableLiveData()
    var currentInfo: MutableLiveData<Info> = MutableLiveData()

    fun selectApp(app: App) {
        currentApp.postValue(app)

        database.child("info").child(app.id.toString()).get()
            .addOnSuccessListener {
                currentInfo.postValue(it.getValue<Info>())
            }
    }

    fun installApp() {
        if (currentInfo.value == null) return

        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<InstallWorker>()
                .addTag(currentApp.value!!.packageName)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    workDataOf(
                        InstallWorker.URL to currentInfo.value!!.apk
                    )
                )
                .build()

        workManager.enqueue(workRequest)
    }

    fun loadApps() {
        database.child("apps").get()
            .addOnSuccessListener {
                apps.postValue(it.getValue<List<App>>())
            }
    }
}