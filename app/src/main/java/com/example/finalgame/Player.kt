package com.example.finalgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class Player(i: Bitmap, sz: Rect) {

    var x: Int = 0
    var y: Int = 0
    val w: Int
    val h: Int
    var image = i
    private val screenWidth = sz.right
    private val screenHeight = sz.bottom


    init {
        w = image.width
        h = image.height

        x = screenWidth/2
        y = screenHeight/2
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    fun update(touch_x: Int, touch_y: Int) {
        x = touch_x - w / 2
        y = touch_y - h / 2
    }
}