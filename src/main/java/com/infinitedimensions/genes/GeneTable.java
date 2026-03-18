package com.infinitedimensions.genes;

import java.util.HashMap;
import java.util.Map;

/**
 * GeneTable — mapeia cada item do Minecraft para "genes" que influenciam
 * as propriedades da dimensão gerada.
 *
 * Cada gene é um int entre -5 e +5.
 * Genes positivos reforçam a propriedade; negativos a suprimem.
 *
 * Propriedades disponíveis:
 *   heat, cold, dark, light, poison, undead, crystal, wetness,
 *   bouncy, hostile, stable, void, sky, nature, chaos, magic
 */
public class GeneTable {

    // Seed de variação extra por nome de pessoa (hashcode das letras)
    // Cada caractere contribui de forma única
    public static final int NAME_GENE_MULTIPLIER = 7;

    private static final Map<String, ItemGenes> TABLE = new HashMap<>();

    static {
        // ── FOGO / CALOR ──
        reg("minecraft:blaze_powder",        heat(4), light(2), hostile(2));
        reg("minecraft:blaze_rod",           heat(3), light(3), hostile(1));
        reg("minecraft:fire_charge",         heat(5), light(2), hostile(3));
        reg("minecraft:magma_cream",         heat(3), wetness(-2), hostile(1));
        reg("minecraft:lava_bucket",         heat(5), cold(-3), hostile(2));
        reg("minecraft:nether_star",         heat(2), magic(5), light(4), chaos(2));
        reg("minecraft:netherrack",          heat(2), hostile(1));
        reg("minecraft:soul_sand",           undead(3), dark(2), hostile(2));
        reg("minecraft:soul_soil",           undead(3), dark(2));
        reg("minecraft:ghast_tear",          heat(1), magic(3), sky(2));

        // ── GELO / FRIO ──
        reg("minecraft:ice",                 cold(3), wetness(2));
        reg("minecraft:packed_ice",          cold(4), wetness(1), stable(2));
        reg("minecraft:blue_ice",            cold(5), wetness(1), stable(3));
        reg("minecraft:snow_block",          cold(2), light(1));
        reg("minecraft:snowball",            cold(1), bouncy(1));
        reg("minecraft:powder_snow_bucket",  cold(4), wetness(2));

        // ── VENENO / NATUREZA SOMBRIA ──
        reg("minecraft:spider_eye",          poison(3), dark(2), hostile(1));
        reg("minecraft:fermented_spider_eye",poison(4), dark(3), chaos(2));
        reg("minecraft:slimeball",           wetness(3), bouncy(4), poison(1));
        reg("minecraft:poisonous_potato",    poison(2), nature(1));
        reg("minecraft:pufferfish",          poison(3), wetness(2));
        reg("minecraft:venom_bottle",        poison(5), hostile(2));  // modded fallback

        // ── MAGIA / ARCANO ──
        reg("minecraft:ender_pearl",         magic(3), void_g(3), chaos(1));
        reg("minecraft:ender_eye",           magic(4), void_g(4), dark(1));
        reg("minecraft:dragon_breath",       magic(5), void_g(3), poison(2), chaos(3));
        reg("minecraft:end_crystal",         magic(4), crystal(3), chaos(2));
        reg("minecraft:chorus_fruit",        magic(2), void_g(1), nature(1));
        reg("minecraft:experience_bottle",   magic(3), light(1));
        reg("minecraft:enchanted_book",      magic(3), stable(1));

        // ── MORTOS-VIVOS / TREVAS ──
        reg("minecraft:bone",                undead(3), dark(1));
        reg("minecraft:bone_meal",           undead(1), nature(2));
        reg("minecraft:rotten_flesh",        undead(3), poison(1), dark(1));
        reg("minecraft:wither_skeleton_skull",undead(5), dark(4), hostile(3));
        reg("minecraft:skeleton_skull",      undead(4), dark(3));
        reg("minecraft:zombie_head",         undead(4), dark(2), hostile(1));
        reg("minecraft:creeper_head",        undead(2), chaos(3), hostile(2));

        // ── CRISTAIS / MINERAIS ──
        reg("minecraft:diamond",             crystal(4), stable(3), light(1));
        reg("minecraft:amethyst_shard",      crystal(3), magic(2), light(2));
        reg("minecraft:quartz",              crystal(2), light(2), stable(1));
        reg("minecraft:emerald",             crystal(3), nature(2), stable(2));
        reg("minecraft:lapis_lazuli",        crystal(2), magic(2), cold(1));
        reg("minecraft:ruby",                crystal(3), heat(2));    // modded fallback
        reg("minecraft:raw_iron",            stable(2), crystal(1));
        reg("minecraft:raw_gold",            stable(1), crystal(2), magic(1));
        reg("minecraft:raw_copper",          stable(1), nature(1));
        reg("minecraft:netherite_scrap",     heat(2), stable(4), void_g(1));

        // ── CÉU / AR ──
        reg("minecraft:feather",             sky(3), light(1));
        reg("minecraft:phantom_membrane",    sky(3), dark(2), undead(1));
        reg("minecraft:elytra",              sky(5), stable(2));
        reg("minecraft:firework_rocket",     sky(2), chaos(2), light(2));
        reg("minecraft:wind_charge",         sky(4), chaos(2), bouncy(3));

        // ── ÁGUA / OCEANO ──
        reg("minecraft:water_bucket",        wetness(4), cold(1));
        reg("minecraft:prismarine_shard",    wetness(3), crystal(1), cold(1));
        reg("minecraft:prismarine_crystals", wetness(2), crystal(2), magic(1), light(2));
        reg("minecraft:heart_of_the_sea",    wetness(5), magic(4), stable(2));
        reg("minecraft:nautilus_shell",      wetness(3), stable(2));
        reg("minecraft:turtle_helmet",       wetness(3), nature(2), stable(1));
        reg("minecraft:sponge",              wetness(-3), stable(1));  // absorve — inverte wetness

        // ── NATUREZA / VIDA ──
        reg("minecraft:oak_sapling",         nature(3));
        reg("minecraft:jungle_sapling",      nature(4), wetness(1), heat(1));
        reg("minecraft:cactus",              nature(2), heat(2), hostile(1), cold(-2));
        reg("minecraft:moss_block",          nature(4), wetness(2), cold(1));
        reg("minecraft:azalea",              nature(3), magic(1));
        reg("minecraft:lily_pad",            nature(2), wetness(3));
        reg("minecraft:bee_nest",            nature(3), sky(1), bouncy(1));
        reg("minecraft:honeycomb",           nature(2), stable(1), bouncy(2));
        reg("minecraft:wheat_seeds",         nature(1), stable(1));

        // ── CAOS / INSTABILIDADE ──
        reg("minecraft:tnt",                 chaos(5), hostile(3), heat(1));
        reg("minecraft:gunpowder",           chaos(3), hostile(1));
        reg("minecraft:flint_and_steel",     chaos(2), heat(2));

        // ── VAZIO / TREVAS PROFUNDAS ──
        reg("minecraft:sculk",               void_g(2), dark(3), hostile(1));
        reg("minecraft:sculk_sensor",        void_g(2), dark(2), hostile(2));
        reg("minecraft:sculk_shrieker",      void_g(3), dark(4), hostile(4), chaos(2));
        reg("minecraft:echo_shard",          void_g(4), dark(3), magic(2));
        reg("minecraft:disc_fragment_5",     void_g(3), dark(2), magic(2));
        reg("minecraft:ancient_debris",      void_g(2), heat(2), stable(4));

        // ── ESTABILIDADE / NEUTRO ──
        reg("minecraft:cobblestone",         stable(1));
        reg("minecraft:stone",               stable(2));
        reg("minecraft:obsidian",            stable(4), heat(1), void_g(1));
        reg("minecraft:bedrock",             stable(5), void_g(2));
        reg("minecraft:dirt",                nature(1), stable(1));
        reg("minecraft:sand",                heat(1), stable(1), cold(-1));
        reg("minecraft:gravel",              stable(1));
        reg("minecraft:clay_ball",           stable(1), wetness(1));
    }

    // ---------- Lookup ----------

    public static ItemGenes getGenes(String itemId) {
        return TABLE.getOrDefault(itemId, ItemGenes.EMPTY);
    }

    /** Aplica os efeitos de um nome de pessoa à semente de genes. */
    public static ItemGenes getNameGenes(String name) {
        int hash = 0;
        for (char c : name.toLowerCase().toCharArray()) {
            hash = hash * 31 + c;
        }
        // Distribui o hash entre as propriedades de forma pseudoaleatória
        int h = Math.abs(hash);
        return new ItemGenes(
            (h % 16 < 8)  ? (h % 3) + 1 : -((h % 3) + 1),   // heat
            (h % 17 < 9)  ? (h % 3) + 1 : -((h % 3) + 1),   // cold
            (h % 13 < 7)  ? (h % 3)     : -((h % 3)),        // dark
            (h % 11 < 6)  ? (h % 3)     : -((h % 3)),        // light
            (h % 19 < 10) ? (h % 2)     : 0,                  // poison
            (h % 23 < 12) ? (h % 2)     : 0,                  // undead
            (h % 7  < 4)  ? (h % 3) + 1 : 0,                  // crystal
            (h % 29 < 15) ? (h % 2)     : -((h % 2)),        // wetness
            (h % 31 < 16) ? (h % 2)     : 0,                  // bouncy
            (h % 37 < 19) ? (h % 2)     : -((h % 2)),        // hostile
            (h % 41 < 21) ? (h % 3)     : 0,                  // stable
            (h % 43 < 22) ? (h % 2)     : 0,                  // void
            (h % 47 < 24) ? (h % 2)     : 0,                  // sky
            (h % 53 < 27) ? (h % 3) + 1 : 0,                  // nature
            (h % 59 < 30) ? (h % 2)     : 0,                  // chaos
            (h % 61 < 31) ? (h % 3)     : 0                   // magic
        );
    }

    // ---------- Builders internos ----------

    private static void reg(String id, int... geneValues) {
        TABLE.put(id, ItemGenes.of(geneValues));
    }

    // Convenções: cada método retorna o par (índice, valor) para o gene
    private static int[] heat(int v)    { return g(0, v);  }
    private static int[] cold(int v)    { return g(1, v);  }
    private static int[] dark(int v)    { return g(2, v);  }
    private static int[] light(int v)   { return g(3, v);  }
    private static int[] poison(int v)  { return g(4, v);  }
    private static int[] undead(int v)  { return g(5, v);  }
    private static int[] crystal(int v) { return g(6, v);  }
    private static int[] wetness(int v) { return g(7, v);  }
    private static int[] bouncy(int v)  { return g(8, v);  }
    private static int[] hostile(int v) { return g(9, v);  }
    private static int[] stable(int v)  { return g(10, v); }
    private static int[] void_g(int v)  { return g(11, v); }
    private static int[] sky(int v)     { return g(12, v); }
    private static int[] nature(int v)  { return g(13, v); }
    private static int[] chaos(int v)   { return g(14, v); }
    private static int[] magic(int v)   { return g(15, v); }

    private static int[] g(int index, int value) {
        return new int[]{index, value};
    }
}
