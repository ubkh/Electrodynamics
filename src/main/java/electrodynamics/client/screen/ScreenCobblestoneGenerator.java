package electrodynamics.client.screen;

import java.util.ArrayList;
import java.util.List;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.ElectricUnit;
import electrodynamics.common.inventory.container.ContainerCobblestoneGenerator;
import electrodynamics.common.settings.Constants;
import electrodynamics.common.tile.TileCobblestoneGenerator;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import electrodynamics.prefab.screen.component.ScreenComponentInfo;
import electrodynamics.prefab.screen.component.ScreenComponentProgress;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ScreenCobblestoneGenerator extends GenericScreen<ContainerCobblestoneGenerator> {

	public ScreenCobblestoneGenerator(ContainerCobblestoneGenerator container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		components.add(new ScreenComponentFluid(() -> {
			TileCobblestoneGenerator cobble = container.getHostFromIntArray();
			if (cobble != null && cobble.isPoweredClient) {
				FluidTank tank = new FluidTank(1000);
				tank.fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
				return tank;
			}
			return null;
		}, this, 21, 18));
		components.add(new ScreenComponentFluid(() -> {
			TileCobblestoneGenerator cobble = container.getHostFromIntArray();
			if (cobble != null && cobble.isPoweredClient) {
				FluidTank tank = new FluidTank(1000);
				tank.fill(new FluidStack(Fluids.LAVA, 1000), FluidAction.EXECUTE);
				return tank;
			}
			return null;
		}, this, 117, 18));
		components.add(new ScreenComponentProgress(() -> {
			TileCobblestoneGenerator cobble = container.getHostFromIntArray();
			if (cobble != null) {
				return cobble.progressClient;
			}
			return 0;
		}, this, 40, 34));
		components.add(new ScreenComponentProgress(() -> {
			TileCobblestoneGenerator cobble = container.getHostFromIntArray();
			if (cobble != null) {
				return cobble.progressClient;
			}
			return 0;
		}, this, 90, 34).left());
		components.add(new ScreenComponentElectricInfo(this::getEnergyInformation, this, -ScreenComponentInfo.SIZE + 1, 2));
	}

	private List<? extends FormattedCharSequence> getEnergyInformation() {
		ArrayList<FormattedCharSequence> list = new ArrayList<>();
		GenericTile box = menu.getHostFromIntArray();
		if (box != null) {
			ComponentElectrodynamic electro = box.getComponent(ComponentType.Electrodynamic);

			list.add(new TranslatableComponent("gui.do2oprocessor.usage",
					new TextComponent(ChatFormatter.getElectricDisplayShort(Constants.COBBLE_GEN_USAGE_PER_TICK * 20, ElectricUnit.WATT))
							.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
			list.add(new TranslatableComponent("gui.do2oprocessor.voltage",
					new TextComponent(ChatFormatter.getElectricDisplayShort(electro.getVoltage(), ElectricUnit.VOLTAGE))
							.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		}
		return list;
	}

}