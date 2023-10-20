package de.coasterfreak.fantasyfrontiers.data.model.items

/**
 * Represents an item in the system.
 *
 * @property name The name of the item.
 * @property maxItemStack The maximum number of instances of the item that can be stacked together. Default value is 99.
 * @property translationKey The translation key used for displaying the item's name. Default value is "item.$name".
 */
data class Item(val name: String, val maxItemStack: Int = 99, val translationKey: String = "item.$name") {

    /**
     * Placeholder method for executing the use functionality of an item.
     *
     * This method is intended to be implemented in a subclass to define the specific behavior of the use functionality for the item.
     *
     * Usage:
     * To use an item, call the `use()` method on an instance of the item. The specific behavior will be executed based on the implementation in the subclass.
     *
     * Example:
     * ```kotlin
     * val myItem = MyItem()
     * myItem.use()
     * ```
     *
     * @see Item
     */
    fun use() {
        // Placeholder for the use method
    }

    /**
     * Displays the item information.
     *
     * This method is responsible for displaying the information of an item. It can be implemented in a subclass to define the specific behavior of how the item information is displayed.
     *
     * Usage:
     * Call the `display()` method on an instance of the item to display its information. The specific behavior will be executed based on the implementation in the subclass.
     *
     * Example:
     * ```kotlin
     * val myItem = MyItem()
     * myItem.display()
     * ```
     *
     * @see Item
     */
    fun display() {
        // Placeholder for displaying item information
    }

    // More utility methods can be added as needed
}
