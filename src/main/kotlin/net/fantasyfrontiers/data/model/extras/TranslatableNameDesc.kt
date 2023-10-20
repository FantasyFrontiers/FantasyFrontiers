package net.fantasyfrontiers.data.model.extras

/**
 * The `TranslatableNameDesc` interface represents an object that can provide translatable name and description.
 * It extends the `Translatable` interface, which allows an object to provide a translation key.
 * Implementing this interface allows an object to provide a translation key for the name and description,
 * which can be used to retrieve the corresponding translation texts.
 */
interface TranslatableNameDesc : Translatable {

    /**
     * Retrieves the translatable name of an object.
     *
     * This method returns the translation key followed by the suffix ".name".
     * The translation key is retrieved from the implementing class by calling the method `getTranslationKey()`.
     * The translation key is used to retrieve the corresponding translation text from a translation resource bundle,
     * file, or database.
     *
     * @return The translatable name as a string.
     */
    fun getTranslatableName(): String {
        return "${getTranslationKey()}.name"
    }

    /**
     * Retrieves the translatable description for the implementing class.
     *
     * This method concatenates the translation key returned by the [getTranslationKey] method with the ".description" suffix.
     * This resulting string is used to retrieve the corresponding translation text from a translation resource bundle, file, or database.
     *
     * @return The translatable description as a string.
     */
    fun getTranslatableDescription(): String {
        return "${getTranslationKey()}.description"
    }

}