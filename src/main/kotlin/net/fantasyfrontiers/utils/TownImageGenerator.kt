package net.fantasyfrontiers.utils

import net.fantasyfrontiers.data.model.town.Towns
import dev.fruxz.ascend.extension.getResourceOrNull
import dev.fruxz.ascend.extension.logging.getItsLogger
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

/**
 * TownImageGenerator is a class that generates town images based on a specified map and town data.
 *
 * The generated images represent individual towns extracted from the full map image. The class also performs
 * pre-generation tasks such as checking for necessary resources and creating directories for storing the generated images.
 * After the images are generated, the class performs post-generation tasks such as disconnecting from the database and logging completion
 * messages.
 *
 * @property fullMap The full map image.
 * @property townsDir The directory path for storing generated town images.
 */
class TownImageGenerator {

    /**
     * Represents a variable that holds the full map image.
     *
     * @property fullMap The full map image.
     */
    private val fullMap = getResourceOrNull("assets/map.png")
    /**
     * Holds the directory path for storing generated town images.
     *
     * The `townsDir` property is a private property that represents the directory path for storing
     * generated town images. It is initialized with the value of `fullMap.parent.toFile().resolve("towns").also { it.mkdirs() }`.
     * The property is of type `File`.
     *
     * Usage example:
     * ```
     * private fun preGenerate() {
     *    if (!townsDir.exists() || !townsDir.isDirectory) {
     *        println("towns directory could not be created!")
     *        return
     *    }
     *    ...
     * }
     *
     * fun generate() {
     *    ...
     *    val subImageFile = File(townsDir, "${town.name}.png").also { it.createNewFile() }
     *    ...
     * }
     *
     * private fun postGenerate() {
     *    ...
     *    getItsLogger().info("Finished generating town images. (${townsDir.absolutePath})")
     * }
     * ```
     */
    private val townsDir = fullMap!!.parent.toFile().resolve("towns").also { it.mkdirs() }


    init {
       preGenerate()
    }

    /**
     * Performs pre-generation tasks before generating town images.
     * If the necessary resources are not found or directories cannot be created, appropriate error messages are printed.
     * Connects to the database.
     */
    private fun preGenerate() {
        if (fullMap == null) {
            println("map.png not found!")
            return
        }
        if (!townsDir.exists() || !townsDir.isDirectory) {
            println("towns directory could not be created!")
            return
        }
        DatabaseConnection.connect()
    }

    /**
     * Generates town images based on the specified map and town data.
     * Each town is represented by a sub-image extracted from the full map image.
     */
    fun generate() {
        val image = ImageIO.read(fullMap!!.toFile())
        val towns = Towns.towns

        towns.forEach { town ->
            val x = (town.coords.x * SCALE_MODIFIER) - (IMAGE_WIDTH / 2)
            val y = (town.coords.y * SCALE_MODIFIER) - (IMAGE_HEIGHT / 2)
            getItsLogger().info("Generating image for town ${town.name} at ($x, $y)")

            val subImage = image.getSubimage(x.roundToInt(), y.roundToInt(), IMAGE_WIDTH, IMAGE_HEIGHT)
            val subImageFile = File(townsDir, "${town.name}.png").also { it.createNewFile() }
            ImageIO.write(subImage, "png", subImageFile)
        }

        postGenerate()
    }

    /**
     * Performs the necessary operations after generating town images.
     * This method disconnects from the database and logs a message indicating completion.
     */
    private fun postGenerate() {
        DatabaseConnection.disconnect()

        getItsLogger().info("Finished generating town images. (${townsDir.absolutePath})")
    }

    /**
     * The Companion class contains constants related to image generation for towns.
     *
     * @property IMAGE_WIDTH The width of the generated town image.
     * @property IMAGE_HEIGHT The height of the generated town image.
     * @property SCALE_MODIFIER The scale modifier used when generating the town image.
     */
    companion object {
        const val IMAGE_WIDTH = 800
        const val IMAGE_HEIGHT = 500
        const val SCALE_MODIFIER = 5
    }

}