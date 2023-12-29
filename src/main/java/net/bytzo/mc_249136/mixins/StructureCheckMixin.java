package net.bytzo.mc_249136.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bytzo.mc_249136.ChunkGeneratorStructureStateAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(StructureCheck.class)
public class StructureCheckMixin implements ChunkGeneratorStructureStateAccessor {
	@Unique
	private ChunkGeneratorStructureState generatorState;

	@Unique
	@Override
	public void passGeneratorState(ChunkGeneratorStructureState state) {
		this.generatorState = state;
	}

	@WrapOperation(
			method = "canCreateStructure(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/Structure;)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/Structure;findValidGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"
			)
	)
	private Optional<Structure.GenerationStub> getGenerationPointIfStructureChunk(Structure instance, Structure.GenerationContext generationContext, Operation<Optional<Structure.GenerationStub>> original) {
		var structureHolder = generationContext.registryAccess().registryOrThrow(Registries.STRUCTURE).wrapAsHolder(instance);

		if (this.generatorState != null) {
			for (var structurePlacement : this.generatorState.getPlacementsForStructure(structureHolder)) {
				if (structurePlacement.isStructureChunk(this.generatorState, generationContext.chunkPos().x, generationContext.chunkPos().z)) {
					return original.call(instance, generationContext);
				}
			}
		}

		return Optional.empty();
	}
}
