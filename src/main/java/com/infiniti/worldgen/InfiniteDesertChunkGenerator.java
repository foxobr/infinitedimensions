package com.infiniti.worldgen;

import com.infiniti.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.StructureManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * InfiniteDesertChunkGenerator — gera o terreno da Dimensão Infiniti.
 *
 * CARACTERÍSTICAS DO TERRENO:
 *
 *   SUPERFÍCIE (Y 60-120):
 *     Dunas de DuneSand variando de altura 60-100, geradas com Simplex Noise.
 *     Ondulações suaves de larga escala imitam dunas reais do deserto.
 *     Camada de superfície: 2-3 blocos de DuneSand sobre DuneSandstone.
 *
 *   SUBSOLO (Y 20-60):
 *     DuneSandstone comprimido com veios de HardenedSand.
 *     Minérios distribuídos nessa faixa:
 *       DuneIronOre:    Y 15-55, frequente
 *       SunstoneOre:    Y 30-55, moderado
 *       BoneCrystalOre: Y 20-50, incomum (perto de fósseis)
 *
 *   PROFUNDIDADE (Y -64 a 20):
 *     VolcanicRock com veios de ThermiteOre.
 *     SpiceOre aparece Y -40 a 10 (muito raro).
 *
 *   PLANÍCIES DE SAL (SaltFlat):
 *     Geradas em vales entre dunas. Totalmente planas, Y=60.
 *     Sem minérios, mas SpiceOre pode aparecer nas bordas.
 *
 *   AFLORAMENTOS VULCÂNICOS:
 *     Pequenas ilhas de VolcanicRock emergindo das dunas.
 *     Frequentemente contêm ThermiteOre acessível.
 *
 * CÉU:
 *   Sem nuvens. Cor laranja-avermelhada com névoa de areia dourada.
 *   Sol enorme e brilhante (ambientLight alto).
 *   À noite: céu roxo-escuro com estrelas grandes (efeito de planeta distante).
 *
 * ESTRUTURAS GERADAS (via InfinitStructures):
 *   - Ruínas de Arenito (pequenas, frequentes)
 *   - Oásis Seco (depressão sem água, com vegetação morta e itens)
 *   - Acampamento de DuneScavenger
 *   - Templo do Especiaria (raro, contém SpiceOre concentrado)
 *   - Espinha de SandLeviathan (WormFossil em linha de 40+ blocos)
 */
public class InfiniteDesertChunkGenerator extends ChunkGenerator {

    private static final int SEA_LEVEL    = 63;
    private static final int MIN_HEIGHT   = -64;
    private static final int MAX_HEIGHT   = 256;
    private static final int DUNE_BASE    = 62;
    private static final int DUNE_AMPLITUDE = 22; // altura máxima das dunas
    private static final int SALT_FLAT_Y  = 60;

    private final SimplexNoise duneNoise;
    private final SimplexNoise duneDetailNoise;
    private final SimplexNoise saltFlatNoise; // determina onde são planícies
    private final SimplexNoise caveNoise;
    private final long seed;

    public InfiniteDesertChunkGenerator(RegistryAccess registryAccess, long seed) {
        super(new FixedBiomeSource(
                registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                        .getHolderOrThrow(Biomes.DESERT) // bioma base
        ));
        this.seed = seed;
        this.duneNoise      = new SimplexNoise(seed);
        this.duneDetailNoise = new SimplexNoise(seed + 1111L);
        this.saltFlatNoise  = new SimplexNoise(seed + 2222L);
        this.caveNoise      = new SimplexNoise(seed + 3333L);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender,
                                                         RandomState randomState,
                                                         StructureManager structureManager,
                                                         ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> generateChunk(chunk), executor);
    }

    private ChunkAccess generateChunk(ChunkAccess chunk) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = cx * 16 + x;
                int wz = cz * 16 + z;

                // Decide se é planície de sal ou duna
                double saltValue = saltFlatNoise.noise2D(wx * 0.005, wz * 0.005);
                boolean isSaltFlat = saltValue > 0.6;

                int surfaceY = isSaltFlat
                        ? SALT_FLAT_Y
                        : getDuneHeight(wx, wz);

                // Preenche blocos de baixo para cima
                for (int y = MIN_HEIGHT; y <= surfaceY; y++) {
                    BlockState state = getBlockAt(wx, y, wz, surfaceY, isSaltFlat);
                    if (state != null) {
                        chunk.setBlockState(new BlockPos(x, y, z), state, false);
                    }
                }

                // Gera cavernas
                generateCaves(chunk, x, z, wx, wz, surfaceY);
            }
        }
        return chunk;
    }

    private int getDuneHeight(int wx, int wz) {
        // Duna larga (escala longa)
        double large = duneNoise.noise2D(wx * 0.003, wz * 0.003);
        // Detalhes menores
        double detail = duneDetailNoise.noise2D(wx * 0.015, wz * 0.015) * 0.35;
        // Micro-rugosidade
        double micro = duneDetailNoise.noise2D(wx * 0.05, wz * 0.05) * 0.1;

        double combined = (large + detail + micro) * 0.5 + 0.5; // normaliza para [0,1]
        return DUNE_BASE + (int)(combined * DUNE_AMPLITUDE);
    }

    private BlockState getBlockAt(int wx, int y, int wz, int surfaceY, boolean isSaltFlat) {
        // Bedrock na base
        if (y <= MIN_HEIGHT + 4) return Blocks.BEDROCK.defaultBlockState();

        // Camada de VolcanicRock nas profundezas
        if (y < -20) {
            return getDeepBlock(wx, y, wz);
        }

        // Subsolo intermediário
        if (y < 30) {
            return getMidBlock(wx, y, wz);
        }

        // Superfície
        if (y == surfaceY) {
            return isSaltFlat
                    ? ModBlocks.SALT_FLAT.get().defaultBlockState()
                    : ModBlocks.DUNE_SAND.get().defaultBlockState();
        }

        if (y >= surfaceY - 3) {
            return isSaltFlat
                    ? ModBlocks.SALT_FLAT.get().defaultBlockState()
                    : ModBlocks.DUNE_SAND.get().defaultBlockState();
        }

        // Sub-superfície: DuneSandstone
        if (y >= surfaceY - 8) {
            // Veios de HardenedSand
            double vein = duneDetailNoise.noise3D(wx * 0.08, y * 0.1, wz * 0.08);
            if (vein > 0.5) return ModBlocks.HARDENED_SAND.get().defaultBlockState();
            return ModBlocks.DUNE_SANDSTONE.get().defaultBlockState();
        }

        // Minérios da zona média
        return getOreOrBase(wx, y, wz, ModBlocks.DUNE_SANDSTONE.get().defaultBlockState());
    }

    private BlockState getDeepBlock(int wx, int y, int wz) {
        BlockState base = ModBlocks.VOLCANIC_ROCK.get().defaultBlockState();

        // SpiceOre: muito raro, Y -40 a 10
        if (y >= -40 && y <= 10) {
            double spice = duneNoise.noise3D(wx * 0.12, y * 0.15, wz * 0.12);
            if (spice > 0.88) return ModBlocks.SPICE_ORE.get().defaultBlockState();
        }

        // ThermiteOre: raro em zona vulcânica
        double thermite = duneDetailNoise.noise3D(wx * 0.1, y * 0.12, wz * 0.1);
        if (thermite > 0.82) return ModBlocks.THERMITE_ORE.get().defaultBlockState();

        return base;
    }

    private BlockState getMidBlock(int wx, int y, int wz) {
        BlockState base = ModBlocks.DUNE_SANDSTONE.get().defaultBlockState();
        return getOreOrBase(wx, y, wz, base);
    }

    private BlockState getOreOrBase(int wx, int y, int wz, BlockState base) {
        // DuneIronOre: Y 15-55, mais comum
        if (y >= 15 && y <= 55) {
            double iron = caveNoise.noise3D(wx * 0.09, y * 0.11, wz * 0.09);
            if (iron > 0.78) return ModBlocks.DUNE_IRON_ORE.get().defaultBlockState();
        }

        // SunstoneOre: Y 30-55, moderado
        if (y >= 30 && y <= 55) {
            double sun = duneNoise.noise3D(wx * 0.11, y * 0.13, wz * 0.11);
            if (sun > 0.83) return ModBlocks.SUNSTONE_ORE.get().defaultBlockState();
        }

        // BoneCrystalOre: Y 20-50, incomum
        if (y >= 20 && y <= 50) {
            double bone = duneDetailNoise.noise3D(wx * 0.07, y * 0.09, wz * 0.07);
            if (bone > 0.86) return ModBlocks.BONE_CRYSTAL_ORE.get().defaultBlockState();
        }

        return base;
    }

    private void generateCaves(ChunkAccess chunk, int lx, int lz, int wx, int wz, int surfaceY) {
        for (int y = MIN_HEIGHT + 5; y < surfaceY - 5; y++) {
            double cave = caveNoise.noise3D(wx * 0.04, y * 0.04, wz * 0.04);
            if (cave > 0.72) {
                // Caverna — deixa vazio (SpiceOre pode ser visível)
                BlockPos pos = new BlockPos(lx, y, lz);
                chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
            }
        }
    }

    // ── Métodos obrigatórios do ChunkGenerator ──

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
                              BiomeManager biomeManager, StructureManager structureManager,
                              ChunkAccess chunk, GenerationStep.Carving step) {}

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                              RandomState randomState, ChunkAccess chunk) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {}

    @Override
    public int getGenDepth() { return 384; }

    @Override
    public net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.BoundingBoxAccessor
    getBoundingBoxForStructure(net.minecraft.world.level.levelgen.structure.Structure structure) {
        return null; // não usado para dimensão custom
    }

    @Override
    public int getSeaLevel() { return SEA_LEVEL; }

    @Override
    public int getMinY() { return MIN_HEIGHT; }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap,
                              LevelHeightAccessor levelHeightAccessor,
                              RandomState randomState) {
        return getDuneHeight(x, z);
    }

    @Override
    public net.minecraft.world.level.NoiseColumn getBaseColumn(int x, int z,
                                                                LevelHeightAccessor levelHeightAccessor,
                                                                RandomState randomState) {
        return new net.minecraft.world.level.NoiseColumn(MIN_HEIGHT, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("Infiniti Desert Generator | Seed: " + seed);
    }

    @Override
    public Codec<? extends ChunkGenerator> codec() {
        return Codec.unit(this); // codec simplificado para desenvolvimento
    }
}
