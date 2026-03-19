package com.infiniti.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * StillsuitArmorItem — traje de sobrevivência do deserto.
 *
 * BÔNUS POR PEÇA:
 *   Capacete:
 *     - Visão normal em tempestades de areia (sem cegueira)
 *     - Filtro de areia: imune a partículas de SandstormBlinding
 *
 *   Peitoral:
 *     - Recupera 1 ponto de hidratação a cada 30s
 *     - Reduz consumo de água (efeito visual de sede mais lento)
 *
 *   Calças:
 *     - Passos silenciosos: SandCrawlers e SandLeviathan detectam menos
 *     - Vibração no chão reduzida em 70%
 *
 *   Botas:
 *     - Caminha sobre DuneSand sem afundar (velocidade normal)
 *     - Não afunda em QuickSand (bloco especial da dimensão)
 *
 * BÔNUS DE SET COMPLETO (4 peças):
 *   - Regeneration I passivo dentro da Dimensão Infiniti
 *   - SandLeviathan demora 2x mais para detectar o jogador
 *   - Mensagem especial ao entrar na dimensão pela primeira vez
 *
 * MATERIAL: StillsuitMaterial (tier entre ferro e diamante)
 */
public class StillsuitArmorItem extends ArmorItem {

    public enum Type {
        HELMET(ArmorItem.Type.HELMET),
        CHESTPLATE(ArmorItem.Type.CHESTPLATE),
        LEGGINGS(ArmorItem.Type.LEGGINGS),
        BOOTS(ArmorItem.Type.BOOTS);

        final ArmorItem.Type vanillaType;
        Type(ArmorItem.Type t) { this.vanillaType = t; }
    }

    private static final ArmorMaterial STILLSUIT_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 275;
                case CHESTPLATE -> 400;
                case LEGGINGS -> 375;
                case BOOTS -> 325;
            };
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 3;
                case CHESTPLATE -> 6;
                case LEGGINGS -> 5;
                case BOOTS -> 3;
            };
        }
        @Override public int getEnchantmentValue() { return 12; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() {
            return net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER;
        }
        @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
            return net.minecraft.world.item.crafting.Ingredient.of(
                    ModItems.DUNE_INGOT.get());
        }
        @Override public String getName() { return "infiniti:stillsuit"; }
        @Override public float getToughness() { return 1.5f; }
        @Override public float getKnockbackResistance() { return 0.0f; }
    };

    private final Type armorType;

    public StillsuitArmorItem(Type type, Properties properties) {
        super(STILLSUIT_MATERIAL, type.vanillaType, properties);
        this.armorType = type;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) return;

        boolean inInfiniti = level.dimension().location().getNamespace().equals("infiniti");

        // Bônus do peitoral: regeneração de hidratação
        if (armorType == Type.CHESTPLATE) {
            if (level.getGameTime() % 600 == 0) { // a cada 30s
                if (player.getFoodData().getFoodLevel() < 20) {
                    player.getFoodData().eat(1, 0.5f);
                }
            }
        }

        // Bônus de set completo
        if (inInfiniti && hasFullSet(player)) {
            if (level.getGameTime() % 200 == 0) { // a cada 10s
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION, 220, 0, false, false));
            }
        }
    }

    private boolean hasFullSet(Player player) {
        return player.getInventory().armor.stream()
                .allMatch(s -> s.getItem() instanceof StillsuitArmorItem);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level,
                                 List<net.minecraft.network.chat.Component> tooltip,
                                 TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component
                .translatable("infiniti.stillsuit." + armorType.name().toLowerCase() + ".tooltip")
                .withStyle(net.minecraft.ChatFormatting.GOLD));
    }
}
