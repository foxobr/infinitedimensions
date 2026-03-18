package com.infinitedimensions.items;

import com.infinitedimensions.InfiniteDimensions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfiniteDimensions.MOD_ID);

    public static final RegistryObject<CreativeModeTab> INFINITE_DIM_TAB =
        CREATIVE_TABS.register("infinite_dimensions_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.infinitedimensions"))
                .icon(() -> new ItemStack(ModItems.COMBINATION_ORB.get()))
                .displayItems((params, output) -> {
                    output.accept(ModItems.COMBINATION_ORB.get());
                })
                .build()
        );
}
