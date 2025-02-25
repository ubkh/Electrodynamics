package electrodynamics.common.tile;

import electrodynamics.DeferredRegisters;
import electrodynamics.SoundRegister;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.common.block.VoxelShapes;
import electrodynamics.common.block.subtype.SubtypeMachine;
import electrodynamics.common.inventory.container.tile.ContainerO2OProcessor;
import electrodynamics.common.inventory.container.tile.ContainerO2OProcessorDouble;
import electrodynamics.common.inventory.container.tile.ContainerO2OProcessorTriple;
import electrodynamics.common.recipe.ElectrodynamicsRecipeInit;
import electrodynamics.common.settings.Constants;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentProcessor;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TileWireMill extends GenericTile {
	public TileWireMill(BlockPos worldPosition, BlockState blockState) {
		this(0, worldPosition, blockState);
	}

	public TileWireMill(int extra, BlockPos worldPosition, BlockState blockState) {
		super(extra == 1 ? DeferredRegisters.TILE_WIREMILLDOUBLE.get() : extra == 2 ? DeferredRegisters.TILE_WIREMILLTRIPLE.get() : DeferredRegisters.TILE_WIREMILL.get(), worldPosition, blockState);
		int processorInputs = 1;
		int processorCount = extra + 1;
		int inputCount = processorInputs * (extra + 1);
		int outputCount = 1 * (extra + 1);
		int biproducts = 1 + extra;
		int invSize = 3 + inputCount + outputCount + biproducts;

		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickClient(this::tickClient));
		addComponent(new ComponentElectrodynamic(this).relativeInput(Direction.NORTH).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE * Math.pow(2, extra)).joules(Constants.WIREMILL_USAGE_PER_TICK * 20 * (extra + 1)));

		int[] ints = new int[extra + 1];
		for (int i = 0; i <= extra; i++) {
			ints[i] = i * 2;
		}

		addComponent(new ComponentInventory(this).size(invSize).inputs(inputCount).outputs(outputCount).upgrades(3).processors(processorCount).processorInputs(processorInputs).biproducts(biproducts).validUpgrades(ContainerO2OProcessor.VALID_UPGRADES).valid(machineValidator(ints)).setMachineSlots(extra).shouldSendInfo());
		addComponent(new ComponentContainerProvider("container.wiremill" + extra).createMenu((id, player) -> (extra == 0 ? new ContainerO2OProcessor(id, player, getComponent(ComponentType.Inventory), getCoordsArray()) : extra == 1 ? new ContainerO2OProcessorDouble(id, player, getComponent(ComponentType.Inventory), getCoordsArray()) : extra == 2 ? new ContainerO2OProcessorTriple(id, player, getComponent(ComponentType.Inventory), getCoordsArray()) : null)));

		for (int i = 0; i <= extra; i++) {
			addProcessor(new ComponentProcessor(this).setProcessorNumber(i).canProcess(component -> component.canProcessItem2ItemRecipe(component, ElectrodynamicsRecipeInit.WIRE_MILL_TYPE.get())).process(component -> component.processItem2ItemRecipe(component)).requiredTicks(Constants.WIREMILL_REQUIRED_TICKS).usage(Constants.WIREMILL_USAGE_PER_TICK));
		}
	}

	protected void tickServer(ComponentTickable tick) {
		InventoryUtils.handleExperienceUpgrade(this);
	}

	protected void tickClient(ComponentTickable tickable) {
		boolean has = getType() == DeferredRegisters.TILE_ELECTRICFURNACEDOUBLE.get() ? getProcessor(0).operatingTicks + getProcessor(1).operatingTicks > 0 : getType() == DeferredRegisters.TILE_ELECTRICFURNACETRIPLE.get() ? getProcessor(0).operatingTicks + getProcessor(1).operatingTicks + getProcessor(2).operatingTicks > 0 : getProcessor(0).operatingTicks > 0;
		if (has) {
			if (level.random.nextDouble() < 0.15) {
				level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + level.random.nextDouble(), worldPosition.getY() + level.random.nextDouble() * 0.5 + 0.5, worldPosition.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
			}
			if (tickable.getTicks() % 200 == 0) {
				SoundAPI.playSound(SoundRegister.SOUND_HUM.get(), SoundSource.BLOCKS, 1, 1, worldPosition);
			}
		}
	}

	static {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.3125, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0, 0.3125, 1, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.125, 0.6875, 0.375, 0.5625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.9375, 0.4375, 0.75, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.5625, 0.625, 0.9375, 0.625, 0.75, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.4375, 0.75, 0.9375, 0.5625, 0.8125, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.421875, 0.640625, 0.125, 0.578125, 0.796875, 0.5625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.421875, 0.421875, 0.125, 0.578125, 0.578125, 0.5625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.4375, 0.5625, 0.9375, 0.5625, 0.625, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.25, 0.0625, 0.75, 0.75), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0, 0.0625, 1, 0.125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.125, 0.0625, 1, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.875, 0.0625, 1, 1), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.9375, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.9375, 0.125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.5625, 0.6875, 0.9375, 0.6875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.6875, 1, 0.625, 0.9375), BooleanOp.OR);
		VoxelShapes.registerShape(SubtypeMachine.wiremill, shape, Direction.EAST);
	}
}
