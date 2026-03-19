package com.infiniti.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * SpicePillItem — pílula de Especiaria concentrada.
 *
 * EFEITOS ao consumir (duração: 60 segundos):
 *   - Night Vision II       (visão noturna aprimorada)
 *   - Speed I               (reflexos aguçados)
 *   - Hero of the Village   (reutilizado como "percepção social" — NPCs amigáveis)
 *
 * EFEITO COLATERAL (10% de chance):
 *   - Nausea por 5s         (abstinência se consumir múltiplas vezes seguidas)
 *
 * MECÂNICA DE DEPENDÊNCIA:
 *   Se o jogador consumir 3+ pílulas em menos de 5 minutos,
 *   ganha tag "spice_addicted" que causa fraqueza fora da dimensão.
 */
public class SpicePillItem extends Item {

    public SpicePillItem(Properties properties) {
        super(properties.food(
                new net.minecraft.world.food.FoodProperties.Builder()
                        .nutrition(0)
                        .saturationMod(0.0f)
                        .alwaysEat()
                        .fast()
                        .build()
        ));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {

            // Efeitos principais
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,    1200, 1)); // 60s nv2
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,  1200, 0)); // 60s speed1

            // Verifica dependência
            long lastDose = player.getPersistentData().getLong("infiniti.spice_last_dose");
            int doseCount = player.getPersistentData().getInt("infiniti.spice_dose_count");
            long now = level.getGameTime();

            if (now - lastDose < 6000) { // menos de 5 min
                doseCount++;
                player.getPersistentData().putInt("infiniti.spice_dose_count", doseCount);
            } else {
                doseCount = 1;
                player.getPersistentData().putInt("infiniti.spice_dose_count", 1);
            }
            player.getPersistentData().putLong("infiniti.spice_last_dose", now);

            // Overdose: 3+ doses
            if (doseCount >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0)); // náusea 5s
                player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("infiniti.spice.overdose"));
            }

            // Efeito de dependência permanente (fora da dim)
            if (doseCount >= 5) {
                player.getPersistentData().putBoolean("infiniti.spice_addicted", true);
            }

            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16; // rápido, como uma pílula
    }
}
