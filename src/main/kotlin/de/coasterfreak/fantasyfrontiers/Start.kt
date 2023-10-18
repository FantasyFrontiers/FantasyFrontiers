package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.utils.DatabaseMigrator
import de.coasterfreak.fantasyfrontiers.utils.TownImageGenerator

/**
 * The main entry point of the application.
 *
 * @param args The command line arguments.
 */
fun main(args: Array<String>) {

    when (args.getOrNull(0)) {
        "migrate" -> DatabaseMigrator().migrate()
        "images" -> TownImageGenerator().generate()
        else -> FantasyFrontiers()
    }
}