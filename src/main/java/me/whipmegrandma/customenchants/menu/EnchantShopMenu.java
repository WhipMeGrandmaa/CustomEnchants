package me.whipmegrandma.customenchants.menu;

import lombok.Data;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.customenchants.manager.EnchantData;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.*;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantShopMenu extends YamlConfig {

	@Getter
	private static EnchantShopMenu instance;

	private String title;
	private Integer size;
	private List<ButtonData> buttons;

	private EnchantShopMenu() {

		instance = this;

		this.loadConfiguration("menu/enchantshopmenu.yml");
	}

	@Override
	protected void onLoad() {
		this.title = this.getString("Title");
		this.size = (int) MathUtil.calculate(this.getString("Size"));
		this.buttons = this.loadButtons();
	}

	private List<ButtonData> loadButtons() {
		List<ButtonData> buttons = new ArrayList<>();

		for (Map.Entry<String, Object> map : this.getMap("Buttons").entrySet()) {
			String name = map.getKey();
			SerializedMap buttonData = SerializedMap.of(map.getValue());

			buttons.add(ButtonData.deserialize(buttonData));
		}

		return buttons;
	}

	public void displayTo(Player player) {
		this.toMenu(player).displayTo(player);
	}

	private Menu toMenu(Player player) {
		Map<Integer, Button> buttons = this.getButtons(player);

		return new Menu() {

			{
				this.setTitle(title);
				this.setSize(size);
				setViewer(player);
				setSound(new SimpleSound(Sound.ENTITY_ENDER_EYE_DEATH, 1));
			}

			@Override
			protected List<Button> getButtonsToAutoRegister() {
				return new ArrayList<>(buttons.values());
			}

			@Override
			public ItemStack getItemAt(int slot) {

				if (buttons.containsKey(slot))
					return buttons.get(slot).getItem();

				return NO_ITEM;
			}
		};
	}

	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (ButtonData data : this.buttons) {

			buttons.put(data.getSlot(), new Button() {

				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {

					if (data.getEnchantment() != null) {

						EnchantData enchantData = EnchantData.getData(data.getEnchantment());
						String title = Replacer.replaceArray(enchantData.getTitle(), "level", MathUtil.toRoman(data.getLevel()));
						List<String> lore = Replacer.replaceArray(enchantData.getLore(), "level", MathUtil.toRoman(data.getLevel()));

						if (Common.doesPluginExist("PowerCurrency") && data.getPrice() != null) {

							if (PowerManager.buy(player, data.getPrice())) {

								menu.restartMenu(data.getReceivedMessage());

								ItemCreator.of(enchantData.getMaterial()).name(title)
										.lore(lore)
										.tag("CustomEnchants:Plugin", data.getEnchantment().getName().toLowerCase() + " " + data.getLevel())
										.give(player);

								CompSound.ENTITY_VILLAGER_NO.play(player);
							} else {

								menu.restartMenu(data.getInsufficientMessage());
								CompSound.ENTITY_VILLAGER_NO.play(player);

							}
						} else {
							ItemCreator.of(enchantData.getMaterial()).name(title)
									.lore(lore)
									.tag("CustomEnchants:Plugin", data.getEnchantment().getName().toLowerCase() + " " + data.getLevel())
									.give(player);

							CompSound.ENTITY_VILLAGER_NO.play(player);

							Bukkit.getLogger().severe("PowerCurrency isn't loaded. All enchants are free.");
						}
					}
				}

				@Override
				public ItemStack getItem() {
					String title = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getTitle()) : data.getTitle();
					List<String> lore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getLore()) : data.getLore();

					return ItemCreator
							.of(data.getMaterial())
							.name(title)
							.lore(lore)
							.glow(data.getGlow())
							.make();
				}

				private String replace(String message) {
					return Replacer.replaceArray(message, "%level%", MathUtil.toRoman(data.getLevel()));
				}

				private List<String> replace(List<String> message) {
					return Replacer.replaceArray(message, "%level%", MathUtil.toRoman(data.getLevel()));
				}
			});
		}

		return buttons;
	}

	@Data

	public static class ButtonData implements ConfigSerializable {

		private SimpleEnchantment enchantment;
		private Integer level;
		private Integer slot;
		private CompMaterial material;
		private Boolean glow;
		private String title;
		private List<String> lore;

		private String receivedMessage;
		private String insufficientMessage;
		private Integer price;

		@Override
		public SerializedMap serialize() {
			return null;
		}

		public static ButtonData deserialize(SerializedMap map) {
			ButtonData button = new ButtonData();

			button.enchantment = map.containsKey("Enchantment") ? EnchantsManager.findEnchantment(map.getString("Enchantment")) : null;

			button.level = map.containsKey("Level") && EnchantsManager.isLevelValid(button.enchantment, map.getInteger("Level")) && button.enchantment != null ? map.getInteger("Level") : 1;

			button.slot = map.containsKey("Slot") ? (int) MathUtil.calculate(map.getString("Slot")) : -1;
			Valid.checkBoolean(button.slot != -1, "Missing slot key from button: " + map);

			button.material = map.containsKey("Material") ? map.getMaterial("Material") : null;
			Valid.checkBoolean(button.material != null, "Missing material key from button: " + map);

			button.glow = map.containsKey("Glow") ? map.getBoolean("Glow") : false;

			button.title = map.containsKey("Title") ? map.getString("Title") :
					button.enchantment != null && button.level != null ? "&b&l" + ItemUtil.bountifyCapitalized(button.enchantment.getName()) + " " + MathUtil.toRoman(button.level) :
							"&b&l" + ItemUtil.bountifyCapitalized(button.material);

			button.lore = map.getStringList("Lore");

			SerializedMap clickedMap = map.getMap("Clicked");

			button.receivedMessage = clickedMap.containsKey("Received_Message") || button.enchantment == null ? clickedMap.getString("Received_Message") : "&aReceived " + button.enchantment.getName() + " " + MathUtil.toRoman(button.level);

			button.insufficientMessage = clickedMap.containsKey("Insufficient_Message") || button.enchantment == null ? clickedMap.getString("Insufficient_Message") : "&cInsufficient power!";

			button.price = clickedMap.containsKey("Price") ? clickedMap.getInteger("Price") : null;

			return button;
		}
	}

	public static EnchantShopMenu loadMenu() {
		return new EnchantShopMenu();
	}

}
