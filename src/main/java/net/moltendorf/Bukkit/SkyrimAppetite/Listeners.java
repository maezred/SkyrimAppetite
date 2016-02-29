package net.moltendorf.Bukkit.SkyrimAppetite;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by moltendorf on 2016-02-28.
 */
public class Listeners implements Listener {
	final protected Map<UUID, Integer> foodLevels = new HashMap<>();

	private void playerConsumedFood(Player player, Material material, MaterialData data) {

		UUID playerId = player.getUniqueId();

		Integer foodLevel = foodLevels.get(playerId);

		if (foodLevel == null) {
			foodLevel = 0;

			player.sendMessage("Since this is your first time eating food with SkyrimAppetite installed, here's how hunger works: we count how much food you've eaten; if you've eaten enough, you gain a point of hunger.");
			player.sendMessage("In short, you must eat a ton of food now.");
		}

		Settings settings = Settings.getInstance();

		Integer foodValue = settings.getFoodValue(material, data);
		foodLevel += foodValue;

		int maxFoodLevel = settings.getMaxFoodLevel();

		if (foodLevel >= maxFoodLevel) {
			player.setFoodLevel(player.getFoodLevel() - foodValue + 1);

			foodLevel -= maxFoodLevel;
		} else {
			player.setFoodLevel(player.getFoodLevel() - foodValue);
		}

		foodLevels.put(playerId, foodLevel);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerItemConsumeEventMonitor(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();

		playerConsumedFood(event.getPlayer(), item.getType(), item.getData());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerInteractEventMonitor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if (block != null) {
			Material material = block.getType();
			Player player = event.getPlayer();

			if (material == Material.CAKE_BLOCK && player.getFoodLevel() < 20) {
				playerConsumedFood(player, material, material.getNewData((byte) 0));
			}
		}
	}
}
