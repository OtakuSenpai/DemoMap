package com.github.otakusenpai.demomap2.map.worldItems

import com.github.otakusenpai.gameutilskt.math.Point2d

abstract class WorldItem() {
    var topId = 0
    var id = 0
    lateinit var pos: Point2d
    var items = mutableListOf<WorldItem>()

    constructor(id: Int, position: Point2d) : this() {
        topId = id
        pos = position
    }

    abstract fun build(x: Int, y: Int): WorldItem
    abstract fun get(id: Int): WorldItem
}