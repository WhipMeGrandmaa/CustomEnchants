package me.whipmegrandma.customenchants.listener;

import me.whipmegrandma.customenchants.api.EnchantCombineEvent;
import me.whipmegrandma.customenchants.enchant.AntiHungerEnchant;
import me.whipmegrandma.customenchants.enchant.DrillEnchant;
import me.whipmegrandma.customenchants.enchant.HasteEnchant;
import me.whipmegrandma.customenchants.enchant.SpeedEnchant;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleEnchantment;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;

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

		if (CompMaterial.isAir(event.getCurrentItem()))
			return;

		ItemStack itemOne = event.getCursor();
		ItemStack itemTwo = event.getCurrentItem();
		String metaData = CompMetadata.getMetadata(clickedOrCursor ? itemOne : itemTwo, "CustomEnchants:Plugin");

		if (metaData == null || CompMaterial.isAir(clickedOrCursor ? itemTwo : itemOne) || event.getClickedInventory() == null || event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
			return;

		String[] data = metaData.split(" ");

		if (data.length != 2)
			return;

		SimpleEnchantment enchant = EnchantsManager.findEnchantment(data[0]);
		Integer level = Integer.parseInt(data[1]);

		String material = clickedOrCursor ? itemTwo.getType().toString().toLowerCase() : itemOne.getType().toString().toLowerCase();

		if (clickedOrCursor ? EnchantsManager.itemContains(itemTwo, enchant) : EnchantsManager.itemContains(itemOne, enchant))
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

		int slot = event.getSlot();
		Inventory inventory = event.getClickedInventory();

		ItemStack updatedItem = ItemCreator.of(clicked).enchant(enchant, level).make();

		if (!Common.callEvent(new EnchantCombineEvent(player, updatedItem, enchant, level)))
			return;

		inventory.setItem(slot, clickedOrCursor ? updatedItem : null);
		event.setCancelled(true);
		player.setItemOnCursor(clickedOrCursor ? null : updatedItem);

		PlayerInventory inventoryPlayer = player.getInventory();
		ItemStack itemHeld = inventoryPlayer.getItemInMainHand();

		ItemStack helmet = inventoryPlayer.getHelmet();
		ItemStack chestplate = inventoryPlayer.getChestplate();
		ItemStack leggings = inventoryPlayer.getLeggings();
		ItemStack boots = inventoryPlayer.getBoots();

		String name = updatedItem.getType().toString().toLowerCase();

		if (!updatedItem.equals(itemHeld) && !updatedItem.equals(helmet) && !updatedItem.equals(chestplate) && !updatedItem.equals(leggings) && !updatedItem.equals(boots))
			return;

		PotionEffect effect = null;

		if (EnchantsManager.itemContains(updatedItem, SpeedEnchant.getInstance()))
			effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);

		if (EnchantsManager.itemContains(updatedItem, AntiHungerEnchant.getInstance()))
			effect = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0);

		if (EnchantsManager.itemContains(updatedItem, HasteEnchant.getInstance()))
			effect = effect = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0);

		if (effect != null)
			player.addPotionEffect(effect);
	}

	@EventHandler
	public void onCombineEffect(EnchantCombineEvent event) {
		Player player = event.getPlayer();
		Location location = player.getLocation();

		CompParticle.FLASH.spawn(player, location);
		CompSound.SUCCESSFUL_HIT.play(player);
	}
}
