package com.example.playlistmaker.player.ui.service

 import android.app.Notification
 import android.app.NotificationChannel
 import android.app.NotificationManager
 import android.app.Service
 import android.content.Intent
 import android.content.pm.ServiceInfo
 import android.media.MediaPlayer
import android.os.Binder
 import android.os.Build
 import android.os.IBinder
 import androidx.core.app.NotificationCompat
 import androidx.core.app.ServiceCompat
 import com.example.playlistmaker.R
 import com.example.playlistmaker.player.ui.result.PlayerState
 import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.flow.asStateFlow
 import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService : Service(), AudioPlayerControl {
    private companion object {
        const val UPDATE_TIME_INTERVAL = 300L
        private const val INTENT_TRACK_URL = "TRACK_URL"
        private const val INTENT_TRACK_NAME = "TRACK_NAME"
        private const val INTENT_ARTIST_NAME = "ARTIST_NAME"
        private const val SERVICE_NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "playlistmaker_music_channel"
    }

    private val binder = MusicServiceBinder()
    private var timerJob: Job? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackPosition = "00:00"
    private var mediaPlayer: MediaPlayer? = null
    private var trackUrl = ""
    private var trackName = ""
    private var artistName = ""
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.StateDefault())
    private val playerState = _playerState.asStateFlow()

    override fun onBind(p0: Intent?): IBinder? {
        trackUrl = p0?.getStringExtra(INTENT_TRACK_URL) ?: ""
        trackName = p0?.getStringExtra(INTENT_TRACK_NAME) ?: ""
        artistName = p0?.getStringExtra(INTENT_ARTIST_NAME) ?: ""
        initMediaPlayer()

        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        releasePlayer()
        timerJob?.cancel()
        _playerState.value = PlayerState.StateDefault()
        super.onDestroy()
    }

    override fun play() {
        _playerState.value = PlayerState.StatePlaying(trackPosition)
        mediaPlayer?.start()
        updatePlaybackTime()
    }

    override fun pause() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.StatePaused(trackPosition)
    }

    override fun startForeground() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun stopForeground() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun getPlayerState(): StateFlow<PlayerState> {
        return playerState
    }

    fun initMediaPlayer() {
        if (trackUrl.isEmpty()) return

        coroutineScope.launch {
            mediaPlayer?.setDataSource(trackUrl)
            mediaPlayer?.prepareAsync()
        }

        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.StatePrepared()
        }

        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            stopForeground()
            _playerState.value = PlayerState.StatePrepared()
        }
    }

    fun releasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updatePlaybackTime() {
        timerJob = coroutineScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(UPDATE_TIME_INTERVAL)
                trackPosition = dateFormat.format(mediaPlayer?.currentPosition)
                _playerState.value = PlayerState.StatePlaying(trackPosition)
            }
        }
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(
            this@MusicService,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$trackName - $artistName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.channel_name_music),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.channel_desc_music)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}