package me.whipmegrandma.customenchants.enchant;

import lombok.Getter;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.mineacademy.fo.model.SimpleEnchantment;

public final class AntiHungerEnchant extends SimpleEnchantment {

	@Getter
	private final static AntiHungerEnchant instance = new AntiHungerEnchant();

	private AntiHungerEnchant() {
		super("Anti-Hunger", 1);

		EnchantsManager.register(this);
	}
}
