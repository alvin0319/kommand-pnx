package dev.minjae.pnx.kommand // ktlint-disable filename

import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginCommand
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.command.tree.ParamList
import cn.nukkit.command.tree.node.IParamNode
import cn.nukkit.command.utils.CommandLogger
import cn.nukkit.plugin.PluginBase

typealias KommandDefaultHandler = (CommandSender, String?, Array<out String>) -> Boolean
typealias KommandParamHandler = (CommandSender, String?, MutableMap.MutableEntry<String, ParamList>, CommandLogger) -> Int

/**
 * Creates new [Kommand] object and returns it.
 * @param name The name of the command.
 * @param description The description of the command.
 * @return The created [Kommand] object.
 */
inline fun <reified T : PluginBase> T.kommand(name: String, description: String, block: Kommand.() -> Unit): Kommand {
    return Kommand(name, description, this).apply {
        block(this)
    }
}

/**
 * Adds overload to the [Kommand].
 * @param key The key of the overload.
 */
inline fun <reified T : Kommand> T.overload(key: String, block: KommandOverload.() -> Unit) {
    val overload = KommandOverload()
    block(overload)
    addCommandParameters(key, overload.params.toTypedArray())
}

/**
 * Adds [CommandParameter] to the [Kommand].
 * @param name The name of the parameter.
 * @param type The type of the parameter.
 */
inline fun <reified T : Kommand> T.param(
    name: String,
    type: CommandParamType,
    node: IParamNode<Any>? = null
) {
    addCommandParameters(name, arrayOf(CommandParameter.newType(name, type, node)))
}

/**
 * Adds [CommandParameter] to the [KommandOverload].
 * @param name The name of the parameter.
 */
inline fun <reified T : KommandOverload> T.param(
    name: String,
    type: CommandParamType,
    node: IParamNode<Any>? = null
) {
    params.add(CommandParameter.newType(name, type, node))
}

/**
 * Adds [CommandParameter] to the [KommandOverload].
 * @param name The name of the parameter.
 */
inline fun <reified T : KommandOverload> T.param(
    param: CommandParameter
) {
    params.add(param)
}

inline fun <reified T : Kommand> T.onExecute(noinline execute: KommandDefaultHandler) {
    if (paramHandler != null) {
        throw IllegalStateException("Cannot set default handler when param handler is set")
    }
    defaultHandler = execute
}

inline fun <reified T : Kommand> T.onExecute(noinline execute: KommandParamHandler) {
    if (defaultHandler != null) {
        throw IllegalStateException("Cannot set param handler when default handler is set")
    }
    paramHandler = execute
}

open class Kommand(
    name: String,
    description: String,
    plugin: PluginBase
) : PluginCommand<PluginBase>(name, description, plugin) {

    var defaultHandler: KommandDefaultHandler? = null

    var paramHandler: KommandParamHandler? = null
        set(value) {
            field = value
            if (!hasParamTree()) {
                enableParamTree()
            }
        }

    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<out String>): Boolean {
        return defaultHandler?.invoke(sender, commandLabel, args) ?: false
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String?,
        result: MutableMap.MutableEntry<String, ParamList>,
        log: CommandLogger
    ): Int {
        return paramHandler?.invoke(sender, commandLabel, result, log) ?: 0
    }
}

open class KommandOverload {
    val params: MutableList<CommandParameter> = mutableListOf()
}
