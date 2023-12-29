package net.bytzo.mc_249136.mixins;

import net.bytzo.mc_249136.ChunkGeneratorStructureStateAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Shadow
	@Final
	private ServerChunkCache chunkSource;

	@Shadow
	@Final
	private StructureCheck structureCheck;

	@Inject(
			method = "<init>",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerLevel;structureCheck:Lnet/minecraft/world/level/levelgen/structure/StructureCheck;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void passGeneratorState(
			MinecraftServer server,
			Executor dispatcher,
			LevelStorageSource.LevelStorageAccess levelStorageAccess,
			ServerLevelData serverLevelData,
			ResourceKey<Level> dimension,
			LevelStem levelStem,
			ChunkProgressListener progressListener,
			boolean isDebug,
			long biomeSeed,
			List<CustomSpawner> customSpawners,
			boolean tickTime,
			RandomSequences randomSequences,
			CallbackInfo callbackInfo
	) {
		((ChunkGeneratorStructureStateAccessor) this.structureCheck).passGeneratorState(this.chunkSource.getGeneratorState());
	}
}
