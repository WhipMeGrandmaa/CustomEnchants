package me.whipmegrandma.customenchants.listener;

import me.whipmegrandma.customenchants.manager.PhysicalShopManager;
import me.whipmegrandma.customenchants.menu.EnchantShopMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMaterial;
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

		if (type == null || block == null)
			return;

		if (type.equals("Creator")) {
			Location location = block.getLocation().add(0.5, 1, 0.5);
			Float yaw = this.getYaw(player);

			location.setYaw(yaw);
			location.setPitch(0);

			Villager villager = (Villager) player.getWorld().spawnEntity(location, EntityType.VILLAGER);
			villager.setProfession(Villager.Profession.TOOLSMITH);
			villager.setSilent(true);
			villager.setAI(false);

			CompMetadata.setMetadata(villager, "CustomEnchants:Plugin");

			PhysicalShopManager.getInstance().add(location);

			event.setCancelled(true);

			Common.tell(player, "Successfully created a custom enchant shop.");
		}
	}

	@EventHandler
	public void onRemoveOrOpenShop(EntityDamageByEntityEvent event) {

		Entity damager = event.getDamager();
		Entity entity = event.getEntity();

		if (!PhysicalShopManager.isEntityValid(entity) || !(damager instanceof Player))
			return;

		event.setCancelled(true);

		Player player = (Player) damager;

		ItemStack item = player.getInventory().getItemInMainHand();
		String metaData = !CompMaterial.isAir(item) ? CompMetadata.getMetadata(item, "CustomEnchants:Plugin") : null;

		if ("Remover".equals(metaData)) {

			entity.remove();
			PhysicalShopManager.getInstance().remove(entity.getLocation());

			Common.tell(player, "Successfully removed a custom enchant shop.");
			return;
		}

		EnchantShopMenu.getInstance().displayTo(player);

	}

	@EventHandler
	public void onOpenShop(PlayerInteractAtEntityEvent event) {

		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();

		if (!PhysicalShopManager.isEntityValid(entity))
			return;

		event.setCancelled(true);

		ItemStack item = player.getInventory().getItemInMainHand();
		String metaData = !CompMaterial.isAir(item) ? CompMetadata.getMetadata(item, "CustomEnchants:Plugin") : null;

		EnchantShopMenu.getInstance().displayTo(player);

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void cancelDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		Location location = entity.getLocation();

		if (entity instanceof Villager && CompMetadata.hasMetadata(entity, "CustomEnchants:Plugin"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onTradingOpenCancel(InventoryOpenEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		InventoryType type = event.getInventory().getType();

		if (type == InventoryType.MERCHANT && holder instanceof Villager && PhysicalShopManager.isEntityValid((Villager) holder))
			event.setCancelled(true);
	}

	private Float getYaw(Player player) {
		BlockFace direction = player.getFacing();

		switch (direction) {
			case SOUTH:
				return -180F;
			case EAST:
				return 90F;
			case NORTH:
				return -0F;
			case WEST:
				return -90F;
		}

		return null;
	}
}
