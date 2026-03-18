package com.infinitedimensions.dimension;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.worldgen.CustomChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * DynamicLevelHelper — cria ServerLevels dinâmicos em runtime.
 *
 * Usa reflection para acessar o mapa privado de levels no MinecraftServer,
 * pois o Forge 1.20.1 não expõe uma API pública para isso.
 *
 * Referência técnica: baseado na abordagem do mod "Dimensional Threading"
 * e documentação do Forge sobre DimensionSpecialEffects.
 */
public class DynamicLevelHelper {

    private static Field levelsField = null;
    private static Field execField   = null;

    static {
        try {
            // Campo privado Map<ResourceKey<Level>, ServerLevel> levels
            levelsField = MinecraftServer.class.getDeclaredField("levels");
            levelsField.setAccessible(true);

            // Executor para carregamento de chunks
            execField = MinecraftServer.class.getDeclaredField("executor");
            execField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // Fallback: tenta com nome ofuscado (caso SRG mapping)
            try {
                // Nome SRG em 1.20.1 via ForgeGradle: f_129744_ (levels)
                levelsField = MinecraftServer.class.getDeclaredField("f_129744_");
                levelsField.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                InfiniteDimensions.LOGGER.error("[DynamicLevelHelper] Could not find levels field! Dynamic dimensions will fail.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void createLevel(MinecraftServer server,
                                   ResourceKey<Level> key,
                                   CustomChunkGenerator chunkGen,
                                   DimensionParams params) {
        if (levelsField == null) {
            InfiniteDimensions.LOGGER.error("[DynamicLevelHelper] levels field not accessible, skipping dimension creation.");
            return;
        }

        try {
            Map<ResourceKey<Level>, ServerLevel> levels =
                (Map<ResourceKey<Level>, ServerLevel>) levelsField.get(server);

            if (levels.containsKey(key)) return;

            WorldData worldData = server.getWorldData();
            ServerLevel overworld = server.overworld();

            // Cria dados derivados para o novo level (herda do overworld)
            DerivedLevelData derivedData = new DerivedLevelData(
                worldData,
                worldData.overworldData()
            );

            // Cria o ServerLevel — construtor principal do 1.20.1
            ServerLevel newLevel = new ServerLevel(
                server,
                server.executor,
                server.storageSource,
                derivedData,
                key,
                new LevelStem(
                    server.registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.DIMENSION_TYPE)
                        .getHolderOrThrow(net.minecraft.world.level.dimension.BuiltinDimensionTypes.OVERWORLD),
                    chunkGen
                ),
                new NoopChunkProgressListener(),
                false, // isDebug
                BiomeManager.obfuscateSeed(params.seed),
                java.util.List.of(), // specialSpawners
                true, // tickTime
                null  // dragonFight
            );

            // Registra border change listener
            overworld.getWorldBorder().addListener(
                new BorderChangeListener.DelegateBorderChangeListener(newLevel.getWorldBorder())
            );

            levels.put(key, newLevel);

            // Dispara evento de load do Forge
            MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

            InfiniteDimensions.LOGGER.info("[DynamicLevelHelper] Level '{}' created and registered.", key.location());

        } catch (Exception e) {
            InfiniteDimensions.LOGGER.error("[DynamicLevelHelper] Exception creating level '{}': {}",
                key.location(), e.getMessage());
            e.printStackTrace();
        }
    }

    /** Listener de progresso sem operação (chunks carregados silenciosamente). */
    private static class NoopChunkProgressListener implements ChunkProgressListener {
        @Override public void updateSpawnPos(ChunkPos center) {}
        @Override public void onStatusChange(ChunkPos pos,
            net.minecraft.world.level.chunk.status.ChunkStatus status) {}
        @Override public void start() {}
        @Override public void stop() {}
    }
}
