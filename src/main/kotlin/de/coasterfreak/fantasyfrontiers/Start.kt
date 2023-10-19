package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.utils.DatabaseMigrator
import de.coasterfreak.fantasyfrontiers.utils.TownImageGenerator
import io.sentry.Sentry

/**
 * The main entry point of the application.
 *
 * @param args The command line arguments.
 */
fun main(args: Array<String>) {
    Sentry.init { options ->
        options.dsn = "https://a848ccc3e9ffcb404ae6b5681ac419bb@sentry.flawcra.cc/29"
        // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
        // We recommend adjusting this value in production.
        options.tracesSampleRate = 1.0
    }

    when (args.getOrNull(0)) {
        "migrate" -> DatabaseMigrator().migrate()
        "images" -> TownImageGenerator().generate()
        else -> FantasyFrontiers()
    }
}