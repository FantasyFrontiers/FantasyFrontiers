package net.fantasyfrontiers.data.model.extras

/**
 * The `Translatable` interface represents an object that can provide a translation key.
 *
 * Implementing this interface allows an object to be translatable and provide a translation key.
 *
 * The translation key is used for retrieving the corresponding translation text from a translation
 * resource bundle, file, or database.
 */
interface Translatable {

    /**
     * Retrieves the translation key for the implementing class.
     *
     * @return The translation key as a string.
     */
    fun getTranslationKey(): String

}