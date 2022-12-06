package me.whipmegrandma.customenchants.listener;

import me.whipmegrandma.customenchants.enchant.AntiHungerEnchant;
import me.whipmegrandma.customenchants.enchant.DrillEnchant;
import me.whipmegrandma.customenchants.enchant.HasteEnchant;
import me.whipmegrandma.customenchants.enchant.SpeedEnchant;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleEnchantment;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class EnchantCombineListener implements Listener {

	private final List<String> drillTools = Arrays.asList(
			"_pickaxe",
			"_axe",
			"_shovel",
			"_hoe",
			"_sword"
	);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClickEnchant(InventoryClickEvent event) {

		this.combine(true, event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void applyEnchant(InventoryClickEvent event) {

		this.combine(false, event);
	}

	private void combine(boolean clickedOrCursor, InventoryClickEvent event) {

		if (event.getCurrentItem() == null)
			return;

		ItemStack itemOne = event.getCursor();
		ItemStack itemTwo = event.getCurrentItem();
		String metaData = CompMetadata.hasMetadata(clickedOrCursor ? itemOne : itemTwo, "CustomEnchants:Plugin") ? CompMetadata.getMetadata(clickedOrCursor ? itemOne : itemTwo, "CustomEnchants:Plugin") : null;

		if (metaData == null || CompMaterial.isAir(clickedOrCursor ? itemTwo : itemOne) || event.getClickedInventory() == null || event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
			return;

		String[] data = metaData.split(" ");

		SimpleEnchantment enchant = EnchantsManager.findEnchantment(data[0]);
		Integer level = Integer.parseInt(data[1]);

		String material = clickedOrCursor ? itemTwo.getType().toString().toLowerCase() : itemOne.getType().toString().toLowerCase();

		if (clickedOrCursor ? itemTwo.containsEnchantment(enchant) : itemOne.containsEnchantment(enchant))
			return;

		if (enchant.equals(AntiHungerEnchant.getInstance()) && material.contains("_helmet"))
			this.applyEnchant(enchant, level, event, clickedOrCursor);

		if (enchant.equals(SpeedEnchant.getInstance()) && material.contains("_boots"))
			this.applyEnchant(enchant, level, event, clickedOrCursor);

		if (enchant.equals(HasteEnchant.getInstance()))
			for (String tool : this.drillTools)
				if (material.contains(tool))
					this.applyEnchant(enchant, level, event, clickedOrCursor);

		if (enchant.equals(DrillEnchant.getInstance()))
			for (String tool : this.drillTools)
				if (material.contains(tool) && !tool.equals("_sword") && !tool.equals("_hoe"))
					this.applyEnchant(enchant, level, event, clickedOrCursor);

	}

	private void applyEnchant(SimpleEnchantment enchant, Integer level, InventoryClickEvent event, boolean clickedOrCursor) {

		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = clickedOrCursor ? event.getCurrentItem() : event.getCursor();

		Integer slot = event.getSlot();
		Inventory inventory = event.getClickedInventory();

		ItemStack updatedItem = ItemCreator.of(clicked).enchant(enchant, level).make();

		inventory.setItem(slot, clickedOrCursor ? updatedItem : null);
		event.setCancelled(true);
		player.setItemOnCursor(clickedOrCursor ? null : updatedItem);

	}
}
