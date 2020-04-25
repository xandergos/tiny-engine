package dev.xanhub.tinyengine.util

/**
 * A hint to developers that only the named functions/classes
 * should be using this function/variable/class.
 */
annotation class FriendsOnly(val friends: Array<String>)