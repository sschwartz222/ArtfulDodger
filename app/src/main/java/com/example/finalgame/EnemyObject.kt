package com.example.finalgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import kotlin.random.Random

class EnemyObject(i: Bitmap, sz: Rect) {

    var x: Int = 0
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0
    var xVelocity = Random.nextInt(-30, 30)
    var yVelocity = Random.nextInt(-30, 30)
    var image = i
    private val screenWidth = sz.right
    private val screenHeight = sz.bottom

    init {
        w = image.width
        h = image.height

        x = 1
        y = 1
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    //sets the bounds of the play area based on canvas size and causes direction to reverse
    //upon hitting a wall; moves object based on both x and y velocity, which are initialized
    //to random ints between 30 and -30
    fun update() {
        if (x > screenWidth - (image.width*1.1) || x < 1) {
            xVelocity *= -1
        }

        if (y > screenHeight - (image.height*1.1) || y < 1) {
            yVelocity *= -1
        }

        x += (xVelocity)
        y += (yVelocity)
    }
}