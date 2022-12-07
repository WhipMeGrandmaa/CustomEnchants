package me.whipmegrandma.customenchants.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.event.SimpleEvent;
import org.mineacademy.fo.model.SimpleEnchantment;

@Getter
public class EnchantCombineEvent extends SimpleEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private ItemStack item;
	private SimpleEnchantment enchantment;
	private Integer level;

	public EnchantCombineEvent(Player player, ItemStack item, SimpleEnchantment enchantment, Integer level) {
		this.player = player;
		this.item = item;
		this.enchantment = enchantment;
		this.level = level;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void setCancelled(boolean b) {

	}
}
