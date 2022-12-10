package me.whipmegrandma.customenchants.manager;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.model.SimpleEnchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantsManager {

	private static Map<String, SimpleEnchantment> enchants = new HashMap<>();

	public static SimpleEnchantment findEnchantment(String name) {
		return enchants.get(name.toLowerCase());
	}

	public static List<SimpleEnchantment> findEnchantments() {
		List<SimpleEnchantment> list = new ArrayList<>();

		for (Map.Entry<String, SimpleEnchantment> map : enchants.entrySet()) {
			list.add(map.getValue());
		}

		return list;
	}

	public static List<String> findEnchantmentNames() {
		List<String> list = new ArrayList<>();

		for (Map.Entry<String, SimpleEnchantment> map : enchants.entrySet()) {
			list.add(map.getValue().getName());
		}

		return list;
	}

	public static boolean has(String enchantment) {
		return enchants.containsKey(enchantment.toLowerCase());
	}

	public static void register(SimpleEnchantment enchantment) {
		enchants.put(enchantment.getName().toLowerCase(), enchantment);
	}

	public static boolean isLevelValid(SimpleEnchantment enchantment, Integer level) {

		for (Map.Entry<String, SimpleEnchantment> map : enchants.entrySet()) {
			if (map.getValue().equals(enchantment))
				return level > 0 && level <= enchantment.getMaxLevel();
		}
		return false;
	}

	public static List<String> findEnchantmentLevels(String name) {
		List<String> list = new ArrayList<>();

		SimpleEnchantment enchantment = findEnchantment(name);

		for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
			list.add(String.valueOf(i));
		}

		return list;
	}

	public static boolean itemContains(ItemStack item, Enchantment enchantment) {

		for (Enchantment enchant : item.getEnchantments().keySet())
			return enchant.equals(enchantment);

		return false;
	}

	public static Integer level(ItemStack item, Enchantment enchantment) {

		for (Map.Entry<Enchantment, Integer> enchant : item.getEnchantments().entrySet()) {
			Enchantment name = enchant.getKey();
			Integer level = enchant.getValue();

			return name.equals(enchantment) ? level : null;
		}

		return null;
	}

}
