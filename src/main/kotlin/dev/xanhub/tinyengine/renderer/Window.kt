package dev.xanhub.tinyengine.renderer

import dev.xanhub.tinyengine.ListenerManager
import org.joml.Vector2i
import org.joml.Vector2ic
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import kotlin.math.floor

class Window(
    initialSize: Vector2ic,
    name: String
) {
    data class CursorMoveEvent(val prev: Vector2ic?, val new: Vector2ic)
    data class ResizeEvent(val prevSize: Vector2ic?, val newSize: Vector2ic)

    private var lastCursorPosition: Vector2ic? = null

    var id: Long = 0; private set

    val cursorMoveListeners = ListenerManager<CursorMoveEvent>()

    val resizeEventListeners = ListenerManager<ResizeEvent>()

    val size: Vector2i
        get() {
            var width: Int? = null
            var height: Int? = null
            stackPush().use { stack ->
                val pWidth = stack.mallocInt(1)
                val pHeight = stack.mallocInt(1)
                glfwGetWindowSize(id, pWidth, pHeight)
                width = pWidth.get()
                height = pHeight.get()
            }
            return Vector2i(width!!, height!!)
        }

    init {
        if(!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        val primaryMonitor = glfwGetPrimaryMonitor()
        assert(primaryMonitor != NULL)
        val vidMode = glfwGetVideoMode(primaryMonitor) ?: throw IllegalStateException("Failed to get monitor video mode.")

        id = glfwCreateWindow(initialSize.x(), initialSize.y(), name, NULL, NULL).takeIf { it != NULL }
            ?: throw RuntimeException("Failed to create window")

        glfwSetWindowPos(id, vidMode.width() / 2 - size.x / 2, vidMode.height() / 2  - size.y / 2)

        glfwMakeContextCurrent(id)
        glfwShowWindow(id)

        glfwSetInputMode(id, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE)
        glfwSetCursorPosCallback(id) { _, x, y ->
            cursorMoveListeners.call(CursorMoveEvent(lastCursorPosition, Vector2i(x.toInt(), y.toInt())))
            lastCursorPosition = Vector2i(x.toInt(), y.toInt())
        }
        glfwSwapInterval(0)

        GL.createCapabilities()
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun draw(drawable: Drawable) {
        glfwMakeContextCurrent(id)
        drawable.draw(this)
    }

    fun setClearColor(r: Float, g: Float, b: Float) {
        glClearColor(r, g, b, 1f)
    }

    fun clear() {
        glClear( GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun hideCursor() {
        glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    }

    fun unhideCursor() {
        glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
    }

    fun getCursorPos(): Vector2i {
        var x: Int? = null
        var y: Int? = null
        stackPush().use { stack ->
            val xN = stack.mallocDouble(1)
            val yN = stack.mallocDouble(1)
            glfwGetCursorPos(id, xN, yN)
            x = floor(xN.get()).toInt()
            y = floor(yN.get()).toInt()
        }
        return Vector2i(x!!, y!!)
    }

    fun isOpen(): Boolean {
        return !glfwWindowShouldClose(id)
    }

    fun close() {
        glfwSetWindowShouldClose(id, true)
    }

    fun pollEvents() {
        glfwPollEvents()
    }

    fun display() {
        glfwSwapBuffers(id)
    }

    fun destroy() {
        glfwFreeCallbacks(id)
        glfwDestroyWindow(id)
    }
}