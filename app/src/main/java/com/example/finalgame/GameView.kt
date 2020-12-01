package com.example.finalgame

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.concurrent.timerTask

class GameView(context: Context, attributes: AttributeSet) :
    SurfaceView(context, attributes), SurfaceHolder.Callback {

    private val thread: GameThread
    private lateinit var surfaceSize: Rect
    private lateinit var timer: Timer

    private var enemies = mutableListOf<EnemyObject>()

    private var player: Player? = null
    private var touchedX: Int = 0
    private var touchedY: Int = 0

    private var touched = false
    private var started = false
    private var spawner = false

    private var cScore = 0
    private var hScore = 0
    private lateinit var tvCurrentScore : TextView
    private lateinit var tvHighScore : TextView

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    private fun startGame() {
        started = false
        spawner = false
        cScore = 0

        player = Player(BitmapFactory.decodeResource(resources, R.drawable.player),
            surfaceSize)
        enemies = mutableListOf()

        timer = Timer()
        timer.schedule(timerTask{
            if (spawner){
            createEnemy(surfaceSize)
            cScore += 1
            }
        },50, 5000)

        thread.setRunning(true)
    }
    private fun endGame() {
        Toast.makeText(context, context.getString(R.string.end_score, cScore),
            Toast.LENGTH_LONG).show()

        if (cScore > hScore) {hScore = cScore}
        thread.setRunning(false)
        timer.cancel()
        startGame()
    }

    private fun createEnemy(surfaceSize: Rect) {
        enemies.add(EnemyObject(BitmapFactory.decodeResource(resources, R.drawable.grenade),
            surfaceSize))
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        tvCurrentScore = (context as Activity).findViewById(R.id.currentScore)
        tvCurrentScore.text = context.getString(R.string.current_score, cScore)

        tvHighScore = (context as Activity).findViewById(R.id.highScore)
        tvHighScore.text = context.getString(R.string.high_score, hScore)

        surfaceSize = surfaceHolder.surfaceFrame

        startGame()
        thread.start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    fun update() {
        if (started) {

            spawner = true

            if (touched) {
                player!!.update(touchedX, touchedY)
            }

            for (enemy in enemies) {

                enemy.update()

                val enemyObjectRect = Rect(
                    enemy.x,
                    enemy.y,
                    enemy.x + enemy.w,
                    enemy.y + enemy.h
                )

                val playerRect = Rect(
                    player!!.x,
                    player!!.y,
                    player!!.x + player!!.w,
                    player!!.y + player!!.h
                )

                if (enemyObjectRect.intersect(playerRect)) {
                    enemy.xVelocity = 0
                    enemy.yVelocity = 0

                    enemy.image = BitmapFactory.decodeResource(resources, R.drawable.explosion)
                    player!!.image = BitmapFactory.decodeResource(resources, R.drawable.playersad)

                    val end = object:  Handler(Looper.getMainLooper()){}
                    end.post {
                        endGame()
                    }
                }
            }

            val updateScore = object:  Handler(Looper.getMainLooper()){}
            updateScore.post(runnable {
                tvCurrentScore.text = context.getString(R.string.current_score, cScore)
                tvHighScore.text = context.getString(R.string.high_score, hScore)
            })
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        player!!.draw(canvas)
        for (enemy in enemies) {
            enemy.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchedX = event.x.toInt()
        touchedY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touched = true
                started = true
            }
            MotionEvent.ACTION_MOVE -> touched = true
            MotionEvent.ACTION_UP -> {
                touched = false
                endGame()
            }
            MotionEvent.ACTION_CANCEL -> touched = false
            MotionEvent.ACTION_OUTSIDE -> touched = false
        }
        return true
    }

    private inline fun runnable(crossinline body: Runnable.() -> Unit) = object : Runnable {
        override fun run() = body()
    }
}