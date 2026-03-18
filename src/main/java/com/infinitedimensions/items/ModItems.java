package com.infinitedimensions.items;

import com.infinitedimensions.InfiniteDimensions;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InfiniteDimensions.MOD_ID);

    // O item principal do mod — craftado com qualquer combinação
    public static final RegistryObject<Item> COMBINATION_ORB =
            ITEMS.register("combination_orb", CombinationOrb::new);
}
