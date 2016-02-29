package net.moltendorf.Bukkit.SkyrimAppetite;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by moltendorf on 15/05/23.
 *
 * @author moltendorf
 */
public class Settings {
	protected static Settings getInstance() {
		return SkyrimAppetite.getInstance().settings;
	}

	private boolean enabled = true; // Whether or not the plugin is enabled at all; interface mode.

	private Map<Material, Value> foodValues = new HashMap<Material, Value>() {{
		put(Material.APPLE, new SimpleValue(4));
		put(Material.BAKED_POTATO, new SimpleValue(5));
		//put(Material.BEETROOT, new SimpleValue(1)); // 1.9
		//put(Material.BEETROOT_SOUP, new SimpleValue(6)); // 1.9
		put(Material.BREAD, new SimpleValue(5));
		put(Material.CAKE_BLOCK, new SimpleValue(2));
		put(Material.CARROT_ITEM, new SimpleValue(3));
		//put(Material.CHORUS_FRUIT, new SimpleValue(4)); // 1.9
		put(Material.COOKED_CHICKEN, new SimpleValue(6));
		put(Material.COOKED_FISH, new ComplexValue(new HashMap<Byte, Integer>() {{
			put((byte) 0, 5); // Cooked Fish
			put((byte) 1, 6); // Cooked Salmon
		}}));
		put(Material.COOKED_MUTTON, new SimpleValue(6));
		put(Material.GRILLED_PORK, new SimpleValue(8));
		put(Material.COOKED_RABBIT, new SimpleValue(5));
		put(Material.COOKIE, new SimpleValue(2));
		put(Material.GOLDEN_APPLE, new SimpleValue(4));
		put(Material.GOLDEN_CARROT, new SimpleValue(6));
		put(Material.MELON, new SimpleValue(2));
		put(Material.MUSHROOM_SOUP, new SimpleValue(6));
		put(Material.POISONOUS_POTATO, new SimpleValue(2));
		put(Material.POTATO_ITEM, new SimpleValue(1));
		put(Material.PUMPKIN_PIE, new SimpleValue(8));
		put(Material.RABBIT_STEW, new SimpleValue(10));
		put(Material.RAW_BEEF, new SimpleValue(3));
		put(Material.RAW_CHICKEN, new SimpleValue(2));
		put(Material.RAW_FISH, new ComplexValue(new HashMap<Byte, Integer>() {{
			put((byte) 0, 2); // Raw Fish
			put((byte) 1, 2); // Raw Salmon
			put((byte) 2, 1); // Clownfish
			put((byte) 3, 1); // Pufferfish
		}}));
		put(Material.MUTTON, new SimpleValue(2));
		put(Material.PORK, new SimpleValue(3));
		put(Material.RABBIT, new SimpleValue(3));
		put(Material.ROTTEN_FLESH, new SimpleValue(4));
		put(Material.SPIDER_EYE, new SimpleValue(2));
		put(Material.COOKED_BEEF, new SimpleValue(8));
	}};

	public Settings() {
		final SkyrimAppetite instance = SkyrimAppetite.getInstance();
		final Logger log = instance.getLogger();

		// Make sure the default configuration is saved.
		instance.saveDefaultConfig();

		final FileConfiguration config = instance.getConfig();

		enabled = config.getBoolean("enabled", enabled);

		final ConfigurationSection foodsSection = config.getConfigurationSection("foods");

		if (foodsSection != null) {
			foodValues.clear();

			for (final String key : foodsSection.getKeys(false)) {
				try {
					int foodValue = foodsSection.getInt(key, -1);

					if (foodValue < 1) {
						ConfigurationSection foodSection = foodsSection.getConfigurationSection(key);

						if (foodSection == null) {
							log.warning("Config: Invalid value type for " + key + " specified.");
						} else {
							foodValues.put(Material.valueOf(key), new ComplexValue(foodSection));
						}
					} else {
						foodValues.put(Material.valueOf(key), new SimpleValue(foodValue));
					}
				} catch (IllegalArgumentException exception) {
					log.warning("Config: Invalid material " + key + " specified.");
				}
			}
		}

		final ConfigurationSection players = config.getConfigurationSection("players");

		if (players != null) {
			Map<UUID, Integer> foodLevels = instance.listeners.foodLevels;

			for (final String key : players.getKeys(false)) {
				try {
					UUID playerId = UUID.fromString(key);

					int foodLevel = players.getInt(key, -1);

					if (foodLevel >= 0) {
						foodLevels.put(playerId, foodLevel);
					} else {
						log.warning("Config: Invalid food level for " + key + " specified.");
					}
				} catch (IllegalArgumentException exception) {
					log.warning("Config: Invalid UUID " + key + ".");
				}
			}
		}
	}

	public boolean getEnabled() {
		return enabled;
	}

	public int getMaxFoodLevel() {
		return 10;
	}

	public Integer getFoodValue(Material material, MaterialData data) {
		final Value value = foodValues.get(material);

		if (value == null) {
			SkyrimAppetite.getInstance().getLogger().warning("Config: No value specified for " + material + ".");

			return 1;
		} else {
			final Integer integer = value.getValue(data);

			if (integer == null) {
				SkyrimAppetite.getInstance().getLogger().warning("Config: No data value specified for " + material + ".");

				return 1;
			} else {
				return integer;
			}
		}
	}

	protected void save() {
		final SkyrimAppetite instance = SkyrimAppetite.getInstance();

		final FileConfiguration config = instance.getConfig();
		final ConfigurationSection players = config.createSection("players");

		for (Map.Entry<UUID, Integer> entry : instance.listeners.foodLevels.entrySet()) {
			players.set(entry.getKey().toString(), entry.getValue());
		}

		instance.saveConfig();
	}
}

interface Value {
	Integer getValue(MaterialData data);
}

class SimpleValue implements Value {
	final private Integer value;

	SimpleValue(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue(MaterialData data) {
		return value;
	}
}

class ComplexValue implements Value {
	final private Map<Byte, Integer> value;

	ComplexValue(Map<Byte, Integer> value) {
		this.value = value;
	}

	ComplexValue(ConfigurationSection config) {
		value = new HashMap<>();

		for (final String key : config.getKeys(false)) {
			try {
				value.put(Byte.valueOf(key), config.getInt(key, 1));
			} catch (NumberFormatException exception) {
				SkyrimAppetite.getInstance().getLogger().warning("Config: Invalid data value specified: " + key + ".");
			}
		}
	}

	@Override
	public Integer getValue(MaterialData data) {
		return value.get(data.getData());
	}
}
