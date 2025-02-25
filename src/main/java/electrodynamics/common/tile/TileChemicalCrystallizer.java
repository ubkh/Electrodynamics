package electrodynamics.common.tile;

import electrodynamics.DeferredRegisters;
import electrodynamics.SoundRegister;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.common.inventory.container.tile.ContainerChemicalCrystallizer;
import electrodynamics.common.recipe.ElectrodynamicsRecipeInit;
import electrodynamics.common.settings.Constants;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentFluidHandlerMulti;
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

public class TileChemicalCrystallizer extends GenericTile {
	public static final int MAX_TANK_CAPACITY = 5000;

	public TileChemicalCrystallizer(BlockPos worldPosition, BlockState blockState) {
		super(DeferredRegisters.TILE_CHEMICALCRYSTALLIZER.get(), worldPosition, blockState);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler());
		addComponent(new ComponentTickable().tickServer(this::tickServer).tickClient(this::tickClient));
		addComponent(new ComponentElectrodynamic(this).relativeInput(Direction.NORTH).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE * 2).maxJoules(Constants.CHEMICALCRYSTALLIZER_USAGE_PER_TICK * 10));
		addComponent(((ComponentFluidHandlerMulti) new ComponentFluidHandlerMulti(this).relativeInput(Direction.values())).setAddFluidsValues(ElectrodynamicsRecipeInit.CHEMICAL_CRYSTALIZER_TYPE.get(), MAX_TANK_CAPACITY, true, false));
		addComponent(new ComponentInventory(this).size(5).relativeSlotFaces(0, Direction.values()).outputs(1).bucketInputs(1).upgrades(3).processors(1).validUpgrades(ContainerChemicalCrystallizer.VALID_UPGRADES).valid(machineValidator()).shouldSendInfo());
		addComponent(new ComponentProcessor(this).setProcessorNumber(0).canProcess(component -> component.consumeBucket().canProcessFluid2ItemRecipe(component, ElectrodynamicsRecipeInit.CHEMICAL_CRYSTALIZER_TYPE.get())).process(component -> component.processFluid2ItemRecipe(component)).requiredTicks(Constants.CHEMICALCRYSTALLIZER_REQUIRED_TICKS).usage(Constants.CHEMICALCRYSTALLIZER_USAGE_PER_TICK));
		addComponent(new ComponentContainerProvider("container.chemicalcrystallizer").createMenu((id, player) -> new ContainerChemicalCrystallizer(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	protected void tickServer(ComponentTickable tick) {
		InventoryUtils.handleExperienceUpgrade(this);
	}

	protected void tickClient(ComponentTickable tickable) {
		ComponentProcessor processor = getComponent(ComponentType.Processor);
		if (processor.operatingTicks > 0 && level.random.nextDouble() < 0.15) {
			Direction direction = this.<ComponentDirection>getComponent(ComponentType.Direction).getDirection();
			double d4 = level.random.nextDouble();
			double d5 = direction.getAxis() == Direction.Axis.X ? direction.getStepX() * (direction.getStepX() == -1 ? 0 : 1) : d4;
			double d6 = level.random.nextDouble();
			double d7 = direction.getAxis() == Direction.Axis.Z ? direction.getStepZ() * (direction.getStepZ() == -1 ? 0 : 1) : d4;
			level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + d5, worldPosition.getY() + d6, worldPosition.getZ() + d7, 0.0D, 0.0D, 0.0D);
		}
		if (processor.operatingTicks > 0 && tickable.getTicks() % 200 == 0) {
			SoundAPI.playSound(SoundRegister.SOUND_HUM.get(), SoundSource.BLOCKS, 1, 1, worldPosition);
		}
	}
}
