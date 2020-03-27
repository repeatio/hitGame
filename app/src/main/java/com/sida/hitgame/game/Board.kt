package com.sida.hitgame.game

import java.util.concurrent.ConcurrentLinkedQueue

class Board(val column: Int, val row: Int) {

    val queue = ConcurrentLinkedQueue<List<Block>>()
    var lastRow: Int

    init {
        lastRow = row + 1
        repeat(lastRow) { j ->
            val rowBlocks = mutableListOf<Block>()
            repeat(column) { i ->
                rowBlocks.add(Block(i, j))
            }
            queue.offer(rowBlocks)
        }
    }

    fun getHeaderRow(): List<Block>? {
        return queue.peek()
    }

    fun getActiveRow(): List<Block>? {
        return queue.firstOrNull { list ->
            list.any {
                it.state == BlockState.ACTIVE
            }
        }
    }

    fun updateBuffer() {
        queue.poll()
        val toAdd = mutableListOf<Block>()
        repeat(column) {
            toAdd.add(Block(it, lastRow))
        }
        queue.add(toAdd)
        lastRow += 1
    }

    fun getColumn(i: Int): List<Block>? {
        return queue.flatten().filter { it.i == i }.sortedBy { it.j }
    }

    fun getBlock(i: Int, j: Int): Block? {
        return queue.flatten().find { it.i == i && it.j == j }
    }
}
