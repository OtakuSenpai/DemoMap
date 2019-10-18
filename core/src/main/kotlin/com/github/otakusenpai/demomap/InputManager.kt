package com.github.otakusenpai.demomap

import com.github.otakusenpai.demomap.map.worldItems.Leader
import com.github.otakusenpai.gameutilskt.math.Direction2d
import squidpony.squidgrid.gui.gdx.SquidInput

class InputManager(player: Leader) : SquidInput(KeyHandler{ key: Char, alt: Boolean, ctrl: Boolean, shift: Boolean ->
    when(key) {
        'w' -> player.setPosition(Direction2d.NORTH)
        'a' -> player.setPosition(Direction2d.WEST)
        's' -> player.setPosition(Direction2d.SOUTH)
        'd' -> player.setPosition(Direction2d.SOUTH)
    }
})