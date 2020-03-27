package com.sida.hitgame.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.StringBuilder
import kotlin.concurrent.thread

class GameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {
    var canvas: Canvas? = null
    val game = Game()
    var screenWidth: Int = 0;
    var screenHeight: Int = 0;
    var running = false
    val redPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    val blackPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }
    val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 40f
    }
    val grayPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }
    val whitePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    val thread: Thread

    init {
        holder.addCallback(this)
        thread = object : Thread() {
            override fun run() {
                running = true
                while (running) {
                    try {
                        canvas = holder.lockCanvas()
                        canvas?.drawGame(game)
                        game.move(screenHeight / (16 * 10) * (1 + game.score / 100f))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try {
                            holder.unlockCanvasAndPost(canvas)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        screenWidth = MeasureSpec.getSize(widthMeasureSpec)
        screenHeight = MeasureSpec.getSize(heightMeasureSpec)
    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        running = false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i("motion", eventToString(event!!))
        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val target = (event.x / screenWidth * 4).toInt()
                game.hitBlock(target)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val target = (event.getX(event.actionIndex) / screenWidth * 4).toInt()
                game.hitBlock(target)
            }
        }
        return true
    }

    fun eventToString(event: MotionEvent): String {
        return "pointerCount = ${event.pointerCount}" + " action : ${event.action.toString(2)}"
    }

    fun Canvas.drawGame(game: Game) {
        drawColor(Color.WHITE)
        val drawable = game.getDrawableBlocks()
        drawable.forEach {
            val mPaint = when (it.state) {
                BlockState.ACTIVE -> blackPaint
                BlockState.ERROR -> redPaint
                BlockState.DISABLED -> grayPaint
                else -> whitePaint
            }
            drawRect(it.getRect(), mPaint)
        }
        drawText("score:" + game.score, 0f, 100f, textPaint)
    }

    fun Block.getRect(): RectF {
        val left = this.i * screenWidth / 4f
        val right = (this.i + 1) * screenWidth / 4f
        val bottom = -this.j * screenHeight / 4f + screenHeight + game.moved
        val top = (-1 - this.j) * screenHeight / 4f + screenHeight + game.moved
        if (top > screenHeight) game.toUpdate = true
        return RectF(left, top, right, bottom)
    }

}