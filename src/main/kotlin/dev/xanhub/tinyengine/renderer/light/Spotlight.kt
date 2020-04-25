package dev.xanhub.tinyengine.renderer.light

import org.joml.Math
import org.joml.Vector3fc

class Spotlight(
    var position: Vector3fc,
    var direction: Vector3fc,
    innerAngle: Float,
    outerAngle: Float,
    var ambient: Vector3fc,
    var diffuse: Vector3fc,
    var specular: Vector3fc
) {
    var innerAngle = innerAngle
        set(value) {
            innerAngleCosine = Math.cos(value)
            field = value
        }
    var innerAngleCosine = Math.cos(innerAngle)
        private set
    var outerAngle = outerAngle
        set(value) {
            outerAngleCosine = Math.cos(value)
            field = value
        }
    var outerAngleCosine = Math.cos(outerAngle)
        private set
}