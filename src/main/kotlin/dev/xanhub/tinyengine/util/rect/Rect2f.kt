package dev.xanhub.tinyengine.util.rect

import org.joml.Vector2f
import kotlin.math.max
import kotlin.math.min

open class Rect2f(val p1: Vector2f, val p2: Vector2f) {
    val left = min(p1.x, p2.x)
    val right = max(p1.x, p2.x)
    val top = min(p1.y, p2.y)
    val bottom = max(p1.y, p2.y)

    fun isInside(p: Vector2f): Boolean {
        return right > p.x && left <= p.x &&
                bottom > p.y && top <= p.y
    }
}