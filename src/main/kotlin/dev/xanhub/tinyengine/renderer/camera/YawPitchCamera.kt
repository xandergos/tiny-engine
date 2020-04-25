package dev.xanhub.tinyengine.renderer.camera

import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3fc

class YawPitchCamera(position: Vector3fc, yaw: Float, pitch: Float, screenSize: Vector2i, fov: Float)
    : Camera(position, Vector3f(1f, 0f, 0f), screenSize, fov) {
    var yaw =  0f
        set(value) {
            field = value
            this.setDirection(yaw, pitch)
        }
    var pitch = 0f
        set(value) {
            field = value
            this.setDirection(yaw, pitch)
        }

    init {
        this.yaw = yaw
        this.pitch = pitch
        this.setDirection(yaw, pitch)
    }
}