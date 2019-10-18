package com.github.otakusenpai.demomap.map.procgen

import java.awt.geom.Line2D
import kotlin.math.max

class ImplShapeMod : ShapeMod() {
    override fun modify(nx: Int, ny: Int, line: Line2D): Double {
        return 1.2 - max(line.ptLineDist(nx.toDouble(), ny.toDouble()) / 160f, 1.0)
    }
}