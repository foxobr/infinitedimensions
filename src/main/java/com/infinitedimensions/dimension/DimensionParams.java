package com.infinitedimensions.dimension;

import com.infinitedimensions.genes.ItemGenes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Contém todos os parâmetros necessários para criar e configurar
 * uma dimensão personalizada.
 */
public class DimensionParams {

    public final long seed;
    public final String name;

    // Blocos que compõem o terreno (ordenados por frequência decrescente)
    public final List<Block> terrainBlocks;

    // Efeitos aplicados ao jogador ao entrar na dimensão
    public final List<MobEffect> playerEffects;

    // Propriedades físicas
    public final float gravity;
    public final boolean hasSkylight;
    public final boolean alwaysDay;
    public final boolean alwaysNight;

    // Cores visuais (RGB packed int)
    public final int fogColor;
    public final int skyColor;
    public final float ambientLight;

    // Genes que originaram essa dimensão (útil para debug e tooltip)
    public final ItemGenes sourceGenes;

    public DimensionParams(long seed, String name,
                           List<Block> terrainBlocks,
                           List<MobEffect> playerEffects,
                           float gravity,
                           boolean hasSkylight,
                           boolean alwaysDay,
                           boolean alwaysNight,
                           int fogColor,
                           int skyColor,
                           float ambientLight,
                           ItemGenes sourceGenes) {
        this.seed          = seed;
        this.name          = name;
        this.terrainBlocks = terrainBlocks;
        this.playerEffects = playerEffects;
        this.gravity       = gravity;
        this.hasSkylight   = hasSkylight;
        this.alwaysDay     = alwaysDay;
        this.alwaysNight   = alwaysNight;
        this.fogColor      = fogColor;
        this.skyColor      = skyColor;
        this.ambientLight  = ambientLight;
        this.sourceGenes   = sourceGenes;
    }

    /** ID único da dimensão, baseado na seed. */
    public String dimensionId() {
        return "dim_" + Long.toHexString(Math.abs(seed));
    }

    @Override
    public String toString() {
        return String.format("DimensionParams{name='%s', seed=%d, blocks=%d, effects=%d, gravity=%.3f}",
            name, seed, terrainBlocks.size(), playerEffects.size(), gravity);
    }
}
