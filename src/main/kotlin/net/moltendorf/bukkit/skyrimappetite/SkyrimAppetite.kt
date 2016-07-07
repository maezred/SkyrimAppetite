package net.moltendorf.bukkit.skyrimappetite

import net.moltendorf.bukkit.skyrimappetite.settings.GlobalSettings
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by moltendorf on 2016-02-28.
 *
 * @author moltendorf
 */
class SkyrimAppetite : JavaPlugin() {
  // Variable data.
  lateinit var listeners: Listeners
    private set
  lateinit var settings: GlobalSettings
    private set

  override fun onEnable() {
    instance = this

    // Construct new settings.
    settings = GlobalSettings()

    // Are we enabled?
    enabled = settings.enabled

    if (enabled) {
      // Register listeners.
      listeners = Listeners()
      server.pluginManager.registerEvents(listeners, this)
      log.info("Enabled general listeners.")
    }
  }

  override fun onDisable() {
    settings.save()

    enabled = false
  }

  companion object {
    var enabled = false
      private set

    lateinit var instance: SkyrimAppetite
      private set
  }
}
