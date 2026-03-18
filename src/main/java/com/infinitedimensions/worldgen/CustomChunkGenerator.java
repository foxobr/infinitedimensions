package com.infinitedimensions.worldgen;

import com.infinitedimensions.dimension.DimensionParams;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * CustomChunkGenerator — gera terreno procedural baseado nos parâmetros
 * da dimensão (genes resolvidos).
 *
 * Usa simplex noise seedado para criar variações de altura e
 * distribui blocos baseado na lista de blocos do DimensionParams.
 */
public class CustomChunkGenerator extends ChunkGenerator {

    private final DimensionParams params;
    private final SimplexNoise noise;
    private final SimplexNoise caveNoise;

    // Constantes de geração
    private static final int SEA_LEVEL    = 63;
    private static final int MIN_HEIGHT   = -64;
    private static final int MAX_HEIGHT   = 256;
    private static final int BEDROCK_LAYER = 4;

    public CustomChunkGenerator(DimensionParams params, RegistryAccess registryAccess) {
        super(new FixedBiomeSource(
            registryAccess.registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getHolderOrThrow(net.minecraft.world.level.biome.Biomes.THE_VOID)
        ));
        this.params    = params;
        this.noise     = new SimplexNoise(params.seed);
        this.caveNoise = new SimplexNoise(params.seed + 999L);
    }

    // ── Geração de Chunks ──

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender,
                                                         RandomState randomState, StructureManager structureManager,
                                                         ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> generateChunk(chunk), executor);
    }

    private ChunkAccess generateChunk(ChunkAccess chunk) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        List<Block> blocks = params.terrainBlocks;
        Block primaryBlock   = blocks.size() > 0 ? blocks.get(0) : Blocks.STONE;
        Block secondaryBlock = blocks.size() > 1 ? blocks.get(1) : Blocks.COBBLESTONE;
        Block tertiaryBlock  = blocks.size() > 2 ? blocks.get(2) : Blocks.GRAVEL;
        Block surfaceBlock   = resolveSurfaceBlock();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                // Altura do terreno usando noise em múltiplas oitavas
                int terrainHeight = getTerrainHeight(worldX, worldZ);

                for (int y = MIN_HEIGHT; y <= terrainHeight; y++) {
                    BlockState state = getBlockForLayer(
                        y, terrainHeight,
                        primaryBlock, secondaryBlock, tertiaryBlock, surfaceBlock,
                        worldX, worldZ
                    );

                    if (state != null) {
                        chunk.setBlockState(new BlockPos(x, y, z), state, false);
                    }
                }

                // Gera cavernas
                generateCaves(chunk, x, z, worldX, worldZ, terrainHeight);
            }
        }

        return chunk;
    }

    private int getTerrainHeight(int worldX, int worldZ) {
        // Noise em múltiplas escalas para terreno variado
        double n1 = noise.eval(worldX * 0.01,  worldZ * 0.01)  * 40;
        double n2 = noise.eval(worldX * 0.05,  worldZ * 0.05)  * 15;
        double n3 = noise.eval(worldX * 0.1,   worldZ * 0.1)   * 8;
        double n4 = noise.eval(worldX * 0.005, worldZ * 0.005) * 30;

        // Genes influenciam o perfil do terreno
        double heightMod = 0;
        heightMod += params.sourceGenes.get("chaos")   * 5.0;
        heightMod += params.sourceGenes.get("stable")  * (-3.0);
        heightMod += params.sourceGenes.get("sky")     * 10.0;
        heightMod += params.sourceGenes.get("void")    * (-5.0);

        int baseHeight = (int) (SEA_LEVEL + n1 + n2 + n3 + n4 + heightMod);
        return Math.max(MIN_HEIGHT + 10, Math.min(MAX_HEIGHT - 10, baseHeight));
    }

    private BlockState getBlockForLayer(int y, int terrainHeight,
                                         Block primary, Block secondary, Block tertiary,
                                         Block surface, int worldX, int worldZ) {
        // Bedrock no fundo
        if (y <= MIN_HEIGHT + BEDROCK_LAYER) {
            Random rng = new Random((long) worldX * 341873128712L + (long) worldZ * 132897987541L + y);
            if (y <= MIN_HEIGHT || rng.nextInt(BEDROCK_LAYER) < (y - MIN_HEIGHT)) {
                return Blocks.BEDROCK.defaultBlockState();
            }
        }

        // Superfície
        if (y == terrainHeight) return surface.defaultBlockState();

        // Subsolo próximo da superfície (4 blocos)
        if (y >= terrainHeight - 4) return secondary.defaultBlockState();

        // Variação com noise de terceiro bloco
        double v = noise.eval(worldX * 0.08, y * 0.08, worldZ * 0.08);
        if (v > 0.4) return tertiary.defaultBlockState();

        return primary.defaultBlockState();
    }

    private Block resolveSurfaceBlock() {
        int nature  = params.sourceGenes.get("nature");
        int cold    = params.sourceGenes.get("cold");
        int heat    = params.sourceGenes.get("heat");
        int void_   = params.sourceGenes.get("void");
        int crystal = params.sourceGenes.get("crystal");

        if (nature >= 3)  return Blocks.GRASS_BLOCK;
        if (cold   >= 4)  return Blocks.SNOW_BLOCK;
        if (heat   >= 4)  return Blocks.MAGMA_BLOCK;
        if (void_  >= 3)  return Blocks.SCULK;
        if (crystal >= 4) return Blocks.AMETHYST_BLOCK;
        return Blocks.STONE;
    }

    private void generateCaves(ChunkAccess chunk, int lx, int lz,
                                int worldX, int worldZ, int terrainHeight) {
        double caveThreshold = 0.55 - (params.sourceGenes.get("chaos") * 0.03);

        for (int y = MIN_HEIGHT + BEDROCK_LAYER + 1; y < terrainHeight - 2; y++) {
            double cv = caveNoise.eval(worldX * 0.06, y * 0.06, worldZ * 0.06);
            if (cv > caveThreshold) {
                chunk.setBlockState(new BlockPos(lx, y, lz), Blocks.CAVE_AIR.defaultBlockState(), false);
            }
        }
    }

    // ── Stubs obrigatórios do ChunkGenerator ──

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        // Não precisamos de codec para geração dinâmica
        return net.minecraft.world.level.levelgen.FlatLevelSource.CODEC.xmap(
            flat -> this, gen -> null
        );
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
                              BiomeManager biomeManager, StructureManager structureManager,
                              ChunkAccess chunk, GenerationStep.Carving step) {
        // Cavernas já geradas em fillFromNoise
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                              RandomState randomState, ChunkAccess chunk) {
        // Superfície gerada em fillFromNoise
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {}

    public int getGenDepth() { return MAX_HEIGHT - MIN_HEIGHT; }

    public CompletableFuture<ChunkAccess> createBiomes(Executor executor, RandomState randomState,
                                                        Blender blender,
                                                        ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void applyBiomeDecoration(WorldGenRegion region, StructureManager structureManager) {}

    @Override
    public int getSeaLevel() { return SEA_LEVEL; }

    @Override
    public int getMinY() { return MIN_HEIGHT; }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type,
                              LevelHeightAccessor level, RandomState randomState) {
        return getTerrainHeight(x, z);
    }

    @Override
    public net.minecraft.world.level.NoiseColumn getBaseColumn(int x, int z,
                                                                LevelHeightAccessor level,
                                                                RandomState randomState) {
        int height = getTerrainHeight(x, z);
        BlockState[] states = new BlockState[height - MIN_HEIGHT];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.STONE.defaultBlockState();
        }
        return new net.minecraft.world.level.NoiseColumn(MIN_HEIGHT, states);
    }

    @Override
    public void addDebugScreenInfo(java.util.List<String> info, RandomState randomState,
                                    BlockPos pos) {
        info.add("InfiniteDim: " + params.name + " | seed=" + params.seed);
    }
}
