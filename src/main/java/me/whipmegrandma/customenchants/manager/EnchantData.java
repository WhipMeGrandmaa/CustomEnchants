package me.whipmegrandma.customenchants.manager;

import lombok.Data;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.SimpleEnchantment;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.Arrays;
import java.util.List;

@Data
public class EnchantData extends YamlConfig {

	private static ConfigItems<EnchantData> data = ConfigItems.fromFolder("enchants", EnchantData.class);

	private String title;
	private List<String> lore;
	private CompMaterial material;
	private Boolean glow;

	private SimpleEnchantment enchant;

	private EnchantData(String name) {
		this.title = name;
		this.enchant = this.setEnchantment(name);

		this.loadConfiguration(NO_DEFAULT, "enchants/" + name + ".yml");
	}

	@Override
	protected void onLoad() {
		this.title = Common.getOrDefault(this.getString("Title"), "&e" + this.title + " {level}");
		this.lore = this.getStringList("Lore").isEmpty() ? Arrays.asList("&7Combine with desired item!") : this.getStringList("Lore");
		this.material = Common.getOrDefault(this.getMaterial("Material"), CompMaterial.ENCHANTED_BOOK);
		this.glow = Common.getOrDefault(this.getBoolean("Glow"), false);

		this.save();
	}

	@Override
	protected void onSave() {
		this.set("Title", this.title);
		this.set("Lore", this.lore);
		this.set("Material", this.material);
		this.set("Glow", this.glow);
	}

	private SimpleEnchantment setEnchantment(String name) {
		return EnchantsManager.findEnchantment(name.toLowerCase());
	}

	public static EnchantData getData(SimpleEnchantment enchantment) {
		String name = enchantment.getName().toLowerCase();

		return data.findItem(name);
	}

	private static void loadOrCreateEnchants() {

		for (String enchant : EnchantsManager.findEnchantmentNames()) {
			loadOrCreateEnchant(enchant);
		}
	}

	private static EnchantData loadOrCreateEnchant(String enchantment) {
		return data.loadOrCreateItem(enchantment);
	}

	private static void clearItems() {
		for (EnchantData item : data.getItems())
			data.removeItem(item);
	}

	public static void loadEnchants() {
		clearItems();
		loadOrCreateEnchants();
	}

}
