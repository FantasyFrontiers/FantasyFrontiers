package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.utils.DatabaseMigrator

fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        if (args[0] == "migrate") {
            DatabaseMigrator().migrate()
            return
        }
    }

    FantasyFrontiers()
}