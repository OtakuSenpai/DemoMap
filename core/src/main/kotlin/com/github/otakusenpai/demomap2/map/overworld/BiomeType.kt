package com.github.otakusenpai.demomap2.map.overworld

import com.badlogic.gdx.graphics.Color
import squidpony.squidgrid.gui.gdx.SColor

enum class BiomeType constructor(val blocks: Boolean, val character: Char, val color: SColor, water: Boolean = false) {
    RIVER(false, '~', SColor.ALICE_BLUE, true),
    OCEAN_ABYSSAL(false, '~', SColor.DARK_BLUE, true),
    OCEAN_DEEP(false, '~', SColor.BLUE, true),
    OCEAN_SHALLOW(false, '~', SColor.LIGHT_BLUE, true),

    BEACH(false, '.', SColor.YELLOW),

    SCORCHED(false, '.', SColor.BROWN),
    BARE(false, '.', SColor.DARK_BROWN),
    TUNDRA(false, ',', SColor.LIGHT_GRAY),

    SNOW(false, '.', SColor.WHITE),
    SEA_ICE(false, '_', SColor(0xffffee, "Sea Ice")),

    SHRUBLAND(false, ';', SColor.BURNT_SIENNA),
    TAIGA(false, 'i', SColor.DARK_GREEN),

    TEMPERATE_DESERT(false, 'd', SColor.RED_BEAN),
    TEMPERATE_DECIDUOUS_FOREST(false, 'D', SColor.GREEN_TEA_DYE),
    TEMPERATE_RAIN_FOREST(false, 'T', SColor.GREEN_BAMBOO),


    SUBTROPICAL_DESERT(false, 'd', SColor.NAVAJO_WHITE),
    GRASSLAND(false, ':', SColor.YELLOW_GREEN),
    TROPICAL_SEASONAL_FOREST(false, 'S', SColor.GREEN_BAMBOO),
    TROPICAL_RAIN_FOREST(false, 'T', SColor.DARK_GREEN);

    val darkColor: Color
    val brightColor: Color
    var water = false

    init {
        this.water = water
        this.darkColor = Color(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a)
        this.brightColor = Color(color.r * 1.25f, color.g * 1.25f, color.b * 1.25f, color.a)
    }

    fun id(): Byte {
        return ordinal.toByte()
    }

    companion object {
        // Tree Line - highest survivable trees
        // 4000 near the equator, 2000 near the poles
        // timberline - Highest canopy - forest
        //   Simplified biome chart: http://imgur.com/kM8b5Zq
        fun biome(e: Float, t: Float, p: Float): BiomeType {

            if (e < 0.0 && t < 0.0) return SEA_ICE

            if (e < -0.75) return OCEAN_ABYSSAL
            if (e < -0.05) return OCEAN_DEEP
            if (e < 0.0) return OCEAN_SHALLOW

            if (t < 0) return SNOW
            if (e > 0.7) { // Above Treeline
                return BARE
            }

            if (e < 0.01) {
                return BEACH
            }

            if (t < 0) {
                if (p < 0.1) return SCORCHED
                if (p < 0.2) return BARE
                return if (p < 0.5) TUNDRA else SNOW
            }

            if (t < 0.40) {
                if (p < 0.2) return TEMPERATE_DESERT
                return if (p < 0.66) SHRUBLAND else TAIGA
            }

            if (t < 0.6) {
                if (p < 0.16) return TEMPERATE_DESERT
                if (p < 0.50) return GRASSLAND
                return if (p < 0.83) TEMPERATE_DECIDUOUS_FOREST else TEMPERATE_RAIN_FOREST
            }

            if (p < 0.10) return SUBTROPICAL_DESERT
            if (p < 0.33) return GRASSLAND
            return if (p < 0.66) TROPICAL_SEASONAL_FOREST else TROPICAL_RAIN_FOREST
        }
    }
}
