package com.infiniti.block;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModBlocks — Registro de 14 Blocos
 * 
 * Terreno: DuneSand, DuneSandstone, HardenedSand, SpiceSoil, SaltFlat, VolcanicRock
 * Minérios: SunstoneOre, SpiceOre, DuneIronOre, BoneCrystalOre, ThermiteOre
 * Especiais: Sandforge, SpiceBlock, WormFossil
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
        ForgeRegistries.BLOCKS, "infiniti");
    
    // ═══════════════════════════════════════════════════════════════════════
    // BLOCOS DE TERRENO (6)
    // ═══════════════════════════════════════════════════════════════════════
    
    public static final RegistryObject<Block> DUNE_SAND = BLOCKS.register("dune_sand",
        () -> new SandBlock(0xDEA855, // Cor laranja-avermelhada
            BlockBehaviour.Properties.of(Material.SAND)
                .strength(0.5f)
                .sound(SoundType.SAND)
        ));
    
    public static final RegistryObject<Block> DUNE_SANDSTONE = BLOCKS.register("dune_sandstone",
        () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
            .strength(1.5f, 6.0f)
            .sound(SoundType.STONE)
        ));
    
    public static final RegistryObject<Block> HARDENED_SAND = BLOCKS.register("hardened_sand",
        () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
            .strength(2.0f, 8.0f)
            .sound(SoundType.STONE)
        ));
    
    public static final RegistryObject<Block> SPICE_SOIL = BLOCKS.register("spice_soil",
        () -> new Block(BlockBehaviour.Properties.of(Material.DIRT)
            .strength(0.6f)
            .sound(SoundType.SAND)
            .lightLevel(state -> 3) // Luz 3
        ));
    
    public static final RegistryObject<Block> SALT_FLAT = BLOCKS.register("salt_flat",
        () -> new Block(BlockBehaviour.Properties.of(Material.SAND)
            .strength(1.0f)
            .sound(SoundType.SAND)
        ));
    
    public static final RegistryObject<Block> VOLCANIC_ROCK = BLOCKS.register("volcanic_rock",
        () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
            .strength(2.5f, 10.0f)
            .sound(SoundType.STONE)
        ));
    
    // ═══════════════════════════════════════════════════════════════════════
    // MINÉRIOS (5)
    // ═══════════════════════════════════════════════════════════════════════
    
    public static final RegistryObject<Block> SUNSTONE_ORE = BLOCKS.register("sunstone_ore",
        () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f, 3.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 4), // Luz 4
            UniformInt.of(1, 2)
        ));
    
    public static final RegistryObject<Block> SPICE_ORE = BLOCKS.register("spice_ore",
        () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f, 3.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 6), // Luz 6
            UniformInt.of(1, 2)
        ));
    
    public static final RegistryObject<Block> DUNE_IRON_ORE = BLOCKS.register("dune_iron_ore",
        () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f, 3.0f)
            .sound(SoundType.STONE),
            UniformInt.of(1, 2)
        ));
    
    public static final RegistryObject<Block> BONE_CRYSTAL_ORE = BLOCKS.register("bone_crystal_ore",
        () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f, 3.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 2), // Luz 2
            UniformInt.of(1, 2)
        ));
    
    public static final RegistryObject<Block> THERMITE_ORE = BLOCKS.register("thermite_ore",
        () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(3.0f, 3.0f)
            .sound(SoundType.STONE),
            UniformInt.of(1, 2)
        ));
    
    // ═══════════════════════════════════════════════════════════════════════
    // BLOCOS ESPECIAIS (3)
    // ═══════════════════════════════════════════════════════════════════════
    
    public static final RegistryObject<Block> SANDFORGE = BLOCKS.register("sandforge",
        () -> new SandforgeBlock(BlockBehaviour.Properties.of(Material.METAL)
            .strength(3.5f, 3.5f)
            .sound(SoundType.METAL)
            .lightLevel(state -> 8) // Luz 8
        ));
    
    public static final RegistryObject<Block> SPICE_BLOCK = BLOCKS.register("spice_block",
        () -> new Block(BlockBehaviour.Properties.of(Material.METAL)
            .strength(5.0f, 6.0f)
            .sound(SoundType.METAL)
            .lightLevel(state -> 10) // Luz 10
        ));
    
    public static final RegistryObject<Block> WORM_FOSSIL = BLOCKS.register("worm_fossil",
        () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
            .strength(2.0f, 6.0f)
            .sound(SoundType.STONE)
        ));
}
