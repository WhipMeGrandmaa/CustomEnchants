package me.whipmegrandma.customenchants.menu;

import lombok.Data;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantPreviewMenu extends YamlConfig {

	@Getter
	private static EnchantPreviewMenu instance;

	private String title;
	private Integer size;
	private List<ButtonData> buttons;

	private EnchantPreviewMenu() {

		instance = this;

		this.loadConfiguration("menu/enchantpreviewmenu.yml");
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
				}

				@Override
				public ItemStack getItem() {
					String title = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getTitle()) : data.getTitle();
					List<String> lore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getLore()) : data.getLore();

					ItemCreator item = ItemCreator
							.of(data.getMaterial())
							.name(title)
							.lore(lore)
							.glow(data.getGlow());

					if (data.skullName != null) {
						String skullName = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getSkullName()) : data.getSkullName();
						item.skullOwner(skullName);
					}

					return item.make();
				}
			});
		}

		return buttons;
	}

	@Data

	private static class ButtonData implements ConfigSerializable {

		private Integer slot;
		private CompMaterial material;
		private Boolean glow;
		private String title;
		private List<String> lore;
		private String skullName;

		@Override
		public SerializedMap serialize() {
			return null;
		}

		public static ButtonData deserialize(SerializedMap map) {
			ButtonData button = new ButtonData();

			button.slot = map.containsKey("Slot") ? (int) MathUtil.calculate(map.getString("Slot")) : -1;
			Valid.checkBoolean(button.slot != -1, "Missing slot key from button: " + map);

			button.material = map.containsKey("Material") ? map.getMaterial("Material") : null;
			Valid.checkBoolean(button.material != null, "Missing material key from button: " + map);

			button.glow = map.containsKey("Glow") ? map.getBoolean("Glow") : false;

			button.title = map.containsKey("Title") ? map.getString("Title") : "&8Menu";

			button.lore = map.getStringList("Lore");

			button.skullName = map.getString("Player_Skull_Name");

			return button;
		}
	}

	public static EnchantPreviewMenu loadMenu() {
		return new EnchantPreviewMenu();
	}

}
