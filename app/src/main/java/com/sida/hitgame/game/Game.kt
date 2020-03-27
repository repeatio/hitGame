package com.sida.hitgame.game

import android.util.Log
import java.lang.StringBuilder


class Game(val gameBoard: Board = Board(4, 4)) {
    var score = 0
    var moved = 0f
    var toUpdate = false

    fun printBoard(): String {
        val queue = gameBoard.queue
        val sb = StringBuilder().append("--------------------\n")
        queue.forEach { line ->
            sb.append("[")
            line.forEach { sb.append(it.toString()).append(",") }
            sb.append("]\n")
        }
        return sb.toString()
    }

    init {
        Log.i("board:", printBoard())
    }

    fun move(delta: Float) {
        moved += delta
        if (toUpdate) {
            score -= getUnhandledNumber()
            updateBuffer()
            Log.i("board:", printBoard())
            toUpdate = false
        }
    }

    fun updateBuffer() {
        gameBoard.updateBuffer()
    }

    fun hitBlock(i: Int) {
        val result = gameBoard.getActiveRow()?.getOrNull(i)?.changeState()
        when (result) {
            BlockState.DISABLED -> score += 1
            BlockState.ERROR -> score -= 1
            else -> {
            }
        }
    }

    fun getDrawableBlocks(): List<Block> {
        return gameBoard.queue.flatten()
    }

    fun getUnhandledNumber(): Int {
        return gameBoard.getHeaderRow()?.count { it.state == BlockState.ACTIVE } ?: 0
    }


}