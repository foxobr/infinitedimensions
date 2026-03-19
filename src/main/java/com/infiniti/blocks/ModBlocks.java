package com.infiniti.blocks;

import com.infiniti.InfinitMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * ModBlocks — todos os blocos exclusivos da Dimensão Infiniti.
 *
 * BLOCOS DE TERRENO:
 *   dune_sand        — areia fina e alaranjada da dimensão
 *   dune_sandstone   — arenito comprimido pelas dunas
 *   hardened_sand    — areia endurecida pela pressão e calor
 *   spice_soil       — solo impregnado de Especiaria (brilho laranja)
 *   salt_flat        — planícies de sal branco refletivo
 *   volcanic_rock    — rocha vulcânica escura em certas áreas
 *
 * MINÉRIOS:
 *   sunstone_ore       — Pedra do Sol (energia solar armazenada)
 *   spice_ore          — Minério de Especiaria (recurso mais raro)
 *   dune_iron_ore      — Ferro das Dunas (versão local do ferro)
 *   bone_crystal_ore   — Cristal Ósseo (deixado por worms mortos)
 *   thermite_ore       — Termita (mineral explosivo e condutor)
 *
 * BLOCOS ESPECIAIS:
 *   sandforge_block    — Bloco de trabalho para receitas avançadas
 *   spice_block        — Bloco puro de Especiaria comprimida
 *   worm_fossil        — Fóssil de worm gigante (decorativo + lore)
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, InfinitMod.MOD_ID);

    // ── Terreno ──
    public static final RegistryObject<Block> DUNE_SAND = register("dune_sand",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(0.5f)
                    .sound(SoundType.SAND)));

    public static final RegistryObject<Block> DUNE_SANDSTONE = register("dune_sandstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> HARDENED_SAND = register("hardened_sand",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(2.0f, 8.0f)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> SPICE_SOIL = register("spice_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(0.6f)
                    .sound(SoundType.GRAVEL)
                    .lightLevel(s -> 3))); // brilho sutil laranja

    public static final RegistryObject<Block> SALT_FLAT = register("salt_flat",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(1.0f)
                    .sound(SoundType.CALCITE)));

    public static final RegistryObject<Block> VOLCANIC_ROCK = register("volcanic_rock",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.5f, 10.0f)
                    .sound(SoundType.BASALT)));

    // ── Minérios ──

    /**
     * Pedra do Sol — encontrado nas camadas intermediárias das dunas.
     * Soltar: SunstoneCrystal (pode ser usado como combustível ou para forja).
     * Raridade: comum/médio
     */
    public static final RegistryObject<Block> SUNSTONE_ORE = register("sunstone_ore",
            () -> new OreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(s -> 4))); // brilho dourado

    /**
     * Minério de Especiaria — o mais valioso da dimensão.
     * Encontrado nas camadas profundas sob as dunas.
     * Soltar: SpiceDust (componente de receitas avançadas).
     * Raridade: muito raro
     */
    public static final RegistryObject<Block> SPICE_ORE = register("spice_ore",
            () -> new OreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(4.5f, 9.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(s -> 6))); // brilho laranja forte

    /**
     * Ferro das Dunas — versão local do ferro, visualmente avermelhado.
     * Soltar: RawDuneIron (fundido na Sandforge em DuneIngot).
     * Raridade: comum
     */
    public static final RegistryObject<Block> DUNE_IRON_ORE = register("dune_iron_ore",
            () -> new OreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_RED)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    /**
     * Cristal Ósseo — formado pelos restos mineralizados de worms.
     * Soltar: BoneCrystalShard (utilizado em armaduras e instrumentos).
     * Raridade: incomum — só aparece perto de WormFossils
     */
    public static final RegistryObject<Block> BONE_CRYSTAL_ORE = register("bone_crystal_ore",
            () -> new OreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(s -> 2)));

    /**
     * Minério de Termita — mineral instável com propriedades explosivas.
     * Soltar: ThermitePowder (perigoso ao manusear; explosivo no Sandforge).
     * Raridade: raro — só em sub-biomas vulcânicos
     */
    public static final RegistryObject<Block> THERMITE_ORE = register("thermite_ore",
            () -> new OreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(3.0f, 4.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    // ── Blocos Especiais ──

    /**
     * Sandforge — bloco de trabalho central do mod.
     * Funciona como uma fornalha + bancada especial:
     *   - Funde RawDuneIron → DuneIngot
     *   - Processa SpiceDust → SpiceEssence
     *   - Cria ferramentas/armaduras especiais com calor da dimensão
     */
    public static final RegistryObject<Block> SANDFORGE_BLOCK = register("sandforge_block",
            () -> new SandforgeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(3.5f, 8.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(s -> 8)
                    .requiresCorrectToolForDrops()));

    /**
     * Bloco de Especiaria pura — decorativo e funcional.
     * Emite partículas alaranjadas e leve brilho.
     * Pode ser usado como combustível ultra-eficiente.
     */
    public static final RegistryObject<Block> SPICE_BLOCK = register("spice_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.SAND)
                    .lightLevel(s -> 10)));

    /**
     * Fóssil de Worm — estrutura gerada proceduralmente nas dunas.
     * Não tem função direta, mas é sinal de BoneCrystalOre próximo.
     * Decorativo e parte do lore da dimensão.
     */
    public static final RegistryObject<Block> WORM_FOSSIL = register("worm_fossil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(2.0f, 5.0f)
                    .sound(SoundType.BONE)));

    // ── Helpers ──

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        // Auto-registra BlockItem junto
        ModItems.ITEMS.register(name, () ->
                new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
