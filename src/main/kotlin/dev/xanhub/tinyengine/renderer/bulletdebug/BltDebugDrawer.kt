package dev.xanhub.tinyengine.renderer.bulletdebug

import com.bulletphysics.linearmath.IDebugDraw
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.mesh.LineDrawer
import dev.xanhub.tinyengine.renderer.mesh.MeshLoader
import dev.xanhub.tinyengine.renderer.mesh.VertexAttribute
import dev.xanhub.tinyengine.renderer.mesh.VertexBuffer
import dev.xanhub.tinyengine.renderer.shader.FlatShader
import dev.xanhub.tinyengine.util.Resource
import org.joml.Vector3f
import org.lwjgl.assimp.Assimp
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import java.awt.Color
import javax.vecmath.Vector3f as JVector3f

private val sphereMesh = MeshLoader.loadSingleMinimal(
    Resource.get("meshes/circle.obj"),
    flags = MeshLoader.defaultFlags or Assimp.aiProcess_GenSmoothNormals or Assimp.aiProcess_DropNormals
)

private val lineVB = object: VertexBuffer(2, arrayOf(VertexAttribute.VEC3F)) {
    init {
        this.put(Vector3f())
        this.put(Vector3f(0f, 0f, 1f))
    }

    override fun getPosition(vertexIndex: Int): Vector3f {
        return this.getVector3f(0, 0)
    }
}
private val lineArrayObj = glGenVertexArrays().apply {
    glBindVertexArray(this)
    lineVB.bindToVBO()
    glBindVertexArray(0)
}

class BltDebugDrawer(val window: Window, val scene: Scene, private var debugMode: Int): IDebugDraw() {
    override fun getDebugMode(): Int = this.debugMode

    override fun setDebugMode(debugMode: Int) {
        this.debugMode = debugMode
    }

    override fun drawContactPoint(
        pointOnB: JVector3f,
        normalOnB: JVector3f,
        distance: Float,
        lifeTime: Int,
        color: JVector3f
    ) {
        val v = Vector3f(pointOnB.x, pointOnB.y, pointOnB.z)
        val c = Color(color.x * 255, color.y * 255, color.z * 255)
        sphereMesh.drawFlatShader(scene, c, TransformBuffered(FlatShader, localTranslation = v))
    }

    override fun drawLine(from: JVector3f, to: JVector3f, color: JVector3f) {
        LineDrawer.draw(
                scene,
                Vector3f(from.x, from.y, from.z),
                Vector3f(to.x, to.y, to.z),
                Vector3f(color.x, color.y, color.z)
        )
    }

    override fun reportErrorWarning(warningString: String?) {
        println("BULLET ERROR!")
        println(warningString)
    }

    override fun draw3dText(location: JVector3f?, textString: String?) {

    }
}