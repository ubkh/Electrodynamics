package electrodynamics.common.item.gear.tools;

import java.util.ArrayList;
import java.util.List;

import electrodynamics.DeferredRegisters;
import electrodynamics.api.fluid.RestrictedFluidHandlerItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Acts as a universal canister container. Can hold a maximum of 2000 mB of fluid. 
 * @author skip999
 *
 */
public class ItemCanister extends Item {

	public static final int MAX_FLUID_CAPACITY = 5000;
	public static final Fluid EMPTY_FLUID = Fluids.EMPTY;
	
    public ItemCanister(Item.Properties itemProperty) {
    	super(itemProperty);
    }
    
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    	if(isInGroup(group)) {
    		items.add(new ItemStack(this));
    		ArrayList<Fluid> FLUIDS = new ArrayList<>(ForgeRegistries.FLUIDS.getValues());
    		if(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null) {
    			for(Fluid fluid : FLUIDS) {
    				ItemStack temp = new ItemStack(this);
        			boolean canFill = temp.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(m ->{
        				return ((RestrictedFluidHandlerItemStack)m).canFillFluidType(new FluidStack(fluid,1));
        			}).orElse(false);
        			if(canFill) {
        				temp.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(h -> {
    	    				h.fill(new FluidStack(fluid, MAX_FLUID_CAPACITY), FluidAction.EXECUTE);
    	    			});
    	    			items.add(temp);
        			}
    			}
    			
    		}
    	}
    }
    
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
    	return new RestrictedFluidHandlerItemStack(stack, stack, MAX_FLUID_CAPACITY, getWhitelistedFluids());
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {;
    	if(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null) {
    		stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(h -> {
    			if(!(((FluidHandlerItemStack.SwapEmpty)h).getFluid().getFluid().isEquivalentTo(EMPTY_FLUID))) {
    				FluidHandlerItemStack.SwapEmpty cap = (FluidHandlerItemStack.SwapEmpty)h;
    				tooltip.add(new StringTextComponent(cap.getFluidInTank(0).getAmount() + "/" + MAX_FLUID_CAPACITY + " mB"));
    				tooltip.add(new StringTextComponent(cap.getFluid().getDisplayName().getString()));
    			}
    		});
    	}
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    	return ActionResult.resultFail(playerIn.getHeldItem(handIn));
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
    	boolean show = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(h -> {
    		RestrictedFluidHandlerItemStack cap = (RestrictedFluidHandlerItemStack)h;
    		return !cap.getFluid().getFluid().isEquivalentTo(EMPTY_FLUID);
    	}).orElse(false);
    	return show;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
    	return 1.0 - stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(h -> {
    		RestrictedFluidHandlerItemStack cap = (RestrictedFluidHandlerItemStack)h;
    		return (double)cap.getFluidInTank(0).getAmount() / (double)cap.getTankCapacity(0);
    	}).orElse(1.0);
    }
    
    @Override
    public boolean hasContainerItem() {
    	return true;
    }
    
    @Override
    //TODO handle NBT canister crafting
    public ItemStack getContainerItem(ItemStack itemStack) {
    	boolean isEmpty = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(m ->{
    		return m.getFluidInTank(0).getFluid().isEquivalentTo(Fluids.EMPTY);
    	}).orElse(true);
    	if(isEmpty) {
    		return new ItemStack(Items.AIR);
    	}
		return new ItemStack(DeferredRegisters.ITEM_CANISTERREINFORCED.get());
    }
    
    public ArrayList<Fluid> getWhitelistedFluids(){
    	ArrayList<Fluid> whitelisted = new ArrayList<>();
    	/* Current Whitelist:
    	 * > Electrodynamics Fluids 
    	 * > Water
    	 */
    	for(RegistryObject<Fluid> fluid : DeferredRegisters.FLUIDS.getEntries()) {
    		whitelisted.add(fluid.get());
    	}
    	whitelisted.add(Fluids.WATER);
    	return whitelisted;
    }
    
}
