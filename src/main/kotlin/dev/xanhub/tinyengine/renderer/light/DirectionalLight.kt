package dev.xanhub.tinyengine.renderer.light

import org.joml.Vector3f

data class DirectionalLight(
    val direction: Vector3f,
    val ambient: Vector3f,
    val diffuse: Vector3f,
    val specular: Vector3f
) {
    init {
        direction.normalize()
    }
}