package dev.xanhub.muzwick

import dev.xanhub.tinyengine.Clock
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import org.joml.Vector2i
import kotlin.math.floor

object Game {
    private var framesSinceLastFpsUpdate = 0
    val window = Window(Vector2i(1600, 900), "XanRPG")
    val clock = Clock()
    var scene: Scene? = null

    @JvmStatic
    fun main(args: Array<String>) {
        clock.tickListeners.add(Int.MAX_VALUE) {
            window.display()
            window.clear()
            window.pollEvents()

            framesSinceLastFpsUpdate++
            if(floor(clock.gameTime + clock.deltaTime) > floor(clock.gameTime)) {
                println("FPS: $framesSinceLastFpsUpdate")
                println("Direction: ${scene?.camera?.direction ?: "NO SCENE"}")
                println("Pos: ${scene?.camera?.position ?: "NO SCENE"}")
                framesSinceLastFpsUpdate = 0
            }

            if(scene != null)
                window.draw(scene!!)
        }

        scene = BuildingNavigationScene

        clock.runWhile { window.isOpen() }
    }
}

