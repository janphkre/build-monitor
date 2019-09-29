package de.janphkre.buildmonitor.util

object Hashing {
    private val previousHashes = IntArray(Hashable.values().size)

    fun previousHash(key: Hashable) : Int? {
        return previousHashes[key.ordinal]
    }

    fun newHash(key: Hashable, hashCode: Int) {
        previousHashes[key.ordinal] = hashCode
    }

    //TODO: SERIALIZE & DESERIALIZE FROM DISK.
}