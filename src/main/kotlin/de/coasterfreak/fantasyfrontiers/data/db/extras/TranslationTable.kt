package de.coasterfreak.fantasyfrontiers.data.db.extras

import de.coasterfreak.fantasyfrontiers.data.model.extras.Translation
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This is a singleton object that represents a translation table.
 * It extends the Table class and provides columns for storing translations.
 * It also overrides the primaryKey property to specify the primary key columns.
 *
 * Usage:
 * TranslationTable.selectAll() - Retrieves all translations from the table.
 * TranslationTable.select { condition } - Retrieves translations that match the given condition.
 * TranslationTable.replace { values } - Adds or updates a translation in the table.
 */
object TranslationTable : Table("translations") {

    val languageCode = varchar("language_code", 5).default("en-US")
    val messageKey = varchar("message_key", 255)
    val message = text("message")

    override val primaryKey = PrimaryKey(languageCode, messageKey)

}

/**
 * This method loads all translations from the database and returns them as a map where the language code is the key and the translation object is the value.
 *
 * @return A map where the language code is the key and the translation object is the value.
 */
fun loadAllTranslations() = transaction {
    TranslationTable.selectAll().map {
        Translation(
            it[TranslationTable.languageCode],
            it[TranslationTable.messageKey],
            it[TranslationTable.message]
        )
    }.groupBy { it.languageCode }
}

/**
 * Loads translations for the specified language code.
 *
 * @param languageCode The language code to load translations for. This should be a dash-combined ISO-639 (language) and ISO-3166 (country) code.
 * @return A map of translation message keys to Translation objects.
 */
fun loadTranslations(languageCode: String) = transaction {
    TranslationTable.select {
        TranslationTable.languageCode eq languageCode
    }.map {
        Translation(
            it[TranslationTable.languageCode],
            it[TranslationTable.messageKey],
            it[TranslationTable.message]
        )
    }.associateBy { it.messageKey }
}

/**
 * Adds a translation to the translation table.
 *
 * @param languageCode the language code of the translation
 * @param messageKey the message key of the translation
 * @param message the translated message
 */
fun addTranslation(languageCode: String, messageKey: String, message: String) = transaction {
    TranslationTable.replace {
        it[TranslationTable.languageCode] = languageCode
        it[TranslationTable.messageKey] = messageKey
        it[TranslationTable.message] = message
    }
}