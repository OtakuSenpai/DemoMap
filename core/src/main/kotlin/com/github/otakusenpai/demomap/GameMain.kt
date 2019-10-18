package com.github.otakusenpai.demomap

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.github.otakusenpai.demomap.map.World
import com.github.otakusenpai.demomap.map.overworld.OverworldChunk
import com.github.otakusenpai.demomap.map.worldItems.Leader
import squidpony.squidgrid.gui.gdx.SquidInput
import kotlin.time.ExperimentalTime

class GameMain : Game() {
    var width = 0
    var height = 0
    var xSize = 16
    var ySize = 16
    var seed = 8L
    var loop = 0

    lateinit var player: Leader

    lateinit var world: World
    lateinit var input: SquidInput
    lateinit var batch: SpriteBatch
    lateinit var view: StretchViewport
    lateinit var camera: OrthographicCamera

    @ExperimentalTime
    override fun create() {
        width = Gdx.graphics.width
        height = Gdx.graphics.height
        camera = OrthographicCamera(500f,(height/width).toFloat() * 400)
        camera.setToOrtho(false)
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
        camera.update()
        batch = SpriteBatch()
        view = StretchViewport(width.toFloat(), height.toFloat(),camera)
        player = Leader().build(
                width * OverworldChunk.chunkSize / 2,
                height * OverworldChunk.chunkSize / 2)
        input = InputManager(player)
        world = World(400,400,seed)
        world.generateWorld()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
        
        ++loop
        println(loop)
        world.render(player,batch)
        
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        view.update(width, height, true)
        view.apply(true)
        camera.viewportWidth = 30f
        camera.viewportHeight = 30f * height/width
        camera.update()
    }

}
