package com.github.otakusenpai.demomap.map

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class Assets() {
    var assetManager: AssetManager = AssetManager()

    init {
        assetManager.load("Gentium-distance.fnt", BitmapFont::class.java)
        assetManager.load("Gentium-distance.png", Texture::class.java)
        assetManager.load("TheArshenOrbs-Dungeon.atlas", TextureAtlas::class.java)
        assetManager.load("TheArshenOrbs-Dungeon.png", Texture::class.java)
        assetManager.finishLoading()
    }
}

object Textures {
    var assets = Assets()
    var atlas = assets.assetManager.get("TheArshenOrbs-Dungeon.atlas", TextureAtlas::class.java)

    val river = atlas.findRegion("shoals_shallow_water_disturbance_2_new")
    val abyssal_ocean = atlas.findRegion("deep_water")
    val deep_ocean = atlas.findRegion("open_sea")
    val shallow_ocean = atlas.findRegion("shoals_shallow_water",0)
    val beach = atlas.findRegion("sand",1)
    val scorched = atlas.findRegion("lava",0)
    val bare = atlas.findRegion("floor_sand_rock",0)
    val tundra = atlas.findRegion("frozen",0)
    val snow = atlas.findRegion("ice_0_new")
    val sea_ice = atlas.findRegion("frozen",9)
    val shrubland = atlas.findRegion("dirt_0_old")
    val grassland = atlas.findRegion("dirt_full_new")
    val taiga = atlas.findRegion("tree_2_lightred")
    val tempRainForest = atlas.findRegion("mangrove",1)
    val tempDecForest = atlas.findRegion("tree_1_red")
    val tropicalRainForest = atlas.findRegion("mangrove",2)
    val tropicalSeasonalForest = atlas.findRegion( "snake-c",1)
    val tempDesert = atlas.findRegion("limestone",1)
    val subtropicalDesert = atlas.findRegion("mangrove",3)
    val settlement = atlas.findRegion("abandoned_shop")
}
