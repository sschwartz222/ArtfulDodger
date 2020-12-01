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

    //initializes game settings--resets variables and schedules enemy spawner; spawner will
    //not start spawning enemies until the game is spawner variable is set to true after
    //the game is started in the update() function
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

    //pauses the game thread and cancels the enemy spawn scheduler, then calls startGame to
    //reinitialize game settings; also updates high score if appropriate and creates a toast
    //to display the player's score
    private fun endGame() {
        Toast.makeText(context, context.getString(R.string.end_score, cScore),
            Toast.LENGTH_LONG).show()

        if (cScore > hScore) {hScore = cScore}
        thread.setRunning(false)
        timer.cancel()
        startGame()
    }

    //adds an enemy object to the enemies list
    private fun createEnemy(surfaceSize: Rect) {
        enemies.add(EnemyObject(BitmapFactory.decodeResource(resources, R.drawable.grenade),
            surfaceSize))
    }

    //when the surface is first created, the text views of current score and high score are
    //stored for future updating; the surfaceSize is also captured for the purpose of initializing
    //player and enemy start positions, as well as the bounds of the frame
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

    //updates the game state if (started) is true (set to true in onTouchEvent function upon
    //player interaction); the player position will update if the person playing is touching the
    //screen, the entire list of enemies will always update; each enemy will check for collisions
    //with the player, although this could probably be made more efficient in the future by only
    //checking hitboxes of enemies in the quadrant the player is in, for example
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

                    //endGame must be called through a handler with access to the main UI
                    //since it prompts a toast to be created and displayed
                    val end = object:  Handler(Looper.getMainLooper()){}
                    end.post {
                        endGame()
                    }
                }
            }
            //updating the score must also be done through a handler since it alters the UI
            //created and controlled by the main activity
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

    //tracks player touch/click actions; of note are action.down and action.up:
    //action.down is the only one that can start a game so the game will be paused after
    //each playthrough until a down-touch is registered to begin the new game
    //action.up also calls endGame since removing one's finger or click from the screen
    //is a condition of defeat
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

    //used in updateScore
    private inline fun runnable(crossinline body: Runnable.() -> Unit) = object : Runnable {
        override fun run() = body()
    }
}