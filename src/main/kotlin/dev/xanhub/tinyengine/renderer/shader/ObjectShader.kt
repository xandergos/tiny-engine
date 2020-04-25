package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import java.io.File

abstract class ObjectShader(vertex: File, fragment: File) : Shader(vertex, fragment) {
    constructor(vertexPath: String, fragmentPath: String): this(File(vertexPath), File(fragmentPath))

    abstract fun updateUniforms(scene: Scene, gameObject: GameObject)
}