package com.infinitedimensions.events;

import com.infinitedimensions.InfiniteDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infinitedimensions.InfiniteDimensions.MOD_ID;

/**
 * ReturnPortalHandler — permite ao jogador voltar ao Overworld
 * de dentro de uma dimensão do mod.
 *
 * Mecânica: Agachar + clique direito com mão vazia em qualquer
 * bloco dentro da dimensão do mod.
 *
 * A posição de retorno é guardada no PersistentData do jogador
 * pelo PortalItemListener quando ele entra na dimensão.
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReturnPortalHandler {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        // Só no servidor
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;

        // Só em dimensões do mod
        ResourceKey<Level> dim = sp.level().dimension();
        if (!dim.location().getNamespace().equals(MOD_ID)) return;

        // Só se estiver agachado e de mão vazia
        if (!sp.isShiftKeyDown()) return;
        if (!sp.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) return;

        returnToOverworld(sp);
        event.setCanceled(true);
    }

    private static void returnToOverworld(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel overworld = server.overworld();

        // Recupera posição de retorno salva
        long returnPosLong = player.getPersistentData().getLong("id_return_pos");
        BlockPos returnPos = returnPosLong != 0
            ? BlockPos.of(returnPosLong)
            : overworld.getSharedSpawnPos();

        // Garante que a posição de retorno é segura (acima do chão)
        BlockPos safePos = findSafeReturn(overworld, returnPos);

        player.teleportTo(
            overworld,
            safePos.getX() + 0.5,
            safePos.getY() + 1.0,
            safePos.getZ() + 0.5,
            player.getYRot(),
            player.getXRot()
        );

        // Remove efeitos da dimensão ao retornar
        player.removeAllEffects();

        player.displayClientMessage(
            Component.literal("§8» §fRetornando ao §aOverworld§f..."),
            true
        );

        // Limpa posição de retorno
        player.getPersistentData().remove("id_return_pos");
        player.getPersistentData().remove("id_return_dim");

        InfiniteDimensions.LOGGER.info("[ReturnPortalHandler] {} returned to overworld at {}",
            player.getName().getString(), safePos);
    }

    private static BlockPos findSafeReturn(ServerLevel level, BlockPos hint) {
        // Testa a posição original e sobe se necessário
        for (int dy = 0; dy <= 10; dy++) {
            BlockPos test = hint.above(dy);
            if (level.getBlockState(test).isAir() &&
                level.getBlockState(test.above()).isAir()) {
                return test;
            }
        }
        return hint.above(5);
    }
}
