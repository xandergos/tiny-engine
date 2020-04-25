package dev.xanhub.tinyengine.renderer.math

import dev.xanhub.tinyengine.ListenerManager
import org.joml.*

open class Transform(
    localTranslation: Vector3fc = Vector3f(),
    localRotation: Quaternionfc = Quaternionf(),
    localScale: Vector3fc = Vector3f(1f)
): Transformable {
    data class TransformEditEvent(val transform: Transform, val property: TransformProperty)

    val editListener by lazy { ListenerManager<TransformEditEvent>() }
    override val transform: Transform get() = this

    open var localTranslation: Vector3fc = localTranslation
        set(value) {
            _normalTransformMatrix = null
            _globalTransformMatrix = null
            field = value
            editListener.call(TransformEditEvent(this, TransformProperty.TRANSLATION))
        }
    open var localRotation: Quaternionfc = localRotation
        set(value) {
            _normalTransformMatrix = null
            _globalTransformMatrix = null
            field = value
            editListener.call(TransformEditEvent(this, TransformProperty.ROTATION))
        }
    open var localScale: Vector3fc = localScale
        set(value) {
            _normalTransformMatrix = null
            _globalTransformMatrix = null
            field = value
            editListener.call(TransformEditEvent(this, TransformProperty.SCALE))
        }
    open var origin: Vector3f = Vector3f()
        set(value) {
            _normalTransformMatrix = null
            _globalTransformMatrix = null
            field = value
            editListener.call(TransformEditEvent(this, TransformProperty.ORIGIN))
        }

    var parent: Transform? = null
        set(value) {
            (field?.children as? HashSet)?.remove(this)
            (value?.children as? HashSet)?.add(this)
            field = value
        }
    val children: Iterable<Transform> = HashSet()

    private var _normalTransformMatrix: Matrix4fc? = null
    val normalTransformMatrix: Matrix4fc
        get() {
            if(_normalTransformMatrix == null) {
                _normalTransformMatrix = globalTransformMatrix.invert(Matrix4f()).transpose(Matrix4f())
            }
            return _normalTransformMatrix!!
        }

    private var _globalTransformMatrix: Matrix4fc? = null
    val globalTransformMatrix: Matrix4fc
        get() {
            if(_globalTransformMatrix == null) {
                _globalTransformMatrix =
                    parent?.globalTransformMatrix?.mul(toLocalTransformMatrix(), Matrix4f()) ?: toLocalTransformMatrix()
            }
            return _globalTransformMatrix!!
        }

    val globalTranslation: Vector3f get() = globalTransformMatrix.getTranslation(Vector3f()).add(origin)

    val globalRotation: Quaternionf get() = globalTransformMatrix.getUnnormalizedRotation(Quaternionf())

    val globalScale: Vector3f get() = globalTransformMatrix.getScale(Vector3f())

    fun toLocalTransformMatrix(): Matrix4f = Matrix4f()
            .translationRotateScale(localTranslation, localRotation, localScale).translate(origin.negate(Vector3f()))

    fun fromTransformMatrix(matrix: Matrix4fc) {
        localTranslation = matrix.getTranslation(Vector3f())
        localRotation = matrix.getUnnormalizedRotation(Quaternionf())
        localScale = matrix.getScale(Vector3f())
    }

    override fun toString(): String {
        return "Transform(localTranslation=$localTranslation, localRotation=$localRotation, localScale=$localScale)"
    }
}