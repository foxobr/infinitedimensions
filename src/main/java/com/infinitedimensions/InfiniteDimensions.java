package com.infinitedimensions;

import com.infinitedimensions.crafting.InfiniteRecipeHandler;
import com.infinitedimensions.dimension.DimensionRegistry;
import com.infinitedimensions.items.ModCreativeTabs;
import com.infinitedimensions.items.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafxmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InfiniteDimensions.MOD_ID)
public class InfiniteDimensions {

    public static final String MOD_ID = "infinitedimensions";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public InfiniteDimensions() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register items
        ModItems.ITEMS.register(modBus);

        // Register creative tab
        ModCreativeTabs.CREATIVE_TABS.register(modBus);

        // Register custom recipe serializer
        InfiniteRecipeHandler.SERIALIZERS.register(modBus);

        // Register dimension registry hooks
        DimensionRegistry.register(modBus);

        LOGGER.info("[InfiniteDimensions] Mod initialized.");
    }
}
