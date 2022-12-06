package me.whipmegrandma.customenchants.enchant;

import lombok.Getter;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.mineacademy.fo.model.SimpleEnchantment;

public final class HasteEnchant extends SimpleEnchantment {

	@Getter
	private final static HasteEnchant instance = new HasteEnchant();

	private HasteEnchant() {
		super("Haste", 1);

		EnchantsManager.register(this);
	}
}
