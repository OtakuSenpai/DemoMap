package com.github.otakusenpai.demomap.map.overworld

import com.github.otakusenpai.demomap.map.worldItems.Settlement
import com.github.otakusenpai.gameutilskt.pathing.twoD.shared.TileBasedMap2d

class Overworld() : TileBasedMap2d {
    override var xSize: Int = 0
    override var ySize: Int = 0

    lateinit var chunks: Array<Array<OverworldChunk>>

    constructor(xSize: Int, ySize: Int): this() {
        this.xSize = xSize
        this.ySize = ySize
        chunks = Array(xSize) { Array(ySize) { OverworldChunk() } }
    }

    private fun getPrecise(globalCoord: Int): Int = if (globalCoord >= 0) {
            globalCoord % OverworldChunk.chunkSize
        } else {
            (globalCoord % OverworldChunk.chunkSize) + OverworldChunk.chunkSize - 1
        }

    private fun getChunkCoord(globalCoord: Int): Int = if (globalCoord >= 0) {
            globalCoord / OverworldChunk.chunkSize
        } else {
            (globalCoord / OverworldChunk.chunkSize) - 1
        }

    fun getPreciseXSize() = xSize * OverworldChunk.chunkSize
    fun getPreciseYSize() = ySize * OverworldChunk.chunkSize

    fun getTileType(globalX: Int, globalY: Int): BiomeType {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return BiomeType.OCEAN_ABYSSAL
        return chunk!!.getTileType(getPrecise(globalX), getPrecise(globalY))
    }

    fun getElevation(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return -1f
        return chunk!!.elevation[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun getSlopeX(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY))
        val chunk2 = loadChunk(getChunkCoord(globalX + 1), getChunkCoord(globalY))
        return if (chunk == null || chunk2 == null) 0f else
            chunk!!.elevation[getPrecise(globalX)][getPrecise(globalY)] -
                    chunk2!!.elevation[getPrecise(globalX + 1)][getPrecise(globalY)]
    }

    fun getSlopeY(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY))
        val chunk2 = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY + 1))
        return if (chunk == null || chunk2 == null) 0f else
            chunk!!.elevation[getPrecise(globalX)][getPrecise(globalY)] -
                    chunk2!!.elevation[getPrecise(globalX)][getPrecise(globalY + 1)]
    }

    fun getTemp(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return -1f
        return chunk!!.temperature[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun getPrecipitation(globalX: Int, globalY: Int): Float {
        val chunk = loadGlobalChunk(globalX, globalY) ?: return -1f
        return chunk!!.precipitation[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun getLatitude(globalY: Int): Float {
        val yCenter = ySize * OverworldChunk.chunkSize / 2
        return (globalY - yCenter).toFloat() / yCenter
    }

    fun getLongitude(globalX: Int): Float {
        val xCenter = xSize * OverworldChunk.chunkSize / 2
        return (globalX - xCenter).toFloat() / xCenter
    }

    fun loadChunk(chunkX: Int, chunkY: Int): OverworldChunk? {
        if (chunkX < 0 || chunkX >= xSize || chunkY < 0 || chunkY >= ySize) {
            return null
        } else if (chunks[chunkX][chunkY] == null) {
            // TODO: load from disk
            throw RuntimeException("Nope, loading not implemented yet")
        }
        return chunks[chunkX][chunkY]
    }

    fun loadGlobalChunk(globalX: Int, globalY: Int): OverworldChunk? {
        return loadChunk(getChunkCoord(globalX), getChunkCoord(globalY))
    }

    fun setRiver(globalX: Int, globalY: Int) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null)
            chunk.river[getPrecise(globalX)][getPrecise(globalY)] = true
    }

    fun buildSettlement(globalX: Int, globalY: Int, pop: Int) {
        val settlement = Settlement().build(globalX, globalY, pop) as Settlement
        val chunk = loadGlobalChunk(globalX, globalY)
        chunk!!.settlement[getPrecise(globalX)][getPrecise(globalY)] = settlement
    }

    fun getSettlement(globalX: Int, globalY: Int): Settlement? {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return null
        return chunk.settlement[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun setPrecipitation(globalX: Int, globalY: Int, precip: Float) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null)
            chunk.precipitation[getPrecise(globalX)][getPrecise(globalY)] = precip
    }

    fun setWind(globalX: Int, globalY: Int, xFlow: Float, yFlow: Float) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null) {
            chunk.windX[getPrecise(globalX)][getPrecise(globalY)] = xFlow
            chunk.windY[getPrecise(globalX)][getPrecise(globalY)] = yFlow
        }
    }

    fun getWindX(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return -1f
        return chunk.windX[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun getWindY(globalX: Int, globalY: Int): Float {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return -1f
        return chunk.windY[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun setElevation(globalX: Int, globalY: Int, elevation: Float) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null)
            chunk.elevation[getPrecise(globalX)][getPrecise(globalY)] = elevation
    }

    fun getRoad(globalX: Int, globalY: Int): Boolean {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return false
        return chunk.road[getPrecise(globalX)][getPrecise(globalY)]
    }

    fun setRoad(globalX: Int, globalY: Int) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null)
            chunk.road[getPrecise(globalX)][getPrecise(globalY)] = true
    }

    override fun isOutside(globalX: Int, globalY: Int): Boolean {
        return globalX < 0 || globalY < 0 || globalX >= xSize * OverworldChunk.chunkSize ||
                globalY >= ySize * OverworldChunk.chunkSize
    }

    fun setRegion(globalX: Int, globalY: Int, newRegion: Int) {
        val chunk = loadGlobalChunk(globalX, globalY)
        if (chunk != null)
            chunk.regionIds[getPrecise(globalX)][getPrecise(globalY)] = newRegion
    }

    fun getRegionId(globalX: Int, globalY: Int): Int {
        val chunk = loadChunk(getChunkCoord(globalX), getChunkCoord(globalY)) ?: return -3
        return chunk.regionIds[getPrecise(globalX)][getPrecise(globalY)]
    }

}