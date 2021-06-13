package dev.lysithea.franziska.commands.administration

import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.FranziskaFeature
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxSubCommand
import dev.lysithea.franziska.core.command.interfaces.SubCommandRegistry
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.UsageArea
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeatureSettingsSyntaxCommand : AbstractSyntaxCommand(), SubCommandRegistry<AbstractSyntaxCommand> {
    override val name = "feature"
    override val description = "adjust features"
    override val permission = PermissionLevel.OWNER
    override val category = CommandCategory.ADMINISTRATION
    override val usageArea = UsageArea.GUILD_MESSAGE
    override val subCommands = mutableMapOf<String, AbstractSyntaxCommand>()

    init {
        registerSubCommands(
            EnableFranziskaFeatureCommand(),
            DisableFranziskaFeatureCommand(),
            HasFranziskaFeatureCommand()
        )
    }

    override suspend fun execute(context: SyntaxContext) {
        if (!context.args.any()) {
            context.respond(
                Embeds.error(
                    "No subcommand specified",
                    "A valid subcommand must be applied."
                )
            )
            return
        }

        executeSubCommand(context);
    }

    private inner class EnableFranziskaFeatureCommand : AbstractSyntaxSubCommand(this), KoinComponent {
        override val name = "enable"
        override val description = "enables a feature for the current guild"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.count() != 1 || context.args[0].isBlank()) {
                context.respond(Embeds.error("Invalid feature", "No feature supplied."))
                return
            }

            val feature = FranziskaFeature.values()
                .firstOrNull { it.name.lowercase() == context.args[0].lowercase() }
            if (feature == null) {
                context.respond(Embeds.error("Invalid feature", "Given feature does not exist."))
                return
            }

            val dataService by inject<DataService>()
            val mutableFeatures = context.guildSettings.enabledFeatures.toMutableSet();
            mutableFeatures.add(feature)
            val updatedSettings = context.guildSettings.copy(enabledFeatures = mutableFeatures)
            dataService.settings.upsert(updatedSettings)
            context.respond(
                Embeds.success(
                    "Features updated",
                    "Successfully enabled ${feature.name} features for ${context.member.guild.asGuild().name}."
                )
            )
        }
    }

    private inner class DisableFranziskaFeatureCommand : AbstractSyntaxSubCommand(this), KoinComponent {
        override val name = "disable"
        override val description = "disables a feature for the current guild"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.count() != 1 || context.args[0].isBlank()) {
                context.respond(Embeds.error("Invalid feature", "No feature supplied."))
                return
            }

            val feature = FranziskaFeature.values()
                .firstOrNull { it.name.lowercase() == context.args[0].lowercase() }
            if (feature == null) {
                context.respond(Embeds.error("Invalid feature", "Given feature does not exist."))
                return
            }

            val dataService by inject<DataService>()
            val mutableFeatures = context.guildSettings.enabledFeatures.toMutableSet();
            mutableFeatures.remove(feature)
            val updatedSettings = context.guildSettings.copy(enabledFeatures = mutableFeatures)
            dataService.settings.upsert(updatedSettings)
            context.respond(
                Embeds.success(
                    "Features updated",
                    "Successfully disabled ${feature.name} features for ${context.member.guild.asGuild().name}."
                )
            )
        }
    }

    private inner class HasFranziskaFeatureCommand : AbstractSyntaxSubCommand(this) {
        override val name = "has"
        override val description = "checks if feature is enabled"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.count() != 1 || context.args[0].isBlank()) {
                context.respond(Embeds.error("Feature invalid", "No feature supplied."))
                return
            }

            val feature = FranziskaFeature.values().firstOrNull { it.name.lowercase() == context.args[0].lowercase() }
            if (feature == null) {
                context.respond(Embeds.error("Invalid feature", "Given feature does not exist."))
                return
            }

            context.respond(
                Embeds.info(
                    "Feature status",
                    "${feature.name} features are ${if (context.hasFeature(feature)) "enabled" else "disabled"} for this server."
                )
            )
        }
    }
}