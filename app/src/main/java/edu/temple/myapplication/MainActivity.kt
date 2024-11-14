package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

const val KEY_VALUE = "startValue"

class MainActivity : AppCompatActivity() {

    private var binder: TimerService.TimerBinder? = null
    private var isBound = false
    private var isPaused = false

    val timerHandler = Handler(Looper.getMainLooper()) {
        findViewById<TextView>(R.id.textView).text = it.what.toString()
        true
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as TimerService.TimerBinder
            binder?.setHandler(timerHandler)
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
                    }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )


        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isBound) {
                if (!binder?.isRunning!!) {
                    binder?.start(100)
                    findViewById<Button>(R.id.startButton).text = "Pause"
                    isPaused = false
                } else {
                    if (isPaused) {
                        binder?.start(100)  // Resume timer
                        findViewById<Button>(R.id.startButton).text = "Pause"
                    } else {
                        binder?.pause()
                        findViewById<Button>(R.id.startButton).text = "Unpause"
                    }
                    isPaused = !isPaused
                }
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isBound) {
                binder?.stop()
            }

        }
    }
}