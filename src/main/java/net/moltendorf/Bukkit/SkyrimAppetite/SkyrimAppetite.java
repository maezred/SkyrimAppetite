package net.moltendorf.Bukkit.SkyrimAppetite;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by moltendorf on 2016-02-28.
 */
public class SkyrimAppetite extends JavaPlugin {

	// Main instance.
	private static SkyrimAppetite instance = null;

	protected static SkyrimAppetite getInstance() {
		return instance;
	}

	// Variable data.
	protected Listeners listeners = null;
	protected Settings settings = null;

	@Override
	public void onEnable() {
		instance = this;

		// Construct new settings.
		listeners = new Listeners();
		settings = new Settings();

		getServer().getPluginManager().registerEvents(listeners, this);
	}

	@Override
	public void onDisable() {
		settings.save();

		instance = null;
	}
}
