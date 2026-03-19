package com.infiniti.items;

import com.infiniti.dimension.InfinitDimension;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * InfitiPortalKeyItem — chave que abre o portal para a Dimensão Infiniti.
 *
 * COMO USAR:
 *   Construir um frame de arenito no formato:
 *     [SandstoneBlock] [SandstoneBlock] [SandstoneBlock]
 *     [SandstoneBlock] [AR]             [SandstoneBlock]
 *     [SandstoneBlock] [AR]             [SandstoneBlock]
 *     [SandstoneBlock] [SandstoneBlock] [SandstoneBlock]
 *
 *   Clicar com direito no centro do frame com a InfitiPortalKey.
 *   O portal se acende com partículas de areia dourada.
 *
 * CRAFTING (Sandforge):
 *   [SunstoneWafer]   [SpiceEssence]    [SunstoneWafer]
 *   [SpiceEssence]    [DuneIngot]       [SpiceEssence]
 *   [SunstoneWafer]   [SpiceEssence]    [SunstoneWafer]
 *   → InfitiPortalKey x1 (RARE)
 *
 * RETORNO:
 *   Usar a chave novamente DENTRO da Dimensão Infiniti traz o jogador de volta.
 *   Morrer na dimensão também retorna ao Overworld (sem punição extra).
 */
public class InfitiPortalKeyItem extends Item {

    public static final ResourceKey<Level> INFINITI_DIM_KEY = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation("infiniti", "infiniti_dimension")
    );

    public InfitiPortalKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));

        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.fail(player.getItemInHand(hand));

        boolean inInfiniti = level.dimension().equals(INFINITI_DIM_KEY);

        if (!inInfiniti) {
            // Teleporta para a Dimensão Infiniti
            ServerLevel target = sp.getServer().getLevel(INFINITI_DIM_KEY);
            if (target != null) {
                sp.teleportTo(target,
                        sp.getX(), 120, sp.getZ(), // spawn alto nas dunas
                        sp.getYRot(), sp.getXRot());
                sp.sendSystemMessage(Component.translatable("infiniti.portal.entering"));
            } else {
                sp.sendSystemMessage(Component.translatable("infiniti.portal.not_loaded"));
            }
        } else {
            // Retorno ao Overworld
            ServerLevel overworld = sp.getServer().overworld();
            sp.teleportTo(overworld,
                    sp.getPersistentData().getDouble("infiniti.return_x"),
                    sp.getPersistentData().getDouble("infiniti.return_y"),
                    sp.getPersistentData().getDouble("infiniti.return_z"),
                    sp.getYRot(), sp.getXRot());
            sp.sendSystemMessage(Component.translatable("infiniti.portal.returning"));
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
