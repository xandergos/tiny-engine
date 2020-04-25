package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.framebuffer.FrameBuffer
import dev.xanhub.tinyengine.util.Resource
import java.io.File

open class PostProcessor(fragment: File) : Shader(Resource.get("shaders/postprocessor/vertex.glsl"), fragment) {
    constructor(fragmentPath: String): this(File(fragmentPath))

    open fun updateUniforms(window: Window, scene: Scene, framebuffer: FrameBuffer) {
        this.setUniformInt("screenTexture", 0)
        framebuffer.texture.use()
    }
}