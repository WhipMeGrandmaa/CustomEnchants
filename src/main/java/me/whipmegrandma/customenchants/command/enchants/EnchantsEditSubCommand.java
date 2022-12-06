package me.whipmegrandma.customenchants.command.enchants;

import org.bukkit.entity.EntityType;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public final class EnchantsEditSubCommand extends SimpleSubCommand {

	public EnchantsEditSubCommand(SimpleCommandGroup parent) {
		super(parent, "edit");

		this.setPermission("customenchants.command.edit");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ItemCreator.ofEgg(EntityType.VILLAGER)
				.name("&e&lEnchant Shop Creator")
				.lore("", "&7Place to create shop.")
				.glow(true)
				.tag("CustomEnchants:Plugin", "Creator")
				.give(getPlayer());

		ItemCreator.of(CompMaterial.DIAMOND_SWORD)
				.name("&e&lEnchant Shop Remover")
				.lore("", "&7Hit shops to remove.")
				.glow(true)
				.tag("CustomEnchants:Plugin", "Remover")
				.give(getPlayer());

		tell("Given editing items.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
