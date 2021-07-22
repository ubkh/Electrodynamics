package electrodynamics.common.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;

public enum ElectricItemTier implements IItemTier {
    DRILL(2, 0, ItemTier.IRON.getEfficiency() * 1.1f, ItemTier.IRON.getAttackDamage() * 1.6f, 5);
    private final int harvestLevel;
    private final float efficency;
    private final float attackDammage;
    private final int enchantability;

    ElectricItemTier(int harvestLevel, int maxUses, float efficency, float attackDammage, int enchantability) {
	this.harvestLevel = harvestLevel;
	this.efficency = efficency;
	this.attackDammage = attackDammage;
	this.enchantability = enchantability;
    }

    @Override
    public int getMaxUses() {
	return 0;
    }

    @Override
    public float getEfficiency() {
	return efficency;
    }

    @Override
    public float getAttackDamage() {
	return attackDammage;
    }

    @Override
    public int getHarvestLevel() {
	return harvestLevel;
    }

    @Override
    public int getEnchantability() {
	return enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
	return Ingredient.EMPTY;
    }

    public String tag() {
	return name().toLowerCase();
    }

}
