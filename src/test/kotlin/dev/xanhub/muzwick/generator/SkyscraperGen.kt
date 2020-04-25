package dev.xanhub.muzwick.generator

import com.bulletphysics.collision.shapes.BoxShape
import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.gameobject.GameObjectCollider
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.FileTexture
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.math.Transform
import dev.xanhub.tinyengine.renderer.mesh.DetailedVertexBuffer
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.shader.ObjectShader
import dev.xanhub.tinyengine.util.Resource
import dev.xanhub.tinyengine.util.rect.Rect2i
import org.joml.*
import org.lwjgl.opengl.GL13.*
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f as JVector3f

const val scale = 12f
const val texScale = 1f
val shader = object : ObjectShader(Resource.get("shaders/minimal-fade/vertex.glsl"), Resource.get("shaders/minimal-fade/fragment.glsl")) {
    override fun updateUniforms(scene: Scene, gameObject: GameObject) {
        // Material
        glActiveTexture(GL_TEXTURE0)
        this.setUniformInt("material.diffuse", 0)
        gameObject.material.diffuse.use()
        this.setUniformVec3f("material.specular", gameObject.material.specularColor)
        this.setUniformFloat("material.shininess", gameObject.material.shininess)

        this.setUniformBlock("ModelData", gameObject.transform.uniformBuffer, 0)
        this.setUniformBlock("SceneData", scene.getUniformBuffer(this), 1)
    }
}

private class Template(val skyscraperHeight: Int, p1: Vector2i, p2: Vector2i) : Rect2i(p1, p2)

object SkyscraperGen {
    private val material = Material(
        FileTexture.get(Resource.get("textures/skyscraper.png").absolutePath, useMipmaps = true, minFilter = GL_NEAREST_MIPMAP_LINEAR, magFilter = GL_NEAREST),
        Vector3f(0.0f),
        4f,
        false
    )

    fun generate(scene: Scene, heightMapFile: File): ArrayList<GameObject> {
        val objects = ArrayList<GameObject>()
        val image = ImageIO.read(heightMapFile)

        val templates = ArrayList<Template>()

        for(x in 0 until image.width) {
            for(z in 0 until image.height) {
                if(templates.find { t -> t.isInside(Vector2i(x, z)) } != null) continue

                val skyscraperHeight = Color(image.getRGB(x, z)).red
                if(skyscraperHeight == 0) continue
                var width = 1
                var height = 1
                while(x+width < image.width && Color(image.getRGB(x+width, z)).red == skyscraperHeight) width++
                fun isValidLine(y: Int): Boolean {
                    for(i in x until x+width) {
                        if(Color(image.getRGB(i, y)).red != skyscraperHeight) return false
                        if(templates.find { t -> t.isInside(Vector2i(i, y)) } != null) return false
                    }
                    return true
                }
                while(z+height < image.height && isValidLine(z+height)) height++

                val transform = Transform(localScale = Vector3f(scale, 1f, scale), localTranslation = Vector3f(-40f*scale, 0f, -40f*scale))
                templates.add(Template(skyscraperHeight, Vector2i(x, z), Vector2i(x+width, z+height)))

                val go = GameObject(scene, getMesh(templates.last()), material, shader).apply {
                    this.transform.fromTransformMatrix(transform.globalTransformMatrix)
                }
                val collider = getCollisionObj(templates.last())
                objects.add(go.apply { collisionObject = collider })
            }
        }

        return objects
    }

    /**
     * Returns the unscaled mesh given by the template. UV's are scaled.
     */
    private fun getMesh(template: Template): Mesh<DetailedVertexBuffer> {
        val vertexBuffer = DetailedVertexBuffer(20)
        val faces = ArrayList<Vector3i>()

        fun addTop() {
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), 0f, template.top.toFloat()),
                Vector3f(0f, 0f, -1f),
                Vector2f(0f, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), 0f, template.top.toFloat()),
                Vector3f(0f, 0f, -1f),
                Vector2f(1f * template.width * scale / texScale, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(0f, 0f, -1f),
                Vector2f(0f, 1f * template.skyscraperHeight / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(0f, 0f, -1f),
                Vector2f(template.width.toFloat() * scale / texScale, template.skyscraperHeight.toFloat() / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            faces.add(Vector3i(0, 1, 3))
            faces.add(Vector3i(0, 2, 3))
        }

        fun addLeft() {
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), 0f, template.top.toFloat()),
                Vector3f(-1f, 0f, 0f),
                Vector2f(0f, 0f),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), 0f, template.bottom.toFloat()),
                Vector3f(-1f, 0f, 0f),
                Vector2f(1f * template.height * scale / texScale, 0f),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(-1f, 0f, 0f),
                Vector2f(0f, 1f * template.skyscraperHeight / texScale),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(-1f, 0f, 0f),
                Vector2f(template.height.toFloat() * scale / texScale, template.skyscraperHeight.toFloat() / texScale),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            faces.add(Vector3i(4, 5, 7))
            faces.add(Vector3i(4, 6, 7))
        }

        fun addRight() {
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), 0f, template.top.toFloat()),
                Vector3f(1f, 0f, 0f),
                Vector2f(0f, 0f),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), 0f, template.bottom.toFloat()),
                Vector3f(1f, 0f, 0f),
                Vector2f(1f * template.height * scale / texScale, 0f),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(1f, 0f, 0f),
                Vector2f(0f, 1f * template.skyscraperHeight / texScale),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(1f, 0f, 0f),
                Vector2f(template.height.toFloat() * scale / texScale, template.skyscraperHeight.toFloat() / texScale),
                Vector3f(0f, 0f, 1f),
                Vector3f(0f, 1f, 0f)
            )
            faces.add(Vector3i(8, 9, 11))
            faces.add(Vector3i(8, 10, 11))
        }

        fun addBottom() {
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), 0f, template.bottom.toFloat()),
                Vector3f(0f, 0f, 1f),
                Vector2f(0f, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), 0f, template.bottom.toFloat()),
                Vector3f(0f, 0f, 1f),
                Vector2f(1f * template.width * scale / texScale, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(0f, 0f, 1f),
                Vector2f(0f, 1f * template.skyscraperHeight / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(0f, 0f, 1f),
                Vector2f(template.width.toFloat() * scale / texScale, template.skyscraperHeight.toFloat() / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 1f, 0f)
            )
            faces.add(Vector3i(12, 13, 15))
            faces.add(Vector3i(12, 14, 15))
        }

        fun addRoof() {
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(0f, 1f, 0f),
                Vector2f(0f, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 0f, 1f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.top.toFloat()),
                Vector3f(0f, 1f, 0f),
                Vector2f(template.width.toFloat() * scale / texScale, 0f),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 0f, 1f)
            )
            vertexBuffer.put(
                Vector3f(template.left.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(0f, 1f, 0f),
                Vector2f(0f, template.height.toFloat() * scale / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 0f, 1f)
            )
            vertexBuffer.put(
                Vector3f(template.right.toFloat(), template.skyscraperHeight.toFloat(), template.bottom.toFloat()),
                Vector3f(0f, 1f, 0f),
                Vector2f(template.width.toFloat() * scale / texScale, template.height.toFloat() * scale / texScale),
                Vector3f(1f, 0f, 0f),
                Vector3f(0f, 0f, 1f)
            )
            faces.add(Vector3i(16, 17, 19))
            faces.add(Vector3i(16, 18, 19))
        }

        addTop()
        addLeft()
        addRight()
        addBottom()
        addRoof()

        return Mesh(vertexBuffer, faces)
    }

    private fun getCollisionObj(template: Template): GameObjectCollider {
        val collisionObject = GameObjectCollider(null)
        collisionObject.collisionShape = BoxShape(JVector3f(
            .5f,
            .5f,
            .5f
        ))
        collisionObject.collisionShape.setLocalScaling(JVector3f(
            scale * template.width,
            template.skyscraperHeight.toFloat(),
            scale * template.height
        ))
        val m = scale // i have no idea why this works, but inlining scale does not.
        collisionObject.setWorldTransform(com.bulletphysics.linearmath.Transform(Matrix4f().apply {
            setIdentity()
            setTranslation(JVector3f(
                (template.left + template.width / 2f - 40) * m,
                template.skyscraperHeight / 2f,
                (template.top + template.height / 2f - 40) * m
            ))
        }))
        return collisionObject
    }
}