package dev.xanhub.tinyengine

class ListenerManager<T> {
    data class Listener<T>(val priority: Int, val callback: (T) -> Unit)
    private val listeners = ArrayList<Listener<T>>()

    fun add(priority: Int, callback: (T) -> Unit) {
        var i =  listeners.binarySearch(0, listeners.size) { l ->
            when {
                l.priority < priority -> 1
                l.priority == priority -> 0
                else -> -1
            }
        }
        if(i < 0) i = -(i + 1)
        listeners.add(i, Listener(priority, callback))
    }

    fun remove(callback: (T) -> Unit, priority: Int): Boolean {
        val startingIndex = listeners.binarySearch(0, listeners.size) { l ->
            when {
                l.priority < priority -> 1
                l.priority == priority -> 0
                else -> -1
            }
        }

        var i = startingIndex
        while(i >= 0 && listeners[i].priority == priority) {
            if(listeners[i].callback == callback) {
                listeners.removeAt(i)
                return true
            }
            i--
        }

        i = startingIndex + 1
        while(i < listeners.size && listeners[i].priority == priority) {
            if(listeners[i].callback == callback) {
                listeners.removeAt(i)
                return true
            }
            i++
        }

        return false
    }

    fun remove(callback: (T) -> Unit): Boolean {
        val i = listeners.indexOfFirst { it.callback == callback }
        if(i == -1) return false

        listeners.removeAt(i)
        return true
    }

    fun call(event: T) {
        for(l in listeners)
            l.callback(event)
    }
}