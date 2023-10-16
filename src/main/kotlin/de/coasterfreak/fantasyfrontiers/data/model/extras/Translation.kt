package de.coasterfreak.fantasyfrontiers.data.model.extras

import kotlinx.serialization.Serializable

/**
 * Represents a translation of a message in a specific language.
 *
 * @param languageCode The language code of the translation as dash-combined ISO-639 (language) and ISO-3166 (country). The Default value is "en-US".
 * @param messageKey The key identifying the message.
 * @param message The translated message.
 */
@Serializable
data class Translation(
    val languageCode: String = "en-US",
    val messageKey: String,
    val message: String,
)
