package dev.xanhub.tinyengine.core.gameobject

import com.bulletphysics.collision.dispatch.CollisionObject

class GameObjectCollider(owner: GameObject?): CollisionObject() {
    var owner = owner; internal set

    init {
        owner?.collisionObject = this
    }
}