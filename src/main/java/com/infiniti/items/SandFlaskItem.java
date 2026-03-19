package com.infiniti.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * SandFlaskItem — frasco de "água de areia" da dimensão.
 *
 * Funciona como a água normal, mas destilada de minerais da dimensão.
 * Não existe água líquida na Dimensão Infiniti — a SandFlask é o único
 * método de hidratação além do StillsuitChestplate.
 *
 * EFEITOS ao beber:
 *   - Restaura 4 pontos de fome
 *   - Remove o efeito SandThirst (se ativo)
 *   - Regeneration I por 5s
 *
 * RECARREGAR:
 *   Colocar SandFlask vazia + SpiceSoil na Sandforge → SandFlask cheia
 *   (A especiaria destila a umidade do solo)
 *
 * Durabilidade: 64 usos por frasco.
 */
public class SandFlaskItem extends Item {

    public SandFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            // Restaurar fome/hidratação
            player.getFoodData().eat(4, 0.8f);

            // Regeneração breve
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));

            // Remove sede
            player.removeEffect(/* SandThirstEffect */ MobEffects.WEAKNESS);

            player.sendSystemMessage(
                    Component.translatable("infiniti.sand_flask.drank"));

            // Consome durabilidade
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }
}
