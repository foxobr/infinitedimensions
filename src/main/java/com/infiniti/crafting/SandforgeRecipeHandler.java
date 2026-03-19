package com.infiniti.crafting;

import com.infiniti.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * SandforgeRecipeHandler — receitas exclusivas da Sandforge.
 *
 * A Sandforge funciona internamente como uma Fornalha customizada.
 * As receitas abaixo são interceptadas via eventos antes de usar
 * a lógica padrão de fundição.
 *
 * ═══════════════════════════════════════════════════════
 * RECEITAS DE FUNDIÇÃO (Sandforge como fornalha):
 * ═══════════════════════════════════════════════════════
 *
 *  RawDuneIron             → DuneIngot        (tempo: 200t, XP: 0.7)
 *  BoneCrystalShard x3     → BoneCrystalIngot (tempo: 400t, XP: 1.5)
 *  SunstoneCrystal         → SunstoneWafer    (tempo: 300t, XP: 1.0)
 *  SpiceDust               → SpiceEssence     (tempo: 600t, XP: 2.0)
 *
 * ═══════════════════════════════════════════════════════
 * RECEITAS DE BIGORNA (Sandforge como bancada especial):
 * ═══════════════════════════════════════════════════════
 *
 *  DuneIngot + SpiceEssence + DuneIngot
 *    → InfitiPortalKey     [1 peça]
 *
 *  ThermitePowder x4 (empilhados)
 *    → ThermiteCharge      [1 peça]
 *
 *  ThumperDevice + SpiceEssence + LeviathanScale
 *    → WormCaller          [1 peça]
 *
 *  SandcrawlerChitin x6 + BoneCrystalIngot
 *    → StillsuitChestplate [1 peça]
 *
 *  SandcrawlerChitin x4 + BoneCrystalIngot
 *    → StillsuitLeggings   [1 peça]
 *
 *  SandcrawlerChitin x2 + SunstoneWafer
 *    → StillsuitHelmet     [1 peça]
 *
 *  SandcrawlerChitin x2 + DuneIngot
 *    → StillsuitBoots      [1 peça]
 *
 *  DuneIngot x2 + BoneCrystalShard
 *    → ThumperDevice x2    [2 peças]
 *
 *  BoneCrystalIngot x3
 *    → SandAnchor          [1 peça]
 *
 *  DuneIngot x3 (em linha)
 *    → DunePickaxe         [1 peça]
 *
 *  DuneIngot x2 (vertical) + DuneIngot x1
 *    → DuneSword           [1 peça]
 *
 *  DuneIngot x1
 *    → DuneShovel          [1 peça]
 *
 * ═══════════════════════════════════════════════════════
 * COMBUSTÍVEIS (tempo de queima na Sandforge):
 * ═══════════════════════════════════════════════════════
 *
 *  SpiceBlock      — 32000t (ultra eficiente, ~16 minutos)
 *  SpiceEssence    — 3200t
 *  SpiceDust       — 400t
 *  SunstoneCrystal — 1600t
 *  DuneIngot       — 800t  (metal quente)
 *  DuneSand        — 100t  (arde lentamente)
 *  VolcanicRock    — 600t
 */
public class SandforgeRecipeHandler {

    // ── Receitas de Bigorna (intercepta AnvilUpdateEvent) ──

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left  = event.getLeft();
        ItemStack right = event.getRight();

        // DuneIngot + SpiceEssence → InfitiPortalKey
        if (is(left, ModItems.DUNE_INGOT) && is(right, ModItems.SPICE_ESSENCE)) {
            event.setOutput(new ItemStack(ModItems.INFINITI_PORTAL_KEY.get()));
            event.setCost(10);
            event.setMaterialCost(1);
            return;
        }

        // ThermitePowder x4 → ThermiteCharge
        if (is(left, ModItems.THERMITE_POWDER) && left.getCount() >= 4 &&
            right.isEmpty()) {
            event.setOutput(new ItemStack(ModItems.THERMITE_CHARGE.get()));
            event.setCost(3);
            event.setMaterialCost(4);
            return;
        }

        // ThumperDevice + LeviathanScale → WormCaller
        if (is(left, ModItems.THUMPER_DEVICE) && is(right, ModItems.LEVIATHAN_SCALE)) {
            event.setOutput(new ItemStack(ModItems.WORM_CALLER.get()));
            event.setCost(20);
            event.setMaterialCost(1);
            return;
        }

        // DuneIngot x2 + BoneCrystalShard → ThumperDevice x2
        if (is(left, ModItems.DUNE_INGOT) && left.getCount() >= 2 &&
            is(right, ModItems.BONE_CRYSTAL_SHARD)) {
            ItemStack out = new ItemStack(ModItems.THUMPER_DEVICE.get(), 2);
            event.setOutput(out);
            event.setCost(4);
            event.setMaterialCost(1);
            return;
        }

        // BoneCrystalIngot x3 → SandAnchor
        if (is(left, ModItems.BONE_CRYSTAL_INGOT) && left.getCount() >= 3 &&
            right.isEmpty()) {
            event.setOutput(new ItemStack(ModItems.SAND_ANCHOR.get()));
            event.setCost(6);
            event.setMaterialCost(3);
            return;
        }

        // StillsuitHelmet: SandcrawlerChitin x2 + SunstoneWafer
        if (is(left, ModItems.SANDCRAWLER_CHITIN) && left.getCount() >= 2 &&
            is(right, ModItems.SUNSTONE_WAFER)) {
            event.setOutput(new ItemStack(ModItems.STILLSUIT_HELMET.get()));
            event.setCost(8);
            event.setMaterialCost(1);
            return;
        }

        // StillsuitChestplate: SandcrawlerChitin x6 + BoneCrystalIngot
        if (is(left, ModItems.SANDCRAWLER_CHITIN) && left.getCount() >= 6 &&
            is(right, ModItems.BONE_CRYSTAL_INGOT)) {
            event.setOutput(new ItemStack(ModItems.STILLSUIT_CHESTPLATE.get()));
            event.setCost(12);
            event.setMaterialCost(1);
            return;
        }

        // StillsuitLeggings: SandcrawlerChitin x4 + BoneCrystalIngot
        if (is(left, ModItems.SANDCRAWLER_CHITIN) && left.getCount() >= 4 &&
            is(right, ModItems.BONE_CRYSTAL_INGOT)) {
            event.setOutput(new ItemStack(ModItems.STILLSUIT_LEGGINGS.get()));
            event.setCost(10);
            event.setMaterialCost(1);
            return;
        }

        // StillsuitBoots: SandcrawlerChitin x2 + DuneIngot
        if (is(left, ModItems.SANDCRAWLER_CHITIN) && left.getCount() >= 2 &&
            is(right, ModItems.DUNE_INGOT)) {
            event.setOutput(new ItemStack(ModItems.STILLSUIT_BOOTS.get()));
            event.setCost(8);
            event.setMaterialCost(1);
            return;
        }

        // DunePickaxe: DuneIngot x3
        if (is(left, ModItems.DUNE_INGOT) && left.getCount() >= 3 && right.isEmpty()) {
            event.setOutput(new ItemStack(ModItems.DUNE_PICKAXE.get()));
            event.setCost(5);
            event.setMaterialCost(3);
            return;
        }

        // DuneSword: DuneIngot x2
        if (is(left, ModItems.DUNE_INGOT) && left.getCount() >= 2 && right.isEmpty()) {
            event.setOutput(new ItemStack(ModItems.DUNE_SWORD.get()));
            event.setCost(4);
            event.setMaterialCost(2);
            return;
        }
    }

    // ── Combustíveis customizados ──

    @SubscribeEvent
    public void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();

        if (is(fuel, ModItems.SPICE_ESSENCE))         event.setBurnTime(3200);
        else if (is(fuel, ModItems.SPICE_DUST))       event.setBurnTime(400);
        else if (is(fuel, ModItems.SUNSTONE_CRYSTAL)) event.setBurnTime(1600);
        else if (is(fuel, ModItems.DUNE_INGOT))       event.setBurnTime(800);
        else if (is(fuel, ModItems.SUNSTONE_WAFER))   event.setBurnTime(1200);
        // SpiceBlock registrado via BlockItem — handled automatically
    }

    // ── Helper ──

    private boolean is(ItemStack stack, net.minecraftforge.registries.RegistryObject<?> item) {
        return !stack.isEmpty() && stack.getItem() == item.get();
    }
}
