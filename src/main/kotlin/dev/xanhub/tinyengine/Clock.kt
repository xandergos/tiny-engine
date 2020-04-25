package dev.xanhub.tinyengine

import org.joml.Math
import org.lwjgl.glfw.GLFW.glfwGetTime
import java.lang.Double.min

class Clock {
    var gameTime = 0.0; private set
    val gameTimef get() = gameTime.toFloat()

    var deltaTime: Double = -1.0; private set
    val deltaTimef get() = deltaTime.toFloat()

    var timeModifier = 1.0

    private var lastStaticTickGameTime = gameTime
    private var lastFrameTrueTime = glfwGetTime()

    val tickListeners = ListenerManager<Unit>()
    val staticTickListeners = ListenerManager<Unit>()

    fun runWhile(shouldContinue: () -> Boolean) {
        while(shouldContinue()) {
            deltaTime = min((getTrueTime() - lastFrameTrueTime) * timeModifier, 0.1)
            lastFrameTrueTime = getTrueTime()
            gameTime += deltaTime

            tickListeners.call(Unit)
            val timeSinceLastStaticTick = gameTime - lastStaticTickGameTime
            if(timeSinceLastStaticTick > 1/(24f * timeModifier)) {
                lastStaticTickGameTime += Math.floor(timeSinceLastStaticTick.toFloat() * (24f * timeModifier)) / (24f * timeModifier)
                staticTickListeners.call(Unit)
            }
        }
    }

    fun getTrueTime() = glfwGetTime()
    fun getTrueTimef() = getTrueTime().toFloat()
}