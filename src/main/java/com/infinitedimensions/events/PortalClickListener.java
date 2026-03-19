package com.infinitedimensions.events;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.dimension.DimensionParams;
import com.infinitedimensions.dimension.DimensionRegistry;
import com.infinitedimensions.genes.GeneResolver;
import com.infinitedimensions.util.NameGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * PortalClickListener (Simplificado) — ativa portais com qualquer item
 * Quando o jogador clica em um portal do Nether com qualquer item na mão,
 * uma nova dimensão é criada e o jogador é teleportado.
 */
@Mod.EventBusSubscriber(modid = InfiniteDimensions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PortalClickListener_Simple {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        // Apenas no servidor
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Verifica se é um portal do Nether
        if (level.getBlockState(pos).getBlock() != Blocks.NETHER_PORTAL) {
            return;
        }

        // Verifica se o jogador tem algo na mão
        if (player.getMainHandItem().isEmpty()) {
            return;
        }

        // Ativa o portal
        activatePortal(serverLevel, pos, player);
        event.setCanceled(true);
    }

    private static void activatePortal(ServerLevel currentLevel, BlockPos portalPos, Player player) {
        try {
            // Gerar parâmetros da dimensão
            long seed = System.currentTimeMillis();
            String dimensionName = NameGenerator.generate();
            int chaosLevel = (int) (Math.random() * 5);
            int stableLevel = (int) (Math.random() * 5);

            DimensionParams params = new DimensionParams(
                "dim_" + Long.toHexString(seed),
                dimensionName,
                seed,
                GeneResolver.resolveGenes(chaosLevel, stableLevel)
            );

            InfiniteDimensions.LOGGER.info("╔══════════════════════════════════════╗");
            InfiniteDimensions.LOGGER.info("║ ✨ CRIAR NOVA DIMENSÃO ✨            ║");
            InfiniteDimensions.LOGGER.info("╠══════════════════════════════════════╣");
            InfiniteDimensions.LOGGER.info("║ Nome: {}", params.name);
            InfiniteDimensions.LOGGER.info("║ ID: {}", params.dimensionId);
            InfiniteDimensions.LOGGER.info("║ Seed: {}", params.seed);
            InfiniteDimensions.LOGGER.info("║ Jogador: {}", player.getName().getString());
            InfiniteDimensions.LOGGER.info("╚══════════════════════════════════════╝");

            // Obter ou criar a dimensão
            var dimensionKey = DimensionRegistry.getOrCreateDimension(currentLevel.getServer(), params);

            // Obter o nível da dimensão
            ServerLevel targetLevel = currentLevel.getServer().getLevel(dimensionKey);
            if (targetLevel == null) {
                player.displayClientMessage(
                    Component.literal("§c✖ Erro ao carregar dimensão"),
                    false
                );
                InfiniteDimensions.LOGGER.error("✗ Não foi possível obter o nível para: {}", params.name);
                return;
            }

            // Som de ativação
            currentLevel.playSound(null, portalPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 1.0F, 1.0F);

            // Efeitos visuais
            spawnActivationEffects(currentLevel, portalPos);

            // Calcular posição de spawn
            BlockPos spawnPos = targetLevel.getWorldSpawnPos();
            double spawnX = spawnPos.getX() + 0.5;
            double spawnY = Math.max(spawnPos.getY(), 64); // Mínimo Y=64
            double spawnZ = spawnPos.getZ() + 0.5;

            // Teletransportar jogador
            player.teleportTo(targetLevel, spawnX, spawnY, spawnZ, player.getYRot(), player.getXRot());

            // Efeitos de chegada
            spawnArrivalEffects(targetLevel, spawnPos);

            // Som de chegada
            targetLevel.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);

            // Mensagens
            player.displayClientMessage(
                Component.literal("§6═════════════════════════════════════════"),
                false
            );
            player.displayClientMessage(
                Component.literal("§d✨ BEM-VINDO A " + dimensionName.toUpperCase() + "! ✨"),
                false
            );
            player.displayClientMessage(
                Component.literal("§fEsta é uma dimensão completamente nova gerada aleatoriamente"),
                false
            );
            player.displayClientMessage(
                Component.literal("§7Seed: §e" + seed),
                false
            );
            player.displayClientMessage(
                Component.literal("§6═════════════════════════════════════════"),
                false
            );

            InfiniteDimensions.LOGGER.info("✓ Jogador {} teleportado com sucesso para {}", 
                player.getName().getString(), params.name);

        } catch (Exception e) {
            InfiniteDimensions.LOGGER.error("✗ Erro ao ativar portal: {}", e.getMessage());
            e.printStackTrace();
            player.displayClientMessage(
                Component.literal("§c✖ Erro ao carregar dimensão!"),
                false
            );
        }
    }

    private static void spawnActivationEffects(ServerLevel level, BlockPos portalPos) {
        // Partículas mágicas de ativação ao redor do portal
        for (int i = 0; i < 20; i++) {
            double x = portalPos.getX() + 0.5 + (Math.random() - 0.5) * 1.5;
            double y = portalPos.getY() + 0.5 + (Math.random() - 0.5) * 1.5;
            double z = portalPos.getZ() + 0.5 + (Math.random() - 0.5) * 1.5;

            double vx = (Math.random() - 0.5) * 0.4;
            double vy = Math.random() * 0.3;
            double vz = (Math.random() - 0.5) * 0.4;

            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                x, y, z,
                1, vx, vy, vz,
                0.5
            );
        }
    }

    private static void spawnArrivalEffects(ServerLevel level, BlockPos spawnPos) {
        // Partículas de chegada
        for (int i = 0; i < 25; i++) {
            double x = spawnPos.getX() + 0.5 + (Math.random() - 0.5) * 2;
            double y = spawnPos.getY() + 1.0 + (Math.random() - 0.5) * 2;
            double z = spawnPos.getZ() + 0.5 + (Math.random() - 0.5) * 2;

            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y, z,
                1, 0, 0.1, 0,
                0.5
            );
        }
    }
}
