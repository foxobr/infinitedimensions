package com.infinitedimensions.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * CombinationOrb — item central do mod.
 *
 * Armazena no NBT:
 *   - "ingredients": lista de IDs dos itens usados no craft
 *   - "custom_names": lista de strings (nomes de pessoas) adicionados via bigorna
 *   - "dimension_seed": seed gerada (preenchida após primeiro uso)
 *   - "dimension_name": nome gerado para a dimensão
 */
public class CombinationOrb extends Item {

    public static final String NBT_INGREDIENTS   = "ingredients";
    public static final String NBT_CUSTOM_NAMES  = "custom_names";
    public static final String NBT_SEED          = "dimension_seed";
    public static final String NBT_DIM_NAME      = "dimension_name";

    public CombinationOrb() {
        super(new Item.Properties().stacksTo(1));
    }

    // ---------- Helpers NBT ----------

    public static void addIngredient(ItemStack orb, String itemId) {
        CompoundTag tag = orb.getOrCreateTag();
        ListTag list = tag.contains(NBT_INGREDIENTS)
                ? tag.getList(NBT_INGREDIENTS, 8)
                : new ListTag();
        list.add(StringTag.valueOf(itemId));
        tag.put(NBT_INGREDIENTS, list);
    }

    public static void addCustomName(ItemStack orb, String name) {
        CompoundTag tag = orb.getOrCreateTag();
        ListTag list = tag.contains(NBT_CUSTOM_NAMES)
                ? tag.getList(NBT_CUSTOM_NAMES, 8)
                : new ListTag();
        list.add(StringTag.valueOf(name.toLowerCase().trim()));
        tag.put(NBT_CUSTOM_NAMES, list);
    }

    public static List<String> getIngredients(ItemStack orb) {
        CompoundTag tag = orb.getOrCreateTag();
        if (!tag.contains(NBT_INGREDIENTS)) return List.of();
        ListTag list = tag.getList(NBT_INGREDIENTS, 8);
        return list.stream().map(t -> t.getAsString()).toList();
    }

    public static List<String> getCustomNames(ItemStack orb) {
        CompoundTag tag = orb.getOrCreateTag();
        if (!tag.contains(NBT_CUSTOM_NAMES)) return List.of();
        ListTag list = tag.getList(NBT_CUSTOM_NAMES, 8);
        return list.stream().map(t -> t.getAsString()).toList();
    }

    public static long getSeed(ItemStack orb) {
        return orb.getOrCreateTag().getLong(NBT_SEED);
    }

    public static void setSeed(ItemStack orb, long seed) {
        orb.getOrCreateTag().putLong(NBT_SEED, seed);
    }

    public static String getDimensionName(ItemStack orb) {
        return orb.getOrCreateTag().getString(NBT_DIM_NAME);
    }

    public static void setDimensionName(ItemStack orb, String name) {
        orb.getOrCreateTag().putString(NBT_DIM_NAME, name);
    }

    // ---------- Tooltip ----------

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        List<String> ingredients = getIngredients(stack);
        List<String> names       = getCustomNames(stack);
        String dimName           = getDimensionName(stack);

        if (!dimName.isEmpty()) {
            tooltip.add(Component.literal("§6Dimensão: §f" + dimName));
        }
        if (!ingredients.isEmpty()) {
            tooltip.add(Component.literal("§7Ingredientes: §f" + ingredients.size()));
        }
        if (!names.isEmpty()) {
            tooltip.add(Component.literal("§7Nomes: §f" + String.join(", ", names)));
        }
        if (ingredients.isEmpty() && names.isEmpty()) {
            tooltip.add(Component.literal("§8Sem combinação definida"));
        }
    }
}
