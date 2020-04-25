package dev.xanhub.tinyengine.renderer.shader.uniformbuffer

enum class UniformBlockLayout {
    PACKED,
    SHARED,
    STANDARDIZED;

    companion object {
        val DEFAULT get() = SHARED
    }
}