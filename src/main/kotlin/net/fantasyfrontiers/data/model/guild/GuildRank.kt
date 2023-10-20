package net.fantasyfrontiers.data.model.guild

import kotlinx.serialization.Serializable

/**
 * Represents a guild rank with an associated experience requirement.
 *
 * @property xpNeeded The experience required to achieve this guild rank.
 */
@Serializable
enum class GuildRank(val xpNeeded: Long) {
    G(0),
    F(100),
    E(500),
    D(1200),
    C(2500),
    B(5000),
    A(10000),
    S(25000),
    SS(100000)
}