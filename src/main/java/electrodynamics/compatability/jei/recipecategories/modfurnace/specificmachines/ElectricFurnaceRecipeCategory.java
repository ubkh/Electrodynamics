package electrodynamics.compatability.jei.recipecategories.modfurnace.specificmachines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import electrodynamics.DeferredRegisters;
import electrodynamics.api.References;
import electrodynamics.common.block.subtype.SubtypeMachine;
import electrodynamics.compatability.jei.recipecategories.modfurnace.ModFurnaceRecipeCategory;
import electrodynamics.compatability.jei.utils.gui.arrows.animated.ArrowRightAnimatedWrapper;
import electrodynamics.compatability.jei.utils.gui.arrows.stat.FlameStaticWrapper;
import electrodynamics.compatability.jei.utils.gui.backgroud.BackgroundWrapper;
import electrodynamics.compatability.jei.utils.gui.item.BigItemSlotWrapper;
import electrodynamics.compatability.jei.utils.gui.item.DefaultItemSlotWrapper;
import electrodynamics.compatability.jei.utils.label.PowerLabelWrapper;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class ElectricFurnaceRecipeCategory extends ModFurnaceRecipeCategory {

	// JEI Window Parameters
	private static BackgroundWrapper BACK_WRAP = new BackgroundWrapper(132,58);
	
	private static DefaultItemSlotWrapper INPUT_SLOT = new DefaultItemSlotWrapper(22, 20);
	private static BigItemSlotWrapper OUTPUT_SLOT = new BigItemSlotWrapper(83, 16);
	
	private static ArrowRightAnimatedWrapper ANIM_ARROW = new ArrowRightAnimatedWrapper(50, 23);
	private static FlameStaticWrapper FLAME = new FlameStaticWrapper(5, 23);
	
	private static PowerLabelWrapper POWER_LABEL = new PowerLabelWrapper(48, BACK_WRAP);

    private static int ANIM_TIME = 50;

    private static String MOD_ID = References.ID;
    private static String RECIPE_GROUP = "electric_furnace";

    public static ItemStack INPUT_MACHINE = new ItemStack(DeferredRegisters.SUBTYPEBLOCK_MAPPINGS.get(SubtypeMachine.electricfurnace));

    public static ResourceLocation UID = new ResourceLocation(MOD_ID, RECIPE_GROUP);
    
    public ElectricFurnaceRecipeCategory(IGuiHelper guiHelper) {
    	super(guiHelper, MOD_ID, RECIPE_GROUP, INPUT_MACHINE, BACK_WRAP, ANIM_TIME);
    	setInputSlots(guiHelper, INPUT_SLOT);
    	setOutputSlots(guiHelper, OUTPUT_SLOT);
    	setStaticArrows(guiHelper, FLAME);
    	setAnimatedArrows(guiHelper, ANIM_ARROW);
    	setLabels(POWER_LABEL);
    }

    @Override
    public ResourceLocation getUid() {
    	return UID;
    }

	@Override
	public List<List<ItemStack>> getItemInputs(SmeltingRecipe recipe) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for(Ingredient ing : recipe.getIngredients()) {
			inputs.add(Arrays.asList(ing.getItems()));
		}
		return inputs;
	}

	@Override
	public List<ItemStack> getItemOutputs(SmeltingRecipe recipe) {
		List<ItemStack> outputs = new ArrayList<>();
		outputs.add(recipe.getResultItem());
		return outputs;
	}

}
