package dev.xanhub.tinyengine.renderer.framebuffer

import org.joml.Vector2ic
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer

class FrameBuffer(val size: Vector2ic) {
    val id = glGenFramebuffers()
    val rboId = glGenRenderbuffers()
    val texture = FramebufferTexture(this)

    init {
        // FrameBuffer
        glBindFramebuffer(GL_FRAMEBUFFER, id)

        // Texture
        val emptyBuffer: ByteBuffer? = null
        glBindTexture(GL_TEXTURE_2D, texture.id)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, size.x(), size.y(), 0, GL_RGB, GL_UNSIGNED_BYTE, emptyBuffer)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.id, 0)

        // RenderBuffer
        glBindRenderbuffer(GL_RENDERBUFFER, rboId)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, size.x(), size.y())
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboId)

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw IllegalStateException()

        glBindTexture(GL_TEXTURE_2D, 0)
        glBindRenderbuffer(GL_RENDERBUFFER, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun use(lambda: () -> Unit) {
        glBindFramebuffer(GL_FRAMEBUFFER, id)
        lambda()
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun destroy() {
        glDeleteFramebuffers(id)
        glDeleteRenderbuffers(this.rboId)
        this.texture.destroy()
    }
}