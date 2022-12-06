package me.whipmegrandma.customenchants.listener;

import me.whipmegrandma.customenchants.manager.PhysicalShopManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;

@AutoRegister
public final class EnchantShopListener implements Listener {

	@EventHandler
	public void onCreate(PlayerInteractEvent event) {
		if (!Remain.isInteractEventPrimaryHand(event))
			return;

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		ItemStack hand = event.getItem();

		String type = hand != null ? CompMetadata.getMetadata(hand, "CustomEnchants:Plugin") : null;

		if (type == null && block == null)
			return;

		if (type.equals("Creator")) {
			Location location = block.getLocation().add(0.5, 1, 0.5);
			Float pitch = (float) (90 * (Math.ceil(Math.abs(player.getLocation().getPitch() / 90))));

			location.setYaw(pitch);
			location.setPitch(0);

			Villager villager = (Villager) player.getWorld().spawnEntity(location, EntityType.VILLAGER);
			villager.setInvulnerable(true);
			villager.setProfession(Villager.Profession.TOOLSMITH);
			villager.setAI(false);

			PhysicalShopManager.getInstance().add(location);

			event.setCancelled(true);
		} else if (type.equals("Remover")) {

		}
	}
}
