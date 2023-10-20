package net.fantasyfrontiers.data.model.player

import kotlinx.serialization.Serializable

/**
 * Represents the context of a conversation.
 */
@Serializable
enum class ConversationContext {
    FRIENDSHIP,
    FORMAL
}