package net.fantasyfrontiers.data.model.town

/**
 * The Towns class represents a collection of towns in a fictional world.
 *
 * Each town is represented by a Town object, which contains information such as the town's name,
 * coordinates, type, population, features, and connections to other towns.
 *
 * This class provides easy access to the towns and their information through various properties.
 *
 * Example usage:
 * val portDrizzle = Towns.PORT_DRIZZLE
 * val townName = portDrizzle.name
 * val townType = portDrizzle.type
 * val population = portDrizzle.population
 *
 * Note: The example code above assumes that the Towns class has been imported into the current file.
 */
object Towns {

    val PORT_DRIZZLE = Town(
        name = "Port Drizzle",
        coords = Coords(
            x = 1027.0,
            y = 395.5
        ),
        type = "Naval",
        population = 40404,
        features = Features(
            capital = false,
            citadel = true,
            port = true,
            shanty = true,
            temple = true,
            walls = true,
            blacksmith = true,
            herbGarden = true,
            merchantsGuild = true,
            adventurersGuild = true,
            blacksmithsGuild = true,
            herbologistsGuild = true
        ),
        connections = listOf(Connection(
            name = "Dewgrass Town",
            distance = 760
        ), Connection(
            name = "Riverbend",
            distance = 3757
        ), Connection(
            name = "Streamstone",
            distance = 3597
        ), Connection(
            name = "Rillrun",
            distance = 3700
        ), Connection(
            name = "Lakevale",
            distance = 5000
        ), Connection(
            name = "Wharfwind",
            distance = 1284
        ), Connection(
            name = "Wharfwind:PORT",
            distance = 1438
        ), Connection(
            name = "MistMeadow",
            distance = 2520
        ), Connection(
            name = "Pinegrove Hamlet",
            distance = 2300
        ))
    )

    val LAKEVALE = Town(
        name = "Lakevale",
        coords = Coords(
            x = 1397.77,
            y = 897.97
        ),
        type = "Lake",
        population = 64049,
        features = Features(
            capital = true,
            citadel = true,
            port = false,
            shanty = true,
            temple = true,
            walls = true,
            blacksmith = true,
            herbGarden = true,
            merchantsGuild = true,
            adventurersGuild = true,
            blacksmithsGuild = true,
            herbologistsGuild = true
        ),
        connections = listOf(Connection(
            name = "Brookfield",
            distance = 950
        ), Connection(
            name = "Tideharbor",
            distance = 2074
        ), Connection(
            name = "Rillrun",
            distance = 567
        ), Connection(
            name = "StreamStone",
            distance = 2000
        ), Connection(
            name = "Riverbend",
            distance = 3683
        ), Connection(
            name = "Dewgrass Town",
            distance = 4245
        ), Connection(
            name = "Port Drizzle",
            distance = 5000
        ))
    )

    val RIVERBEND = Town(
        name = "Riverbend",
        coords = Coords(
            x = 1799.5,
            y = 488.0
        ),
        type = "Naval",
        population = 35381,
        features = Features(
            capital = false,
            citadel = true,
            port = false,
            shanty = true,
            temple = true,
            walls = true,
            blacksmith = true,
            herbGarden = true,
            adventurersGuild = true,
            merchantsGuild = true,
        ),
        connections = listOf(Connection(
            name = "Streamstone",
            distance = 1683
        ), Connection(
            name = "Rillrun",
            distance = 2383
        ), Connection(
            name = "Lakevale",
            distance = 3683
        ), Connection(
            name = "Dewgrass Town",
            distance = 2000
        ), Connection(
            name = "Port Drizzle",
            distance = 3757
        ))
    )

    val TIDEHARBOR = Town(
        name = "Tideharbor",
        coords = Coords(
            x = 826.5,
            y = 850.0
        ),
        type = "Naval",
        population = 2608,
        features = Features(
            capital = false,
            citadel = false,
            port = true,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = true,
            herbGarden = false,
            blacksmithsGuild = true,
        ),
        connections = listOf(Connection(
            name = "Lakevale",
            distance = 2074
        ), Connection(
            name = "Brookfield",
            distance = 2800
        ), Connection(
            name = "Waterwillow Town",
            distance = 2083
        ), Connection(
            name = "Port Drizzle:PORT",
            distance = 8438
        ), Connection(
            name = "Wharfwind:PORT",
            distance = 7000
        ))
    )

    val DEWGRASS_TOWN = Town(
        name = "Dewgrass Town",
        coords = Coords(
            x = 1372.79,
            y = 480.68
        ),
        type = "Hunting",
        population = 429,
        features = Features(
            capital = false,
            citadel = false,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = false,
            herbGarden = false,
            adventurersGuild = true,
        ),
        connections = listOf(Connection(
            name = "Riverbend",
            distance = 2000
        ), Connection(
            name = "Streamstone",
            distance = 3245
        ), Connection(
            name = "Rillrun",
            distance = 2945
        ), Connection(
            name = "Lakevale",
            distance = 4245
        ), Connection(
            name = "Port Drizzle",
            distance = 760
        ))
    )

    val WHARFWIND = Town(
        name = "Wharfwind",
        coords = Coords(
            x = 845.5,
            y = 124.0
        ),
        type = "Naval",
        population = 9081,
        features = Features(
            capital = false,
            citadel = true,
            port = true,
            shanty = false,
            temple = false,
            walls = true,
            blacksmith = false,
            herbGarden = true,
            herbologistsGuild = true,
        ),
        connections = listOf(Connection(
            name = "Port Drizzle",
            distance = 1284
        ), Connection(
            name = "Port Drizzle:PORT",
            distance = 1438
        ), Connection(
            name = "MistMeadow",
            distance = 2134
        ), Connection(
            name = "Pinegrove Hamlet",
            distance = 1914
        ), Connection(
            name = "Tideharbor:PORT",
            distance = 7000
        ))
    )

    val STREAMSTONE = Town(
        name = "Streamstone",
        coords = Coords(
            x = 1846.07,
            y = 876.47
        ),
        type = "Hunting",
        population = 2028,
        features = Features(
            capital = false,
            citadel = false,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = false,
            herbGarden = true,
            adventurersGuild = true,
        ),
        connections = listOf(Connection(
            name = "Rillrun",
            distance = 1700
        ), Connection(
            name = "Lakevale",
            distance = 2000
        ), Connection(
            name = "Riverbend",
            distance = 1683
        ), Connection(
            name = "Dewgrass Town",
            distance = 3245
        ), Connection(
            name = "Port Drizzle",
            distance = 3597
        ))
    )

    val MISTMEADOW = Town(
        name = "MistMeadow",
        coords = Coords(
            x = 504.26,
            y = 522.74
        ),
        type = "Hunting",
        population = 26,
        features = Features(
            capital = false,
            citadel = false,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = false,
            herbGarden = false
        ),
        connections = listOf(Connection(
            name = "Wharfwind",
            distance = 2134
        ), Connection(
            name = "Pinegrove Hamlet",
            distance = 820
        ), Connection(
            name = "Port Drizzle",
            distance = 2520
        ))
    )

    val BROOKFIELD = Town(
        name = "Brookfield",
        coords = Coords(
            x = 1322.51,
            y = 1142.44
        ),
        type = "Hunting",
        population = 4117,
        features = Features(
            capital = false,
            citadel = true,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = false,
            herbGarden = true,
            herbologistsGuild = true,
        ),
        connections = listOf(Connection(
            name = "Lakevale",
            distance = 950
        ), Connection(
            name = "Tideharbor",
            distance = 2800
        ))
    )

    val PINEGROVE_HAMLET = Town(
        name = "Pinegrove Hamlet",
        coords = Coords(
            x = 699.32,
            y = 423.24
        ),
        type = "Mining",
        population = 204,
        features = Features(
            capital = false,
            citadel = false,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = true,
            herbGarden = false,
            blacksmithsGuild = true,
            adventurersGuild = true
        ),
        connections = listOf(Connection(
            name = "MistMeadow",
            distance = 820
        ), Connection(
            name = "Wharfwind",
            distance = 1914
        ), Connection(
            name = "Port Drizzle",
            distance = 2300
        ))
    )

    val RILLRUN = Town(
        name = "Rillrun",
        coords = Coords(
            x = 1556.25,
            y = 967.64
        ),
        type = "Generic",
        population = 13161,
        features = Features(
            capital = false,
            citadel = true,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = true,
            herbGarden = false,
            adventurersGuild = true,
        ),
        connections = listOf(Connection(
            name = "Lakevale",
            distance = 567
        ), Connection(
            name = "StreamStone",
            distance = 1700
        ), Connection(
            name = "Riverbend",
            distance = 2383
        ), Connection(
            name = "Dewgrass Town",
            distance = 2945
        ), Connection(
            name = "Port Drizzle",
            distance = 3700
        ))
    )

    val WATERWILLOW_TOWN = Town(
        name = "Waterwillow Town",
        coords = Coords(
            x = 354.67,
            y = 1059.32
        ),
        type = "Hunting",
        population = 3933,
        features = Features(
            capital = false,
            citadel = false,
            port = false,
            shanty = false,
            temple = false,
            walls = false,
            blacksmith = false,
            herbGarden = true
        ),
        connections = listOf(Connection(
            name = "Tideharbor",
            distance = 2083
        ))
    )


    /**
     * Represents a list of towns.
     *
     * @property towns The list of towns.
     */
    val towns = listOf(
        PORT_DRIZZLE,
        LAKEVALE,
        RIVERBEND,
        TIDEHARBOR,
        DEWGRASS_TOWN,
        WHARFWIND,
        STREAMSTONE,
        MISTMEADOW,
        BROOKFIELD,
        PINEGROVE_HAMLET,
        RILLRUN,
        WATERWILLOW_TOWN
    )

    /**
     * Retrieves a town by its name.
     *
     * @param name The name of the town to retrieve.
     * @return The town with the specified name, or null if no town is found.
     */
    fun getByName(name: String): Town {
        return towns.find { it.name == name } ?: throw Exception("Town $name not found.")
    }
}