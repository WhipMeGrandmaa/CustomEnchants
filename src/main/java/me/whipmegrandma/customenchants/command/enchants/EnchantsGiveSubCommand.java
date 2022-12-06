package me.whipmegrandma.customenchants.command.enchants;

import me.whipmegrandma.customenchants.manager.EnchantData;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleEnchantment;

import java.util.List;

public final class EnchantsGiveSubCommand extends SimpleSubCommand {

	public EnchantsGiveSubCommand(SimpleCommandGroup parent) {
		super(parent, "give");

		this.setUsage("<username> <enchant> <level>");
		this.setPermission("customenchants.command.give");
		this.setMinArguments(3);
	}

	@Override
	protected void onCommand() {

		Player player = findPlayer(args[0]);
		SimpleEnchantment enchant = this.findEnchant(args[1]);
		Integer level = findLevel(2, enchant);

		EnchantData enchantData = EnchantData.getData(enchant);
		String title = Replacer.replaceArray(enchantData.getTitle(), "level", MathUtil.toRoman(level));
		List<String> lore = Replacer.replaceArray(enchantData.getLore(), "level", MathUtil.toRoman(level));

		ItemCreator.of(enchantData.getMaterial())
				.name(title)
				.lore(lore)
				.tag("CustomEnchants:Plugin", enchant.getName().toLowerCase() + " " + level)
				.give(player);

		Common.tell(player, "You have been given " + enchant.getName() + " " + String.valueOf(MathUtil.toRoman(level)) + "!");

		if (!player.equals(getPlayer()))
			tell("You gave " + player.getName() + " " + enchant.getName() + " " + String.valueOf(MathUtil.toRoman(level)) + "!");
	}

	private Integer findLevel(Integer index, SimpleEnchantment enchantment) {
		Integer level = findNumber(index, "The level must be a positive whole number!");

		checkBoolean(enchantment.getMaxLevel() >= level, "The max level for " + enchantment.getName() + " is " + enchantment.getMaxLevel() + "!");
		checkBoolean(0 <= level, "The level must be at least 1!");

		return level;
	}

	private SimpleEnchantment findEnchant(String enchant) {
		checkBoolean(EnchantsManager.has(enchant), "This enchantment does not exist. Use '/enchants list'  to see all available enchants.");

		return EnchantsManager.findEnchantment(enchant);
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		if (args.length == 2)
			return completeLastWord(EnchantsManager.findEnchantmentNames());

		if (args.length == 3 && EnchantsManager.has(args[1]))
			return completeLastWord(EnchantsManager.findEnchantmentLevels(args[1]));

		return NO_COMPLETE;
	}
}
