package me.whipmegrandma.customenchants.enchant;

import lombok.Getter;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.mineacademy.fo.model.SimpleEnchantment;

public final class SpeedEnchant extends SimpleEnchantment {

	@Getter
	private final static SpeedEnchant instance = new SpeedEnchant();

	private SpeedEnchant() {
		super("Speed", 1);

		EnchantsManager.register(this);
	}
}
