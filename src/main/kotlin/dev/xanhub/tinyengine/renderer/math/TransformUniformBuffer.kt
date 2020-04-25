package dev.xanhub.tinyengine.renderer.math

import dev.xanhub.tinyengine.renderer.shader.Shader
import dev.xanhub.tinyengine.renderer.shader.uniformbuffer.Uniform
import dev.xanhub.tinyengine.renderer.shader.uniformbuffer.UniformBuffer
import org.joml.Matrix4fc

class TransformUniformBuffer(shader: Shader, val transform: Transform)
    : UniformBuffer(shader, "ModelData") {
    val uniforms = Uniform.listOf(shader, "modelTransform", "normalTransform")

    init {
        transform.editListener.add(Int.MAX_VALUE) {
            needsUpdate = true
        }
    }

    private var needsUpdate: Boolean = true

    override fun bind(bindingPoint: Int) {
        if(needsUpdate) {
            setModelTransform(transform.globalTransformMatrix)
            setNormalTransform(transform.normalTransformMatrix)
        }
        super.bind(bindingPoint)
    }

    private fun setModelTransform(m: Matrix4fc) = this.set(uniforms[0], m)

    private fun setNormalTransform(m: Matrix4fc) = this.set(uniforms[1], m)
}