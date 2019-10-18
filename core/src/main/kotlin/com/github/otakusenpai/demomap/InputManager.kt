package com.github.otakusenpai.demomap

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.github.otakusenpai.demomap.map.worldItems.Leader
import com.github.otakusenpai.gameutilskt.math.Direction2d

/*
class InputManager(player: Leader) : SquidInput(KeyHandler{ key: Char, alt: Boolean, ctrl: Boolean, shift: Boolean ->
    when(key) {
        'w' -> player.setPosition(Direction2d.NORTH)
        'a' -> player.setPosition(Direction2d.WEST)
        's' -> player.setPosition(Direction2d.SOUTH)
        'd' -> player.setPosition(Direction2d.SOUTH)
    }
})
*/

class InputManager(var player: Leader) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> player.setPosition(Direction2d.NORTH)
            Input.Keys.A -> player.setPosition(Direction2d.WEST)
            Input.Keys.S -> player.setPosition(Direction2d.SOUTH)
            Input.Keys.D -> player.setPosition(Direction2d.EAST)
        }
        return true
    }
}