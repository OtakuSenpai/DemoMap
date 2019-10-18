package com.github.otakusenpai.demomap.map.worldItems

import com.github.otakusenpai.gameutilskt.math.Direction2d
import com.github.otakusenpai.gameutilskt.math.Point2d

class Leader() : WorldItem() {
    private constructor(id: Int, pos: Point2d) : this() {
        this.id = id
        this.pos = pos
    }

    override fun build(x: Int, y: Int): Leader {
        val leader = Leader(topId, Point2d(x, y))
        items.add(topId++, leader)
        return leader
    }

    override fun get(id: Int): Leader {
        return items[id] as Leader
    }

    fun setPosition(direction2d: Direction2d) {
        this.pos.x -= direction2d.x
        this.pos.y -= direction2d.y
    }
}
