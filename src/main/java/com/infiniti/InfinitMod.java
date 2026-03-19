package com.infiniti;

import com.infiniti.blocks.ModBlocks;
import com.infiniti.dimension.InfinitDimension;
import com.infiniti.entity.ModEntities;
import com.infiniti.items.ModItems;
import com.infiniti.crafting.SandforgeRecipeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * InfinitMod — Dimensão Infiniti
 *
 * Uma única dimensão de deserto vasto, inspirada em Duna.
 * Possui minérios exclusivos, criaturas únicas e mecânicas
 * de forja de areia (Sandforge).
 */
@Mod(InfinitMod.MOD_ID)
public class InfinitMod {

    public static final String MOD_ID = "infiniti";

    public InfinitMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registro de todos os conteúdos
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);

        // Receitas e eventos
        MinecraftForge.EVENT_BUS.register(new SandforgeRecipeHandler());
        MinecraftForge.EVENT_BUS.register(new InfinitDimension());
    }
}
