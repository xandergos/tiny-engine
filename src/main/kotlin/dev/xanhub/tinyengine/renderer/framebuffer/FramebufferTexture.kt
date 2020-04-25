package dev.xanhub.tinyengine.renderer.framebuffer

import dev.xanhub.tinyengine.renderer.material.Texture
import org.lwjgl.opengl.GL11.glGenTextures

class FramebufferTexture(val frameBuffer: FrameBuffer): Texture(glGenTextures()) {

}