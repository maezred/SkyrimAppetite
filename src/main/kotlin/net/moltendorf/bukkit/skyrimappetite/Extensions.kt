package net.moltendorf.bukkit.skyrimappetite

import net.moltendorf.bukkit.skyrimappetite.settings.GlobalSettings
import org.bukkit.Server
import org.bukkit.configuration.Configuration
import org.bukkit.scheduler.BukkitScheduler
import java.util.logging.Logger

/**
 * Created by moltendorf on 16/6/23.
 *
 * @author moltendorf
 */
internal val enabled: Boolean
  get() = SkyrimAppetite.enabled

internal val instance: SkyrimAppetite
  get() = SkyrimAppetite.instance

internal val config: Configuration
  get() = instance.config

internal val log: Logger
  get() = instance.logger

internal val server: Server
  get() = instance.server

internal val scheduler: BukkitScheduler
  get() = server.scheduler

internal val settings: GlobalSettings
  get() = instance.settings
