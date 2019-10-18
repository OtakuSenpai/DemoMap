package com.github.otakusenpai.demomap

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.github.otakusenpai.demomap.map.World
import com.github.otakusenpai.demomap.map.overworld.OverworldChunk
import com.github.otakusenpai.demomap.map.worldItems.Leader

class GameMain : Game() {
    var width = 0
    var height = 0
    var seed = 8L
    var loop = 0

    lateinit var world: World
    lateinit var input: InputAdapter
    lateinit var leader: Leader

    lateinit var batch: SpriteBatch
    lateinit var view: StretchViewport
    lateinit var camera: OrthographicCamera

    override fun create() {
        width = Gdx.graphics.width / 4
        height = Gdx.graphics.height / 4
        camera = OrthographicCamera(width.toFloat(), height.toFloat())
        camera.setToOrtho(false)
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
        camera.update()
        batch = SpriteBatch()
        view = StretchViewport(width.toFloat(), height.toFloat(),camera)
        world = World(width,height,seed)
        leader = Leader().build(world.xSize * OverworldChunk.chunkSize / 2,
                world.ySize * OverworldChunk.chunkSize / 2)
        input = InputManager(leader)
        Gdx.input.inputProcessor = input
        world.generateWorld()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
        
//        ++loop
//        println(loop)
        world.render(leader,batch)
        
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
