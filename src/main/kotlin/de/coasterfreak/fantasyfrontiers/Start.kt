package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.utils.DatabaseMigrator

/**
 * The main entry point of the application.
 *
 * @param args The command line arguments.
 */
fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        if (args[0] == "migrate") {
            DatabaseMigrator().migrate()
            return
        }
    }

    FantasyFrontiers()
}