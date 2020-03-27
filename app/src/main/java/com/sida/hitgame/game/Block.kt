package com.sida.hitgame.game

import android.graphics.RectF
import kotlin.random.Random


enum class BlockState {
    ACTIVE, DISABLED, UNDEFINED, ERROR
}

class Block(val i: Int, val j: Int) {

    var state: BlockState

    init {
        state = when (Random.nextInt(4)) {
            0 -> BlockState.ACTIVE
            else -> BlockState.UNDEFINED
        }
    }

    fun changeState(): BlockState {
        when (state) {
            BlockState.ACTIVE -> state = BlockState.DISABLED
            BlockState.UNDEFINED -> state = BlockState.ERROR
            else -> {
            }
        }
        return state
    }

    override fun toString(): String {
        return "（$i,$j）"
    }

}
