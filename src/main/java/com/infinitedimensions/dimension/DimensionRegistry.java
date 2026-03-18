package com.infinitedimensions.dimension;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.worldgen.CustomChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * DimensionRegistry — cria e mantém dimensões dinâmicas em runtime.
 *
 * Cada dimensão é identificada pelo ID da seed em hexadecimal.
 * Uma vez criada, a dimensão é mantida em cache para reutilização.
 */
public class DimensionRegistry {

    // Cache: dimensionId → params da dimensão
    private static final Map<String, DimensionParams> PARAMS_CACHE = new HashMap<>();

    // Cache: dimensionId → ResourceKey da dimensão
    private static final Map<String, ResourceKey<Level>> KEY_CACHE = new HashMap<>();

    public static void register(IEventBus modBus) {
        modBus.addListener(DimensionRegistry::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        InfiniteDimensions.LOGGER.info("[DimensionRegistry] Ready for dynamic dimension creation.");
    }

    /**
     * Retorna (ou cria) a ResourceKey de uma dimensão.
     * Caso a dimensão ainda não exista no servidor, ela é registrada dinamicamente.
     */
    public static ResourceKey<Level> getOrCreateDimension(MinecraftServer server,
                                                            DimensionParams params) {
        String dimId = params.dimensionId();

        if (KEY_CACHE.containsKey(dimId)) {
            return KEY_CACHE.get(dimId);
        }

        // Cria a ResourceKey
        ResourceKey<Level> key = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(InfiniteDimensions.MOD_ID, dimId)
        );

        // Cria o LevelStem (dimensão) dinamicamente
        try {
            createDimensionLevel(server, key, params);
            PARAMS_CACHE.put(dimId, params);
            KEY_CACHE.put(dimId, key);
            InfiniteDimensions.LOGGER.info("[DimensionRegistry] Created dimension: {} ({})",
                params.name, dimId);
        } catch (Exception e) {
            InfiniteDimensions.LOGGER.error("[DimensionRegistry] Failed to create dimension: {}", e.getMessage());
        }

        return key;
    }

    /**
     * Registra o ServerLevel para a dimensão no servidor.
     * Usa reflection para acessar o mapa interno de levels do servidor,
     * pois o Forge 1.20.1 não expõe isso diretamente.
     */
    @SuppressWarnings("unchecked")
    private static void createDimensionLevel(MinecraftServer server,
                                              ResourceKey<Level> key,
                                              DimensionParams params) {
        // Obtém o registry de DimensionTypes do servidor
        Registry<DimensionType> dimTypeRegistry =
            server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

        // Usa o DimensionType do Overworld como base (poderia ser customizado)
        ResourceKey<DimensionType> dimTypeKey = Level.OVERWORLD.location().equals(key.location())
            ? net.minecraft.world.level.dimension.BuiltinDimensionTypes.OVERWORLD
            : net.minecraft.world.level.dimension.BuiltinDimensionTypes.NETHER;

        // Cria o gerador de chunks personalizado
        CustomChunkGenerator chunkGen = new CustomChunkGenerator(params, server.registryAccess());

        // Registra o LevelStem dinamicamente via DimensionDataStorage do Forge
        // NOTA: Em 1.20.1 isso requer ForgeHooks ou acesso direto ao campo levels
        // Esta implementação usa a abordagem mais portável disponível
        DynamicLevelHelper.createLevel(server, key, chunkGen, params);
    }

    public static DimensionParams getParams(String dimId) {
        return PARAMS_CACHE.get(dimId);
    }

    public static boolean exists(String dimId) {
        return KEY_CACHE.containsKey(dimId);
    }

    public static ResourceKey<Level> getKey(String dimId) {
        return KEY_CACHE.get(dimId);
    }
}
