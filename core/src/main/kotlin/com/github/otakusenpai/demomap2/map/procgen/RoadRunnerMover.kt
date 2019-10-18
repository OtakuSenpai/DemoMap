package com.github.otakusenpai.demomap2.map.procgen

import com.github.otakusenpai.demomap2.map.overworld.Overworld
import com.github.otakusenpai.gameutilskt.pathing.twoD.pathfinder.AStarHeuristic2d
import com.github.otakusenpai.gameutilskt.pathing.twoD.pathfinder.ClosestHeuristic2d
import com.github.otakusenpai.gameutilskt.pathing.twoD.shared.Mover2d

class RoadRunnerMover(internal var overWorld: Overworld) : Mover2d {
    internal val offroadMult = 2f
    internal val hillClimb = 1000f

    override var diagonal: Boolean = true
    override var heuristic: AStarHeuristic2d = ClosestHeuristic2d()

    override fun canTraverse(sx: Int, sy: Int, tx: Int, ty: Int): Boolean {
        return overWorld.getElevation(tx, ty) > 0
    }

    override fun canOccupy(tx: Int, ty: Int): Boolean {
        return overWorld.getElevation(tx, ty) > 0
    }

    override fun getCost(sx: Int, sy: Int, tx: Int, ty: Int): Float {
        return ((if (overWorld.getRoad(tx, ty)) 1f else offroadMult) // roads are less expensive

                * (if (tx == sx || ty == tx) 0.7f else 1f) // diagonal

                * (1 + hillClimb * Math.abs(overWorld.getElevation(tx, ty) - overWorld.getElevation(sx, sy)))) // The difference in elevation is absolutely tiny. so penalize any change drastically.
    }
}
