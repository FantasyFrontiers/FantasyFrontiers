package net.fantasyfrontiers.data.model.player

import kotlinx.serialization.Serializable

/**
 * Represents a noble title.
 *
 * @property nameKey The translation key used to retrieve the localized name of the noble title.
 * @property titleKey The translation key used to retrieve the localized title of the noble title.
 */
@Serializable
enum class NobleTitle {
    KNIGHT,
    BARON,
    COUNT,
    MARGRAVE,
    LANDGRAVE,
    DUKE,
    ELECTOR,
    GRAND_DUKE,
    ARCHDUKE,
    KING,
    EMPEROR;

    val nameKey = "noble.rank.${name.lowercase()}"

    // If now title translation is found, the name is used instead.
    val titleKey = "noble.title.${name.lowercase()}"
}