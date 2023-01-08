package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ContentMainBinding
import com.udacity.notification.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadingButton: LoadingButton
    private var selectedFileName = ""
    private var selectedDownloader = ""
    private lateinit var binding: ContentMainBinding
    private var checkBox = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        notificationManager = this.getSystemService(
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.notification_channel_id), CHANNEL_ID
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        loadingButton = binding.customButton
        loadingButton.setLoadingButtonState(ButtonState.Completed)

        binding.radioGroup.setOnCheckedChangeListener { _, checked_id ->
            checkBox = true
            when (checked_id) {
                R.id.glideBtn -> {
                    selectedDownloader = getString(R.string.glideGithubURL)
                    selectedFileName = getString(R.string.Glide)
                }
                R.id.udacityBtn -> {
                    selectedDownloader = getString(R.string.loadAppGithubURL)
                    selectedFileName = getString(R.string.udacity)
                }
                R.id.retrofitBtn -> {
                    selectedDownloader = getString(R.string.retrofitGithubURL)
                    selectedFileName = getString(R.string.retrofit)
                }
                else -> checkBox = false
            }
        }
        custom_button.setOnClickListener {
            if (checkBox) {
                download()
                registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            } else {
                makeToast()
            }

        }
    }

    private fun makeToast() {
        Toast.makeText(
            this, "Please select the file to download", Toast.LENGTH_SHORT
        ).show()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent!!.action
            if (downloadID == id) {
                if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val manager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    selectedFileName,
                                    applicationContext,
                                    "Success"
                                )
                            } else {
                                loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    selectedFileName,
                                    applicationContext,
                                    "Failure"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        loadingButton.setLoadingButtonState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Downloaded"
            }
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
}
