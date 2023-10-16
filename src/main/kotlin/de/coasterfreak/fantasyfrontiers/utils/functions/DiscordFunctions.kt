package de.coasterfreak.fantasyfrontiers.utils.functions

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

fun <T> InteractionHook.withTestPermission(dsl: InteractionHook.() -> T) {
    try {
        dsl()
    }
    catch(e: PermissionException) {
        this.editOriginal("").setEmbeds(
            EmbedBuilder()
                .setTitle("PermissionCheck failed :x:")
                .setDescription("""
                    Missing permissions:
                    ```${e.permission}```
                """.trimIndent())
                .setColor(0xFF3333)
                .build()
        ).queue()
    }
}

fun <T> ComponentInteraction.withTestPermission(dsl: InteractionHook.() -> T) {
    hook.withTestPermission(dsl)
}