package dev.xanhub.tinyengine.util.rect

import org.joml.Vector2i
import kotlin.math.max
import kotlin.math.min

open class Rect2i(val p1: Vector2i, val p2: Vector2i) {
    val left get() = min(p1.x, p2.x)
    val right get() = max(p1.x, p2.x)
    val top get() = min(p1.y, p2.y)
    val bottom get() = max(p1.y, p2.y)
    val width get() = right - left
    val height get() = bottom - top

    fun isInside(p: Vector2i): Boolean {
        return p.x in left until right &&
                p.y in top until bottom
    }
}