package com.github.otakusenpai.demomap2.map.worldItems

import com.github.otakusenpai.gameutilskt.math.Point2d

class Settlement() : WorldItem() {
    var population  = 0

    private constructor(id: Int, pos: Point2d, pop: Int): this() {
        this.id = id
        this.pos = pos
        population = pop
    }

    @Deprecated("Use build(Int,Int,Int)")
    override fun build(x: Int, y: Int): WorldItem {
        return build(x,y,0)
    }

    fun build(x: Int, y: Int, pop: Int): WorldItem {
        val settlement = Settlement(topId, Point2d(x,y),pop)
        items.add(topId++, settlement)
        return settlement
    }

    override fun get(id: Int): WorldItem{
        return items[id] as Settlement
    }
}


