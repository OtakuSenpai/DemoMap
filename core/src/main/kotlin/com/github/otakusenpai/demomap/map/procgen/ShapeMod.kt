package com.github.otakusenpai.demomap.map.procgen

import java.awt.geom.Line2D

abstract class ShapeMod {
    abstract fun modify(nx: Int, ny: Int, line: Line2D): Double
}