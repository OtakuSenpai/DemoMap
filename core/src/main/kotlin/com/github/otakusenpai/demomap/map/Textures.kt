package com.github.otakusenpai.demomap.map

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.github.otakusenpai.demomap.map.overworld.BiomeType

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

    val river = atlas.findRegion("ink_full").texture
    val abyssal_ocean = atlas.findRegion("deep_water").texture
    val deep_ocean = atlas.findRegion("open_sea").texture
    val shallow_ocean = atlas.findRegion("green_bones",9).texture
    val beach = atlas.findRegion("sand",1).texture
    val scorched = atlas.findRegion("lava",0).texture
    val bare = atlas.findRegion("unseen").texture
    val tundra = atlas.findRegion("frozen",0).texture
    val snow = atlas.findRegion("ice_0_new").texture
    val sea_ice = atlas.findRegion("frozen",9).texture
    val shrubland = atlas.findRegion("dirt_0_old").texture
    val grassland = atlas.findRegion("dirt_full_new").texture
    val taiga = atlas.findRegion("tree_2_lightred").texture
    val tempRainForest = atlas.findRegion("mangrove",1).texture
    val tempDecForest = atlas.findRegion("tree_1_red").texture
    val tropicalRainForest = atlas.findRegion("mangrove",2).texture
    val tropicalSeasonalForest = atlas.findRegion( "snake-c",1).texture
    val tempDesert = atlas.findRegion("limestone",1).texture
    val subtropicalDesert = atlas.findRegion("mangrove",3).texture
    val settlement = assets.assetManager.get("TheArshenOrbs-Dungeon.atlas",
            TextureAtlas::class.java).findRegion("abandoned_shop").texture
}
