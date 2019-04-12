package command

import context.ICommandContext
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import org.reflections.Reflections
import util.Loggers
import util.getSubTypesOf

/**
 * Loader that finds commands in [pathToPackage]
 */
class CommandLoader internal constructor(internal val pathToPackage: String) {
    private val logger = Loggers.getLogger<CommandLoader>()

    /**
     * Find and loads commands with specified [registry] from [pathToPackage]
     */
    fun load(registry: CommandRegistry) {
        logger.info("Начата загрузка модулей...")

        val countModules = Reflections(pathToPackage)
            .getSubTypesOf<ModuleBase<ICommandContext>>()
            .map { registry.register(it.kotlin) }
            .count()

        logger.info("Загружено $countModules модулей")
    }
}

private var commandLoader = CommandLoader("")
var DiscordClient.commandLoader
    get() = command.commandLoader
    private set(value) {
        command.commandLoader = value
    }

var DiscordClientBuilder.commandLoadersPackage: String
    get() = commandLoader.pathToPackage
    set(value) {
        commandLoader = CommandLoader(value)
    }