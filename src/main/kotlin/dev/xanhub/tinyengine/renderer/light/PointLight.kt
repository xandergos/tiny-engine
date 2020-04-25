package dev.xanhub.tinyengine.renderer.light

import org.joml.Vector3f

class PointLight(
    val position: Vector3f,
    val c0: Float,
    val c1: Float,
    val c2: Float,
    val ambient: Vector3f,
    val diffuse: Vector3f,
    val specular: Vector3f
)