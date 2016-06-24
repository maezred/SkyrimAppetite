package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by moltendorf on 2016-02-28.
 */
class SkyrimAppetite : JavaPlugin() {
    // Variable data.
    var listeners: Listeners? = null
    var settings: Settings? = null

    override fun onEnable() {
        instance = this

        // Construct new settings.
        listeners = Listeners()
        settings = Settings()

        server.pluginManager.registerEvents(listeners, this)
    }

    override fun onDisable() {
        settings!!.save()

        instance = null
    }

    companion object {
        // Main instance.
        var instance: SkyrimAppetite? = null
            private set
    }
}
