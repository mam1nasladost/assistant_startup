package com.example.assistant_startup
import android.R.drawable.ic_btn_speak_now
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.assistant_startup.remote.repository_imp.AssistantRepoImp
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class MyForegroundService(private val repo: AssistantRepoImp) : Service(), org.vosk.android.RecognitionListener {
    private val CHANNEL_ID = "ForegroundServiceChannelId"
    private val NOTIFICATION_ID = 1

    private var voskModel: Model? = null
    private var voskSpeechService: SpeechService? = null
    private val STARTKEYWORD = "ассистент"
    private val ENDKEYWORD = "стоп"

    private var androidSpeechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var wakeLock: PowerManager.WakeLock? = null

    private var isVoskReady = false
    private var isProcessingKeyword = false
    private var currentRecognizer: Recognizer? = null

    companion object {
        var isRunning by mutableStateOf(false)
            private set
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
        mainHandler.post { initAndroidSpeechRecognizer() }

        initModelAndVosk()
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Голосовой ассистент")
            .setContentText("Запуск и подготовка...")
            .setSmallIcon(ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)

        if (isVoskReady) {
            startVoskListening()
        }

        return START_STICKY
    }

    private fun initModelAndVosk() {
        StorageService.unpack(this, "model", "model",
            { model ->
                Log.d("VoiceService", "Модель Vosk успешно загружена")
                voskModel = model
                val recognizer = Recognizer(voskModel, 16000.0f)
                currentRecognizer = recognizer
                voskSpeechService = SpeechService(recognizer, 16000.0f)
                isVoskReady = true

                updateNotification("Ожидание ключевого слова...")
                startVoskListening()
            },
            { exception ->
                Log.e("VoiceService", "Ошибка распаковки модели: ${exception.message}")
                updateNotification("Ошибка загрузки модели")
            }
        )
    }

    private fun prepareVoskService() {
        try {
            if (currentRecognizer == null) {
                currentRecognizer = Recognizer(voskModel, 16000.0f)
            }
            else {
                currentRecognizer?.reset()
            }
            voskSpeechService = SpeechService(currentRecognizer, 16000.0f)
        } catch (e: Exception) {
            Log.e("VoiceService", "Ошибка инициализации Vosk: ${e.message}")
        }
    }

    private fun startVoskListening() {
        voskSpeechService?.startListening(this)
        Log.d("VoiceService", "Vosk слушает...")
    }

    override fun onPartialResult(hypothesis: String?) {
        if (hypothesis == null || hypothesis.contains("\"partial\" : \"\"")) {
            return
        }
        Log.d("VoskTest", "Partial: $hypothesis")
        if (hypothesis.contains(STARTKEYWORD)) {
            checkKeyword(hypothesis)
        }
    }

    private fun checkKeyword(jsonHypothesis: String?) {
        if (jsonHypothesis.isNullOrEmpty() || isProcessingKeyword) return

        try {
            val jsonObject = JSONObject(jsonHypothesis)
            val text = jsonObject.optString("text", "")
            val partial = jsonObject.optString("partial", "")
            val combinedText = (text + partial).lowercase()

            if (combinedText.isEmpty()) return

            if (combinedText.contains(STARTKEYWORD)) {
                isProcessingKeyword = true
                Log.d("VoiceService", "Услышал ключевое слово!")
                voskSpeechService?.stop()
                voskSpeechService?.shutdown()
                voskSpeechService = null

                mainHandler.postDelayed({
                    startAndroidSpeechRecognizer()
                }, 300L)
            }
            else if (combinedText.contains(ENDKEYWORD)) {
                Log.d("VoiceService", "Команда остановки!")
                onDestroy()
            }
        } catch (e: Exception) {
            Log.e("VoiceService", "Ошибка парсинга: ${e.message}")
        }
    }

    override fun onResult(hypothesis: String?) {
        checkKeyword(hypothesis)
    }

    override fun onFinalResult(hypothesis: String?) {}
    override fun onError(e: Exception?) {
        Log.e("VoiceService", "Ошибка Vosk: ${e?.message}")
    }
    override fun onTimeout() {}

    private fun initAndroidSpeechRecognizer() {
        androidSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        androidSpeechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VoiceService", "SpeechRecognizer готов к записи")
                updateNotification("Слушаю команду...")
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val userCommand = matches[0]
                    Log.d("VoiceService", "Пользователь сказал: $userCommand")
                    // TODO: Обработать команду пользователя
                }
                finishSpeechAndRestartVosk()
            }

            override fun onError(error: Int) {
                Log.e("VoiceService", "Ошибка SpeechRecognizer: $error")
                finishSpeechAndRestartVosk()
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceService", "SpeechRecognizer: начало речи услышано")
            }
            override fun onRmsChanged(rmsdB: Float) {
//                Log.d("VoiceService", "Громкость звука: $rmsdB")
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("VoiceService", "SpeechRecognizer: конец речи услышан")
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startAndroidSpeechRecognizer() {
        if (androidSpeechRecognizer == null) {
            initAndroidSpeechRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        }
        androidSpeechRecognizer?.startListening(intent)
    }

    private fun finishSpeechAndRestartVosk() {
        mainHandler.post {
            androidSpeechRecognizer?.destroy()
            androidSpeechRecognizer = null

            mainHandler.postDelayed({
                prepareVoskService()
                startVoskListening()
                isProcessingKeyword = false
                updateNotification("Ожидание ключевого слова...")
            }, 500L)
        }
    }

    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Голосовой ассистент")
            .setContentText(text)
            .setSmallIcon(ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AssistantStartup::VoskListeningWakeLock"
        )
        wakeLock?.acquire(40*60*1000L) // 40 минут
        Log.d("VoiceService", "WakeLock установлен на 40 минут")
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            Log.d("VoiceService", "WakeLock освобожден")
        }
    }

    override fun onDestroy() {
        Log.d("VoiceService", "Остановка сервиса")
        voskSpeechService?.stop()
        voskSpeechService?.shutdown()
        voskSpeechService = null
        voskModel?.close()
        voskModel = null
        currentRecognizer?.close()
        currentRecognizer = null

        mainHandler.post {
            androidSpeechRecognizer?.stopListening()
            androidSpeechRecognizer?.cancel()
            androidSpeechRecognizer?.destroy()
            androidSpeechRecognizer = null
        }
        isRunning = false

        stopForeground(STOP_FOREGROUND_REMOVE)
        releaseWakeLock()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}