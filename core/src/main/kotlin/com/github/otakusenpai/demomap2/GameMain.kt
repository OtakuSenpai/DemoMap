package com.github.otakusenpai.demomap2

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.github.otakusenpai.demomap2.map.World

class GameMain : Game() {
    var width = 0
    var height = 0
    var xSize = 16
    var ySize = 16
    var seed = 8L
    var loop = 0

    lateinit var world: World

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
        world.generateWorld()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
        
//        ++loop
//        println(loop)
        world.render(batch)
        
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
