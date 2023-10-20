package de.coasterfreak.fantasyfrontiers.data.model.items
class Item(private var _name: String, private var _translationKey: String, private var _maxItemStack: Int = 99) {

    var name: String
        get() = _name
        set(value) {
            _name = value
        }

    var translationKey: String
        get() = _translationKey
        set(value) {
            _translationKey = value
        }

    var maxItemStack: Int
        get() = _maxItemStack
        set(value) {
            _maxItemStack = value
        }

    // Utility methods can be added here
    fun use() {
        // Placeholder for the use method
    }

    fun display() {
        // Placeholder for displaying item information
    }

    // More utility methods can be added as needed
}
