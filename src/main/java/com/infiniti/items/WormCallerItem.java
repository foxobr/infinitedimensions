package com.infiniti.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/**
 * WormCallerItem — invoca o SandLeviathan, o verme gigante das dunas.
 *
 * COMO USAR:
 *   Segurar clique direito por 3 segundos na areia da Dimensão Infiniti.
 *   O jogador sente o chão tremer, e o SandLeviathan emerge.
 *
 * RISCOS:
 *   - SandLeviathan é extremamente perigoso (boss-like)
 *   - Pode devorar o jogador instantaneamente se sem SandAnchor
 *   - Atrai TODOS os SandCrawlers próximos também
 *
 * RECOMPENSAS:
 *   - Derrotar o SandLeviathan dropa LeviathanScale (material endgame)
 *   - Matar pelo menos 1 garante conquista "Domador das Dunas"
 *
 * CRAFTING (Sandforge — requer SpiceEssence):
 *   ThumperDevice + SpiceEssence + LeviathanScale (qualquer quantidade)
 *   → WormCaller x1
 *
 * DURABILIDADE: uso único — se destruída ao invocar, Leviathan fica berserk.
 */
public class WormCallerItem extends Item {

    private static final int USE_DURATION = 60; // 3 segundos (20 ticks/s)

    public WormCallerItem(Properties properties) {
        super(properties.durability(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (!level.dimension().location().getNamespace().equals("infiniti")) {
                player.sendSystemMessage(Component.translatable("infiniti.wormcaller.wrong_dimension"));
                return InteractionResultHolder.fail(stack);
            }

            player.sendSystemMessage(Component.translatable("infiniti.wormcaller.charging"));
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player && level instanceof ServerLevel serverLevel) {

            // Tremor do chão
            level.playSound(null, player.blockPosition(),
                    SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 3.0f, 0.3f);

            // TODO: Spawnar SandLeviathan na posição do jogador
            // SandLeviathanEntity leviathan = new SandLeviathanEntity(ModEntities.SAND_LEVIATHAN.get(), level);
            // leviathan.setPos(player.getX() + 20, player.getY(), player.getZ() + 20);
            // level.addFreshEntity(leviathan);

            player.sendSystemMessage(Component.translatable("infiniti.wormcaller.summoned"));

            // Destrói o item após uso
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.BOW;
    }
}
