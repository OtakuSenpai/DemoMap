package com.github.otakusenpai.demomap.map.procgen

import com.github.otakusenpai.demomap.map.overworld.Overworld
import com.github.otakusenpai.demomap.map.worldItems.Settlement
import com.github.otakusenpai.demomap.map.overworld.OverworldChunk
import com.github.otakusenpai.gameutilskt.math.Direction2d
import com.github.otakusenpai.gameutilskt.math.MathUtils
import com.github.otakusenpai.gameutilskt.math.MathUtils.Companion.d
import com.github.otakusenpai.gameutilskt.math.Point2d
import com.github.otakusenpai.gameutilskt.noise.SimplexNoise
import com.github.otakusenpai.gameutilskt.pathing.twoD.pathfinder.AStarPathFinder2d
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.util.*
import java.util.logging.Logger
import kotlin.math.*

var settlement = Settlement()

class WorldGen {
    internal var el: SimplexNoise
    internal var mo: SimplexNoise
    internal var r: Random

    constructor(seed: Long) {
        r = Random(seed)
        el = SimplexNoise(r.nextLong())
        mo = SimplexNoise(r.nextLong())
    }

    constructor() {
        r = Random()
        el = SimplexNoise(r.nextLong())
        mo = SimplexNoise(r.nextLong())
    }

    // Initial step the can be done per chunk
    private fun generateChunkedHeightMap(overWorld: Overworld, shapeMods: MutableList<ShapeMod>, chunkX: Int, chunkY: Int): OverworldChunk {
        val line = Line2D.Float(Point2D.Float(0f, overWorld.getPreciseXSize().toFloat()),
                Point2D.Float(overWorld.getPreciseYSize().toFloat(), 0f))
        val overWorldChunk = OverworldChunk()
        for (x in 0 until OverworldChunk.chunkSize - 1) {
            for (y in 0 until OverworldChunk.chunkSize - 1) {
                //global coord
                val nx = chunkX * OverworldChunk.chunkSize + x
                val ny = chunkY * OverworldChunk.chunkSize + y
                var ridginess = fbm(nx.toDouble(), ny.toDouble(), 6, 1 / 320.0, 1.0, 2.0, 0.5)
                ridginess = abs(ridginess) * -1
                var elevation = max(fbm(nx.toDouble(), ny.toDouble(), 6, 1 / 200.0, 1.0, 2.0, 0.5), ridginess)
                for (mod in shapeMods) {
                    elevation += mod.modify(nx, ny,line)
                }
                overWorldChunk.elevation[x][y] = elevation.toFloat()
            }
        }
        return overWorldChunk
    }

    private fun fbm(x: Double, y: Double, octaves: Int, frequency: Double, amplitude: Double, lacunarity: Double, gain: Double): Double {
        var frequency = frequency
        var amplitude = amplitude
        var total = 0.0
        for (i in 0 until octaves) {
            total += el.eval(x * frequency, y * frequency) * amplitude
            frequency *= lacunarity
            amplitude *= gain
        }
        return total
    }

    private fun generateChunkedTemperatureMap(overWorld: Overworld, chunkX: Int, chunkY: Int): OverworldChunk {

        val overWorldChunk = overWorld.loadChunk(chunkX, chunkY)
        val yCenter = overWorld.ySize * OverworldChunk.chunkSize / 2

        for (x in 0 until OverworldChunk.chunkSize - 1) {
            for (y in 0 until OverworldChunk.chunkSize - 1) {

                val nx = chunkX * OverworldChunk.chunkSize + x
                val ny = chunkY * OverworldChunk.chunkSize + y

                val yDist = abs(ny - yCenter).toFloat() / yCenter

                // Temperature
                //decreases with height, decreases with closeness to poles

                overWorldChunk!!.temperature[x][y] = MathUtils.limit(
                        1.2f * (1f - yDist) -
                                max(0f, overWorldChunk.elevation[x][y]) + 0.1f * el.eval(
                                (1.0f * nx / 125.0f).toDouble(),
                                (1.0f * ny / 125.0f).toDouble()).toFloat(),
                        -1f, 1f)
            }
        }
        return overWorldChunk!!
    }

    private fun floodFillBFS(overWorld: Overworld, sx: Int, sy: Int, target: Int, replacement: Int) {

        val q = Point2d(sx, sy)
        val xSize = overWorld.getPreciseXSize()
        val ySize = overWorld.getPreciseYSize()

        if (q.y < 0 || q.y >= ySize || q.x < 0 || q.x >= xSize)
            return

        val stack = ArrayDeque<Point2d>()
        stack.push(q)
        while (stack.size > 0) {
            val p = stack.pop()
            val x = p.x
            val y = p.y
            if (y < 0 || y >= ySize || x < 0 || x >= xSize)
                continue
            val value = overWorld.getRegionId(x, y)
            if (value == target) {
                overWorld.setRegion(x, y, replacement)
                if (x + 1 < xSize && overWorld.getRegionId(x + 1, y) == target)
                    stack.push(Point2d(x + 1, y))
                if (x - 1 > 0 && overWorld.getRegionId(x - 1, y) == target)
                    stack.push(Point2d(x - 1, y))
                if (y + 1 < ySize && overWorld.getRegionId(x, y + 1) == target)
                    stack.push(Point2d(x, y + 1))
                if (y - 1 > 0 && overWorld.getRegionId(x, y - 1) == target)
                    stack.push(Point2d(x, y - 1))
            }
        }

    }

    fun expandRealms(overWorld: Overworld) {
        val realm: Array<IntArray>
        // select empire starting points
        // set them as their respective
    }

    companion object {
        private val log = Logger.getLogger(WorldGen::class.java.name)
        private val uncalculatedRegion = -1
        private val blockedRegion = -2
    }

    /*
     * The following functions are used in world generation
     */

    /**
     * Precipitation should flow down grade to the sea
     * Anywhere there is a significant flow should be marked as a river
     *
     *
     * Areas with enough flow can dig out a canyon.
     *
     *
     * May need to floodfill a lake if we get to a local minima
     *
     * @param overWorld
     */
    fun generateRivers(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() -1).toInt()
        val ySize = (overWorld.getPreciseYSize() -1).toInt()
        for (i in 0..4) {
            val riverFlux = Array(xSize + 1) { FloatArray(ySize + 1) }
            for(x in 0 until xSize) {
                for(y in 0 until ySize) {
                    val flow = overWorld.getPrecipitation(x, y) + 0.1f
                    var tempX = x
                    var tempY = y

                    while (true) {
                        if (overWorld.isOutside(tempX, tempY))
                            break

                        // if the biome is ocean or frozen then end.
                        //                        BiomeType existingType = overWorld.getTileType(tempX, tempY);

                        //                        if (existingType == BiomeType.OCEAN_ABYSSAL || existingType == BiomeType.OCEAN_DEEP || existingType == BiomeType.OCEAN_SHALLOW) {
                        //                            break;
                        //                        }
                        var facing: Direction2d? = null
                        var height = overWorld.getElevation(tempX, tempY)

                        for (facing2d in Direction2d.values()) {
                            val potentialHeight = overWorld.getElevation(tempX + facing2d.x,
                                    tempY + facing2d.y)
                            if (height > potentialHeight) {
                                facing = facing2d
                                height = potentialHeight
                            }
                        }
                        if (facing == null) {
                            break//TODO: local minima lake
                        } else {
                            riverFlux[tempX][tempY] += flow
                            tempX += facing.x
                            tempY += facing.y
                        }
                    }
                }
            }

            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    var value = 0f
                    for (xD in -5..5) {
                        value += riverFlux[MathUtils.limit(x + xD, 0, (xSize - 1).toInt())][y] /
                                (abs(xD) + 1)
                    }
                    riverFlux[x][y] = value / 3.9f
                }
            }
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    var value = 0f
                    for (yD in -5..5) {
                        value += riverFlux[x][MathUtils.limit(y + yD, 0, (ySize - 1).toInt())] /
                                (abs(yD) + 1)
                    }
                    riverFlux[x][y] = value / 3.9f
                }
            }
            var maxFlux = 0f
            //Find maxflux to get
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    maxFlux = max(maxFlux, sqrt(riverFlux[x][y].toDouble()).toFloat())
                }
            }
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    val f = overWorld.getElevation(x, y)
                    overWorld.setElevation(x, y, max(-1f, f - 0.2f * sqrt(max(riverFlux[x][y] - 0.1,
                            0.0)).toFloat() / maxFlux))
                }
            }
            for (x in 0 until xSize) {
                for (y in 0 until ySize) {
                    if (riverFlux[x][y] > maxFlux / 2f && !overWorld.getTileType(x, y).water) {
                        overWorld.setRiver(x, y)
                    }
                }
            }
        }
    }

    /**
     * Build Settlements
     *
     *
     * Human settlements should be built near a source of water, preferably a river.
     * Most should be built at a low elevation, near an area with high river flux
     * Generating a good distance from other towns
     *
     *
     * Population should follow https://en.wikipedia.org/wiki/Zipf%27s_law
     *
     * @param overWorld
     */
    fun populateSettlements(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() - 1).toInt()
        val ySize = (overWorld.getPreciseYSize() - 1).toInt()
        val totalSettlements = 20
        val propositions = 1000

        // Build Settlements
        for (i in 0 until totalSettlements) {
            val possibleLocations = mutableListOf<Point2d>()
            val pop = d(1000)
            // Propose new settlement locations
            for (j in 0 until propositions) {
                val x = MathUtils.getIntInRange(0, xSize)
                val y = MathUtils.getIntInRange(0, ySize)
                possibleLocations.add(Point2d(x, y))
            }

            var bestScore = java.lang.Float.NEGATIVE_INFINITY
            var bestLocal: Point2d? = null

            for (p in possibleLocations) {
                var score = 0f
                val biomeType = overWorld.getTileType(p.x, p.y)

                if (biomeType.water) {
                    score -= 1000f
                }
                for (s in settlement.items) {
                    s as Settlement
                    val dist = if(p.getChebyshevDistance(s.pos) == 0) 1 else p.getChebyshevDistance(s.pos) 
                    score -= s.population * pop / (dist * dist)
                }
                val percip = overWorld.getPrecipitation(p.x, p.y)
                score += percip * 10
                if (score > bestScore) {
                    bestScore = score
                    bestLocal = p
                }
            }
            if (bestLocal != null) {
                overWorld.buildSettlement(bestLocal.x, bestLocal.y, pop)
            }
        }
    }


    fun generateElevation(overWorld: Overworld) {
        for(x in 0 until overWorld.xSize - 1) {
            for(y in 0 until overWorld.ySize - 1) {
                overWorld.chunks[x][y] = generateChunkedHeightMap(overWorld, mutableListOf(ImplShapeMod()), x, y)
            }
        }
    }

    fun evenElevation(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() -1)
        val ySize = (overWorld.getPreciseYSize() - 1)
        var highest = -1f
        var lowest = 1f

        for (y in 0 until ySize ) {
            for (x in 0 until xSize) {
                val elev = overWorld.getElevation(x, y)
                if (elev < lowest)
                    lowest = elev
                if (elev > highest)
                    highest = elev
            }
        }
        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                var elev = overWorld.getElevation(x, y)
                elev = 2 * (elev - lowest) / (highest - lowest) - 1
                overWorld.setElevation(x, y, elev)
            }
        }
    }

    fun generateTemperature(overWorld: Overworld) {
        IntRange(0, overWorld.xSize - 1).forEach { x -> IntRange(0, overWorld.ySize - 1).forEach{
            y -> overWorld.chunks[x][y] = generateChunkedTemperatureMap(overWorld, x, y) } }
    }

    fun dropEdges(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() - 1)
        val ySize = (overWorld.getPreciseYSize() - 1)
        val xCenter = overWorld.xSize * OverworldChunk.chunkSize / 2
        val yCenter = overWorld.ySize * OverworldChunk.chunkSize / 2
        val maxDist = sqrt((xCenter * xCenter + yCenter * yCenter).toDouble())

        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                var elev = overWorld.getElevation(x, y)
                val dist = sqrt((xCenter - x).toDouble().pow(2.0) + Math.pow((yCenter - y).toDouble(), 2.0))

                // Elevation - decreases near edges
                elev = MathUtils.limit((elev - (dist / maxDist).pow(4.0)).toFloat(), -1f, 1f)
                overWorld.setElevation(x, y, elev)
            }
        }
    }

    fun generateWind(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() - 1)
        val ySize = (overWorld.getPreciseYSize() - 1)
        val periods = 4f

        IntRange(0, overWorld.xSize - 1).forEach { xChunk ->
            IntRange(0, overWorld.ySize - 1).forEach { yChunk ->
                val overWorldChunk = overWorld.loadChunk(xChunk, yChunk)
                for (y in 0 until OverworldChunk.chunkSize) {
                    val ny = yChunk * OverworldChunk.chunkSize + y
                    val percentage = ny.toFloat() / ySize
                    val globalWindX = cos(periods.toDouble() * (2 * Math.PI) * percentage.toDouble()).toFloat()
                    for (x in 0 until OverworldChunk.chunkSize) {
                        val nx = xChunk * OverworldChunk.chunkSize + x
                        val temp = overWorld.getTemp(nx, ny)
                        val xd = temp - overWorld.getTemp(nx + 1, ny)
                        val yd = temp - overWorld.getTemp(nx, ny + 1)
                        // Rotate 90 degrees
                        var windX = yd
                        var windY = -xd

                        windX = 50f * windX - 0.5f * globalWindX
                        windY = 50f * windY + 0.5f * globalWindX
                        overWorldChunk!!.windX[x][y] = windX
                        overWorldChunk.windY[x][y] = windY
                    }
                }
            }
        }
    }

    // This is a hack for rain shadows.  Start at your square, then go upwind
    // warm temp on water squares boosts humidity, cold mountains limit it
    fun generatePrecipitation(overWorld: Overworld) {
        val xSize = (overWorld.getPreciseXSize() -1)
        val ySize = (overWorld.getPreciseYSize() -1)
        val maxDistance = 1000f
        val precip = Array(xSize) { FloatArray(ySize) }

        for(x in 0 until xSize) {
            for(y in 0 until ySize) {
                var tempX = x
                var tempY = y
                var distanceLeft = maxDistance
                while (distanceLeft > 0) {
                    if (overWorld.isOutside(tempX, tempY))
                        break
                    val bt = overWorld.getTileType(tempX, tempY)
                    if (bt.water)
                        break
                    distanceLeft--
                    val windX = overWorld.getWindX(tempX, tempY)
                    val windY = overWorld.getWindY(tempX, tempY)
                    tempX += windX.toInt()
                    tempY += windY.toInt()
                }
                precip[x][y] = distanceLeft / maxDistance
                //overWorld.setPrecipitation(x, y, distanceLeft / maxDistance);
            }
        }

        //Blur to get rid of tight swirls
        for (x in 0 until xSize) {
            for (y in 0 until ySize) {
                var foo = 0f
                for (xD in -5..5) {
                    foo += precip[MathUtils.limit(x + xD, 0, xSize - 1)][y]
                }
                precip[x][y] = foo / 10f
            }
        }
        for (x in 0 until xSize) {
            for (y in 0 until ySize) {
                var value = 0f
                for (yD in -5..5) {
                    value += precip[x][MathUtils.limit(y + yD, 0, ySize - 1)]
                }
                precip[x][y] = value / 10f
            }
        }
        for (x in 0 until xSize) {
            for (y in 0 until ySize) {
                overWorld.setPrecipitation(x, y, precip[x][y])
            }
        }
    }

    // http://roadtrees.com/creating-road-trees/
    fun createRoadNetwork(overWorld: Overworld) {
        //Step One Determine which cities to link together first. Larger closer cities should go first
        val settlementsPerContinent = HashMap<Int, MutableList<Settlement>>()
        for (s in settlement.items) {
            s as Settlement
            val id = overWorld.getRegionId(s.pos.x, s.pos.y)
            val settlements: MutableList<Settlement> = settlementsPerContinent[id]!!
            settlementsPerContinent[id] = settlements
            settlements.add(settlement)
        }

        settlementsPerContinent.values.parallelStream().forEach { settlements ->
            val pairs = ArrayList<RankedSettlementPair>()
            for (a in 0 until settlements.size - 1) {
                for (b in a + 1 until settlements.size) {
                    pairs.add(RankedSettlementPair(settlements[a], settlements[b]))
                }
            }
            pairs.sort()
            val pathFinder2d = AStarPathFinder2d(overWorld, (overWorld.getPreciseXSize() *
                    overWorld.getPreciseYSize()).toInt())
            val mover2d = RoadRunnerMover(overWorld)
            for (p in pairs) {
                val a = settlement.get(p.a)
                val b = settlement.get(p.b)
                val path = pathFinder2d.findPath(mover2d, a.pos.x, a.pos.y, b.pos.x, b.pos.y)
                if (path != null) {
                    for (i in 0 until path.length) {
                        val step = path.getStep(i)
                        overWorld.setRoad(step.x, step.y)
                    }
                }
            }

        }
        // Use A* to link cities.  Cost should reflect slope and terrain type.  Bridges are possible, but expensive.
        //The first cities that are linked will have busy roads, and they will shrink down as
    }

    // This finds contiguous land masses
    fun generateContinents(overWorld: Overworld) {
        for (x in 0 until overWorld.getPreciseXSize()) {
            for (y in 0 until overWorld.getPreciseYSize()) {
                if (overWorld.getElevation(x, y) < 0)
                    overWorld.setRegion(x, y, blockedRegion)
                else
                    overWorld.setRegion(x, y, uncalculatedRegion)
            }
        }
        var i = 0
        for (x in 0 until overWorld.getPreciseXSize()) {
            for (y in 0 until overWorld.getPreciseYSize()) {
                if (overWorld.getRegionId(x, y) == uncalculatedRegion) {
                    floodFillBFS(overWorld, x, y, uncalculatedRegion, i++)
                }
            }
        }
    }
}

internal class RankedSettlementPair(a: Settlement, b: Settlement) : Comparable<RankedSettlementPair> {
    var a: Int = 0
    var b: Int = 0
    var distance: Double = 0.toDouble()

    init {
        this.a = a.id
        this.b = b.id

        distance = a.population * b.population / ((a.pos.getChebyshevDistance(b.pos)).toFloat().pow(2.0f)).toDouble()
    }

    override fun compareTo(o: RankedSettlementPair): Int {
        if (distance == o.distance) {
            return 0
        }
        return if (distance - o.distance > 0) 1 else -1
    }
}
