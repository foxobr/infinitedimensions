package com.infiniti;

import com.infiniti.block.ModBlocks;
import com.infiniti.item.ModItems;
import com.infiniti.entity.ModEntities;
import com.infiniti.dimension.InfinitDimension;
import com.infiniti.crafting.SandforgeRecipeHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafxmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * InfiniteMod — Classe Principal
 * 
 * Dimensão Infiniti: Uma única dimensão temática no estilo Duna.
 * Deserto eterno, criaturas únicas, minérios exclusivos, Sandforge e o Leviatã das Areias.
 */
@Mod(InfiniteMod.MOD_ID)
public class InfiniteMod {
    
    public static final String MOD_ID = "infiniti";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    public InfiniteMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Registrar blocos
        ModBlocks.BLOCKS.register(modBus);
        
        // Registrar itens
        ModItems.ITEMS.register(modBus);
        
        // Registrar entidades
        ModEntities.ENTITY_TYPES.register(modBus);
        
        // Registrar dimensão
        InfinitDimension.register(modBus);
        
        // Registrar receitas
        SandforgeRecipeHandler.SERIALIZERS.register(modBus);
        
        LOGGER.info("
╔════════════════════════════════════════════════════╗");
        LOGGER.info("║  🌕 DIMENSÃO INFINITI v1.0.0 INICIALIZADA 🌕        ║");
        LOGGER.info("║  \"Quem controla a Especiaria, controla o universo\"  ║");
        LOGGER.info("╚════════════════════════════════════════════════════╝");
    }
}
