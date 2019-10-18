package com.github.otakusenpai.demomap2.map.overworld

import com.github.otakusenpai.demomap2.map.worldItems.Settlement

class OverworldChunk {
    var elevation: Array<FloatArray>
    var temperature: Array<FloatArray>
    var precipitation: Array<FloatArray>
    var drainage: Array<FloatArray>
    var regionIds: Array<IntArray>
    var windX: Array<FloatArray>
    var windY: Array<FloatArray>

    // TODO: sunlight - angle of terrain can reduce it.  It effects temp and plant types
    // TODO: Moisture should be generated from warm water.  It should create pressure and move to low pressure
    // The north sides of mountains may have a different biome then the south sides.

    var river: Array<BooleanArray>
    var settlement: Array<Array<Settlement?>>
    var road: Array<BooleanArray>

    init {
        elevation = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        temperature = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        precipitation = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        drainage = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        regionIds = Array(chunkSize) { IntArray(chunkSize) { 0 } }
        windX = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        windY = Array(chunkSize) { FloatArray(chunkSize) { 0f } }
        road = Array(chunkSize) { BooleanArray(chunkSize) { false } }
        river = Array(chunkSize) { BooleanArray(chunkSize) { false } }
        settlement = Array(chunkSize) { arrayOfNulls<Settlement?>(chunkSize) }
    }

    fun getTileType(pX: Int, pY: Int): BiomeType {
        return if (river[pX][pY]) BiomeType.RIVER else BiomeType.biome(elevation[pX][pY], temperature[pX][pY], precipitation[pX][pY])
    }
    companion object {
        const val chunkSize = 64
    }

}
