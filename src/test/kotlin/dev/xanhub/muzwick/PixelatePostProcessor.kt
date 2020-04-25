package dev.xanhub.muzwick

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.framebuffer.FrameBuffer
import dev.xanhub.tinyengine.renderer.shader.PostProcessor
import dev.xanhub.tinyengine.util.Resource
import org.joml.Vector2f

object PixelatePostProcessor: PostProcessor(Resource.get("shaders/postprocessor/fragment.glsl")) {
    var pixelSize = 1
    var colorBits = 255

    override fun updateUniforms(window: Window, scene: Scene, framebuffer: FrameBuffer) {
        super.updateUniforms(window, scene, framebuffer)
        this.setUniformVec2f("windowSize", Vector2f(window.size))
        this.setUniformInt("pixelSize", pixelSize)
        this.setUniformInt("colorBits", colorBits)
    }
}