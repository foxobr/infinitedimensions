package com.infinitedimensions.genes;

import com.infinitedimensions.dimension.DimensionParams;
import com.infinitedimensions.util.NameGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * GeneResolver — transforma a soma de genes de uma combinação
 * em parâmetros concretos de dimensão.
 */
public class GeneResolver {

    /**
     * Resolve os genes de todos os ingredientes + nomes e retorna
     * os parâmetros completos da dimensão.
     */
    public static DimensionParams resolve(List<String> ingredients, List<String> customNames) {

        // 1. Soma os genes de todos os itens
        ItemGenes total = ItemGenes.EMPTY;
        for (String itemId : ingredients) {
            total = total.add(GeneTable.getGenes(itemId));
        }

        // 2. Soma genes dos nomes de pessoas
        for (String name : customNames) {
            total = total.add(GeneTable.getNameGenes(name));
        }

        // 3. Gera seed determinística da combinação
        long seed = buildSeed(ingredients, customNames);

        // 4. Resolve blocos predominantes
        List<Block> blocks = resolveBlocks(total);

        // 5. Resolve efeitos de status
        List<MobEffect> effects = resolveEffects(total);

        // 6. Resolve propriedades físicas da dimensão
        float gravity       = resolveGravity(total);
        boolean hasSkylight  = total.get("sky") >= 2 || total.get("dark") < 1;
        boolean alwaysDay    = total.get("light") >= 4;
        boolean alwaysNight  = total.get("dark") >= 4;
        int fogColor         = resolveFogColor(total);
        int skyColor         = resolveSkyColor(total);
        float ambientLight   = resolveAmbientLight(total);

        // 7. Gera nome da dimensão
        String dimensionName = NameGenerator.generate(seed, total.dominantGeneName());

        return new DimensionParams(
            seed, dimensionName,
            blocks, effects,
            gravity, hasSkylight, alwaysDay, alwaysNight,
            fogColor, skyColor, ambientLight,
            total
        );
    }

    // ── Seed ──

    public static long buildSeed(List<String> ingredients, List<String> names) {
        long seed = 1L;
        for (String s : ingredients) {
            seed = seed * 31L + s.hashCode();
        }
        for (String n : names) {
            seed = seed * 37L + n.hashCode();
        }
        return seed;
    }

    // ── Blocos ──

    private static List<Block> resolveBlocks(ItemGenes g) {
        List<Block> list = new ArrayList<>();

        // Bloco base (sempre presente)
        list.add(Blocks.STONE);

        // Primário: bloco mais forte vence
        int heat    = g.get("heat");
        int cold    = g.get("cold");
        int dark    = g.get("dark");
        int undead  = g.get("undead");
        int crystal = g.get("crystal");
        int wetness = g.get("wetness");
        int void_   = g.get("void");
        int nature  = g.get("nature");
        int sky     = g.get("sky");
        int poison  = g.get("poison");

        if (heat >= 3)    { list.add(Blocks.MAGMA_BLOCK); list.add(Blocks.NETHERRACK); }
        if (heat >= 5)    { list.add(Blocks.BASALT); list.add(Blocks.BLACKSTONE); }
        if (cold >= 3)    { list.add(Blocks.PACKED_ICE); list.add(Blocks.SNOW_BLOCK); }
        if (cold >= 5)    { list.add(Blocks.BLUE_ICE); list.add(Blocks.POWDER_SNOW); }
        if (dark >= 3)    { list.add(Blocks.BLACKSTONE); list.add(Blocks.OBSIDIAN); }
        if (dark >= 5)    { list.add(Blocks.CRYING_OBSIDIAN); list.add(Blocks.SOUL_SAND); }
        if (undead >= 3)  { list.add(Blocks.BONE_BLOCK); list.add(Blocks.SOUL_SOIL); }
        if (undead >= 5)  { list.add(Blocks.WITHER_ROSE); }
        if (crystal >= 3) { list.add(Blocks.AMETHYST_BLOCK); list.add(Blocks.CALCITE); }
        if (crystal >= 5) { list.add(Blocks.DIAMOND_ORE); list.add(Blocks.EMERALD_ORE); }
        if (wetness >= 3) { list.add(Blocks.CLAY); list.add(Blocks.PRISMARINE); }
        if (wetness >= 5) { list.add(Blocks.SEA_LANTERN); list.add(Blocks.DARK_PRISMARINE); }
        if (void_ >= 3)   { list.add(Blocks.SCULK); list.add(Blocks.DEEPSLATE); }
        if (void_ >= 5)   { list.add(Blocks.SCULK_CATALYST); }
        if (nature >= 3)  { list.add(Blocks.MOSS_BLOCK); list.add(Blocks.GRASS_BLOCK); }
        if (nature >= 5)  { list.add(Blocks.JUNGLE_LOG); list.add(Blocks.AZALEA); }
        if (sky >= 3)     { list.add(Blocks.GLOWSTONE); list.add(Blocks.END_STONE); }
        if (sky >= 5)     { list.add(Blocks.PURPUR_BLOCK); }
        if (poison >= 3)  { list.add(Blocks.SLIME_BLOCK); }

        // Garante ao menos 3 blocos únicos
        if (list.size() < 3) list.add(Blocks.COBBLESTONE);

        return list;
    }

    // ── Efeitos de Status ──

    private static List<MobEffect> resolveEffects(ItemGenes g) {
        List<MobEffect> effects = new ArrayList<>();

        if (g.get("poison")  >= 2)  effects.add(MobEffects.POISON);
        if (g.get("heat")    >= 4)  effects.add(MobEffects.FIRE_RESISTANCE);
        if (g.get("cold")    >= 4)  effects.add(MobEffects.MOVEMENT_SLOWDOWN);
        if (g.get("dark")    >= 3)  effects.add(MobEffects.BLINDNESS);
        if (g.get("undead")  >= 4)  effects.add(MobEffects.WITHER);
        if (g.get("crystal") >= 4)  effects.add(MobEffects.DAMAGE_RESISTANCE);
        if (g.get("wetness") >= 3)  effects.add(MobEffects.WATER_BREATHING);
        if (g.get("sky")     >= 3)  effects.add(MobEffects.LEVITATION);
        if (g.get("nature")  >= 3)  effects.add(MobEffects.REGENERATION);
        if (g.get("magic")   >= 4)  effects.add(MobEffects.NIGHT_VISION);
        if (g.get("chaos")   >= 3)  effects.add(MobEffects.CONFUSION);
        if (g.get("void")    >= 3)  effects.add(MobEffects.DARKNESS);
        if (g.get("bouncy")  >= 4)  effects.add(MobEffects.JUMP);
        if (g.get("hostile") >= 4)  effects.add(MobEffects.WEAKNESS);
        if (g.get("stable")  >= 4)  effects.add(MobEffects.ABSORPTION);
        if (g.get("light")   >= 4)  effects.add(MobEffects.GLOWING);

        return effects;
    }

    // ── Gravidade ──

    private static float resolveGravity(ItemGenes g) {
        float base = 0.08f; // gravidade padrão do MC
        base += g.get("heat")    * 0.002f;
        base -= g.get("sky")     * 0.008f;
        base -= g.get("bouncy")  * 0.005f;
        base += g.get("stable")  * 0.003f;
        base += g.get("void")    * 0.005f;
        // Clamp entre 0.02 (quase sem gravidade) e 0.15 (muito pesado)
        return Math.max(0.02f, Math.min(0.15f, base));
    }

    // ── Cores ──

    private static int resolveFogColor(ItemGenes g) {
        int r = 200, gr = 200, b = 200;
        r  += g.get("heat")   * 20 - g.get("cold")  * 15;
        b  += g.get("cold")   * 20 - g.get("heat")  * 15;
        gr += g.get("nature") * 20 - g.get("void")  * 15;
        gr -= g.get("dark")   * 15;
        r  -= g.get("dark")   * 10;
        b  -= g.get("dark")   * 10;
        r  = clamp(r, 10, 255);
        gr = clamp(gr, 10, 255);
        b  = clamp(b, 10, 255);
        return (r << 16) | (gr << 8) | b;
    }

    private static int resolveSkyColor(ItemGenes g) {
        int r = 100, gr = 150, b = 220;
        r  += g.get("heat")   * 25;
        b  += g.get("cold")   * 20 + g.get("magic") * 15;
        gr += g.get("nature") * 20;
        r  -= g.get("void")   * 20;
        gr -= g.get("void")   * 20;
        b  -= g.get("void")   * 20;
        r  = clamp(r, 0, 255);
        gr = clamp(gr, 0, 255);
        b  = clamp(b, 0, 255);
        return (r << 16) | (gr << 8) | b;
    }

    private static float resolveAmbientLight(ItemGenes g) {
        float base = 0.0f;
        base += g.get("light") * 0.05f;
        base -= g.get("dark")  * 0.03f;
        base += g.get("magic") * 0.02f;
        return Math.max(0.0f, Math.min(1.0f, base));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
