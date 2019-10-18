package com.github.otakusenpai.demomap.map

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.otakusenpai.demomap.map.overworld.BiomeType
import com.github.otakusenpai.demomap.map.overworld.Overworld
import com.github.otakusenpai.demomap.map.overworld.OverworldChunk
import com.github.otakusenpai.demomap.map.procgen.WorldGen
import com.github.otakusenpai.demomap.map.worldItems.Leader

class World() {
    lateinit var worldGen: WorldGen
    lateinit var overWorld: Overworld
    lateinit var player: Leader

    val xSize = 16
    val ySize = 16
    var screenWidth = 0
    var screenHeight = 0

    fun getBiomeTexture(biomeType: BiomeType): Texture {
        return when(biomeType) {
            BiomeType.RIVER -> Textures.river
            BiomeType.OCEAN_ABYSSAL -> Textures.abyssal_ocean
            BiomeType.OCEAN_DEEP -> Textures.deep_ocean
            BiomeType.OCEAN_SHALLOW -> Textures.shallow_ocean
            BiomeType.BEACH -> Textures.beach
            BiomeType.SCORCHED -> Textures.scorched
            BiomeType.BARE -> Textures.bare
            BiomeType.TUNDRA -> Textures.tundra
            BiomeType.SNOW -> Textures.snow
            BiomeType.SEA_ICE -> Textures.sea_ice
            BiomeType.SHRUBLAND -> Textures.shrubland
            BiomeType.GRASSLAND -> Textures.grassland
            BiomeType.TAIGA -> Textures.taiga
            BiomeType.TEMPERATE_RAIN_FOREST -> Textures.tempRainForest
            BiomeType.TEMPERATE_DECIDUOUS_FOREST -> Textures.tempDecForest
            BiomeType.TROPICAL_RAIN_FOREST -> Textures.tropicalRainForest
            BiomeType.TROPICAL_SEASONAL_FOREST -> Textures.tropicalSeasonalForest
            BiomeType.TEMPERATE_DESERT -> Textures.tempDesert
            BiomeType.SUBTROPICAL_DESERT -> Textures.subtropicalDesert
        }
    }

    constructor(screenX: Int, screenY: Int, seed: Long): this() {
        worldGen = WorldGen(seed)
        overWorld = Overworld(xSize, ySize)
        player = Leader().build(
                overWorld.xSize * OverworldChunk.chunkSize / 2,
                overWorld.ySize * OverworldChunk.chunkSize / 2
        )
        screenWidth = screenX / 4
        screenHeight = screenY / 4

    }

    fun toWorldX(screenX: Int): Int {
        return player.pos.x - screenWidth / 2 + screenX
    }

    fun toWorldY(screenY: Int): Int {
        return player.pos.y + screenHeight / 2 - screenY
    }

    fun toScreenX(worldX: Int): Int {
        return worldX - (player.pos.x - screenWidth / 2)
    }

    fun toScreenY(worldY: Int): Int {
        return -worldY + player.pos.y + screenHeight / 2
    }

    fun generateWorld() {
        println("Starting generation.")

        worldGen.generateElevation(overWorld)
        println("Generated elevation.")

        worldGen.evenElevation(overWorld)
        println("Generated even elevation.")

        worldGen.dropEdges(overWorld)
        println("Generated drop edges.")

        worldGen.generateTemperature(overWorld)
        println("Generated temperature.")

        worldGen.generateWind(overWorld)
        println("Generated wind flow.")

        worldGen.generatePrecipitation(overWorld)
        println("Generated precipitation.")

        worldGen.generateRivers(overWorld)
        println("Generated rivers.")

        worldGen.evenElevation(overWorld)
        println("Generated even elevation.")

        worldGen.generateWind(overWorld)
        println("Generated wind flow again.")

        worldGen.generatePrecipitation(overWorld)
        println("Generated precipitation.")

        worldGen.generateContinents(overWorld)
        println("Generated continents.")

        worldGen.populateSettlements(overWorld)
        println("Generated settlements.")
        println("Done.")
    }

    fun render(batch: SpriteBatch) {
        val left = player.pos.x - screenWidth / 2
        val top = player.pos.y + screenHeight / 2
        for (x in 0 until screenWidth) {
            for (y in 0 until screenHeight) {
                //Get world coords for screen
                val wx = x + left
                val wy = -y + top

                if (overWorld.getSettlement(wx, wy) != null) {
                    batch.draw(Textures.settlement,(x * 4).toFloat(), (y * 4).toFloat(), 4f, 4f)
                } else {
                    //batch.draw(getBiomeTexture(overWorld.getTileType(wx, wy)),
                    //          (x * 4).toFloat(), (y * 4).toFloat(), 4f, 4f)
                    batch.draw(getBiomeTexture(overWorld.getTileType(wx,wy)),wx.toFloat(),wy.toFloat())
                    /*
                     * Todo
                     *
                    val diff = overWorld.getElevation(wx, wy) - overWorld.getElevation(wx, wy + 1)  
                    if (diff < -0.01) {
                        batch.draw(getBiomeTexture(biomeType),x.toFloat(),y.toFloat())
                    } else if (diff > 0.01) {
                        batch.draw(getBiomeTexture(biomeType, x.toFloat(), y.toFloat()))
                    } else {
                        display.placeCharacter(x, y, biomeType.character, biomeType.color)
                    }
                     */
                }
            }
        }
    }
}
