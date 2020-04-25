package dev.xanhub.tinyengine.core.gameobject.sprite

import dev.xanhub.tinyengine.renderer.material.Material

class Animation(val frames: List<Material>, val fps: Float) {
    fun getMaterial(time: Float): Material {
        val i = (time * fps).toLong() % frames.size
        return frames[i.toInt()]
    }
}