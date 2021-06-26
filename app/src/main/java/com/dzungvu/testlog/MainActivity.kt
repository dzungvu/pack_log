package com.dzungvu.testlog

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dzungvu.packlog.Result
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    private val tag = this::class.java.simpleName
    private val tvCenter: TextView by lazy { findViewById(R.id.tvCenter) }
    private val btnPause: Button by lazy { findViewById(R.id.btnPauseLog) }
    private val btnPackLog: Button by lazy { findViewById(R.id.btnPackLog) }
    private lateinit var writeLogTask: TimerTask

    private var timer: Timer? = null
    private var isPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        writeLogTask = object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                Log.d(tag, "log debug at $currentTime")
                Log.i(tag, "log info at $currentTime")

                tvCenter.post {
                    tvCenter.text = "write text at $currentTime"
                }
            }
        }

        btnPause.setOnClickListener {
            if (isPause) {
                playLog()
            } else {
                pauseLog()
            }
        }

        btnPackLog.setOnClickListener {
            packlog()
        }

        timer = Timer()
        timer?.scheduleAtFixedRate(writeLogTask, 0, 100L)
    }

    private fun pauseLog() {
        if (isPause) return
        else {
            btnPause.text = "Play"
            isPause = true
            timer?.cancel()
            timer = null
        }
    }

    private fun playLog() {
        if (isPause) {
            btnPause.text = "Pause"
            isPause = false
            timer = Timer()
            writeLogTask = object : TimerTask() {
                override fun run() {
                    val currentTime = System.currentTimeMillis()
                    Log.d(tag, "log debug at $currentTime")
                    Log.i(tag, "log info at $currentTime")

                    tvCenter.post {
                        tvCenter.text = "write text at $currentTime"
                    }
                }
            }
            timer?.scheduleAtFixedRate(writeLogTask, 0, 100L)
        }
    }

    private fun packlog() {
        lifecycleScope.launch {
            when (val result = AppApplication.instance.logcatHelper.getLogFile()) {
                is Result.Success -> {
                    Log.i(tag, "Build file success")
                }

                else -> {
                    Log.e(tag, "error getting file")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}