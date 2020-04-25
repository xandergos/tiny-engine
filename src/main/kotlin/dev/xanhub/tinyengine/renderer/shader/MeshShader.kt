package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.mesh.VertexBuffer
import java.io.File

abstract class MeshShader(vertex: File, fragment: File) : ObjectShader(vertex, fragment) {
    constructor(vertexPath: String, fragmentPath: String): this(File(vertexPath), File(fragmentPath))

    abstract fun updateUniforms(scene: Scene, mesh: Mesh<out VertexBuffer>, material: Material, transform: TransformBuffered)
}