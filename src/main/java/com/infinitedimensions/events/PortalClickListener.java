package com.infinitedimensions.events;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.dimension.DimensionParams;
import com.infinitedimensions.dimension.DimensionRegistry;
import com.infinitedimensions.genes.GeneResolver;
import com.infinitedimensions.items.CombinationOrb;
import com.infinitedimensions.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * PortalClickListener — escuta cliques em portais do Nether com o CombinationOrb
 * (funciona como um isqueiro para acender portais personalizados)
 */
@Mod.EventBusSubscriber(modid = InfiniteDimensions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PortalClickListener {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        ItemStack heldItem = player.getMainHandItem();

        // Verifica se o bloco é um portal do Nether e se está segurando a orbe
        if (!serverLevel.getBlockState(pos).is(Blocks.NETHER_PORTAL)) return;
        if (!heldItem.is(ModItems.COMBINATION_ORB.get())) return;

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        activatePortal(serverPlayer, serverLevel, pos, heldItem);
    }

    private static void activatePortal(ServerPlayer player, ServerLevel level, BlockPos portalPos, ItemStack orb) {
        List<String> ingredients = CombinationOrb.getIngredients(orb);
        List<String> names       = CombinationOrb.getCustomNames(orb);

        InfiniteDimensions.LOGGER.info("╔════════════════════════════════════════╗");
        InfiniteDimensions.LOGGER.info("║ 🔥 PORTAL ATIVADO COM ORBE! 🔥          ║");
        InfiniteDimensions.LOGGER.info("╠════════════════════════════════════════╣");
        InfiniteDimensions.LOGGER.info("║ Jogador: {}", player.getName().getString());
        InfiniteDimensions.LOGGER.info("║ Localização: X={}, Y={}, Z={}", portalPos.getX(), portalPos.getY(), portalPos.getZ());
        InfiniteDimensions.LOGGER.info("║ Ingredientes: {}", ingredients.size() > 0 ? ingredients : "NENHUM");
        InfiniteDimensions.LOGGER.info("║ Nomes: {}", names.size() > 0 ? names : "NENHUM");
        InfiniteDimensions.LOGGER.info("╚════════════════════════════════════════╝");

        if (ingredients.isEmpty() && names.isEmpty()) {
            player.displayClientMessage(
                Component.literal("§c✗ Orbe vazio! Combine itens para criar uma dimensão."),
                true
            );
            return;
        }

        // Resolve parâmetros da dimensão
        DimensionParams params = GeneResolver.resolve(ingredients, names);
        InfiniteDimensions.LOGGER.info("✓ Dimensão gerada: {} (seed: {})", params.name, params.seed);

        // Obtém (ou cria) a dimensão
        MinecraftServer server = level.getServer();
        ResourceKey<Level> dimKey = DimensionRegistry.getOrCreateDimension(server, params);
        ServerLevel targetLevel   = server.getLevel(dimKey);

        if (targetLevel == null) {
            InfiniteDimensions.LOGGER.error("✗ ERRO: Não foi possível carregar a dimensão {}",
                dimKey.location());
            player.displayClientMessage(
                Component.literal("§c✗ Erro ao carregar dimensão!"),
                true
            );
            return;
        }

        // ── Efeitos visuais intensos ──
        spawnPortalActivationEffects(level, portalPos);

        // Consome 1 item da orbe
        orb.shrink(1);
        InfiniteDimensions.LOGGER.info("✓ Orbe consumida (restam: {})", orb.getCount());

        // Teleporta o jogador
        BlockPos spawnPos = findSafeSpawn(targetLevel);
        
        player.teleportTo(
            targetLevel,
            spawnPos.getX() + 0.5,
            spawnPos.getY() + 1.0,
            spawnPos.getZ() + 0.5,
            player.getYRot(),
            player.getXRot()
        );

        // Efeito de chegada
        for (int i = 0; i < 25; i++) {
            double x = spawnPos.getX() + 0.5 + (Math.random() - 0.5) * 2;
            double y = spawnPos.getY() + 1.0 + (Math.random() - 0.5) * 2;
            double z = spawnPos.getZ() + 0.5 + (Math.random() - 0.5) * 2;
            targetLevel.sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y, z, 1, 0, 0.1, 0, 0.5
            );
        }

        // Som de chegada
        targetLevel.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
            net.minecraft.sounds.SoundSource.PLAYERS,
            1.0f, 1.2f
        );

        // Notifica com mensagens
        player.displayClientMessage(
            Component.literal("§6═══════════════════════════════════════"),
            false
        );
        player.displayClientMessage(
            Component.literal("§d✨ BEM-VINDO A " + params.name.toUpperCase() + " ✨"),
            false
        );
        player.displayClientMessage(
            Component.literal("§fSeed: §e" + params.seed),
            false
        );
        player.displayClientMessage(
            Component.literal("§7Genes - Caos§f:§c" + params.sourceGenes.get("chaos") + 
                            " §fNatureza§f:§2" + params.sourceGenes.get("nature") +
                            " §fFrio§f:§3" + params.sourceGenes.get("cold")),
            false
        );
        player.displayClientMessage(
            Component.literal("§6═══════════════════════════════════════"),
            false
        );

        // Armazena informações de retorno
        player.getPersistentData().putLong("id_return_pos", portalPos.asLong());
        player.getPersistentData().putString("id_return_dim", "minecraft:overworld");

        InfiniteDimensions.LOGGER.info("✓✓✓ {} TELEPORTADO PARA '{}' (seed={}) ✓✓✓",
            player.getName().getString(), params.name, params.seed);
    }

    private static void spawnPortalActivationEffects(ServerLevel level, BlockPos portalPos) {
        // Muitas partículas de portal
        for (int i = 0; i < 80; i++) {
            double x = portalPos.getX() + 0.5 + (Math.random() - 0.5) * 4;
            double y = portalPos.getY() + 0.5 + (Math.random() - 0.5) * 4;
            double z = portalPos.getZ() + 0.5 + (Math.random() - 0.5) * 4;
            
            double vx = (Math.random() - 0.5) * 0.8;
            double vy = (Math.random() - 0.5) * 0.8 + 0.2;
            double vz = (Math.random() - 0.5) * 0.8;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                x, y, z,
                1, vx, vy, vz,
                1.0
            );
        }

        // Partículas de amethyst (roxa)
        for (int i = 0; i < 20; i++) {
            double x = portalPos.getX() + 0.5 + (Math.random() - 0.5) * 2;
            double y = portalPos.getY() + 0.5 + (Math.random() - 0.5) * 2;
            double z = portalPos.getZ() + 0.5 + (Math.random() - 0.5) * 2;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.AMETHYST,
                x, y, z,
                1, 0, 0.1, 0, 0.3
            );
        }

        // Sons de ativação
        level.playSound(
            null,
            portalPos.getX() + 0.5,
            portalPos.getY() + 0.5,
            portalPos.getZ() + 0.5,
            net.minecraft.sounds.SoundEvents.PORTAL_TRIGGER,
            net.minecraft.sounds.SoundSource.BLOCKS,
            2.0f, 0.8f
        );

        level.playSound(
            null,
            portalPos.getX() + 0.5,
            portalPos.getY() + 0.5,
            portalPos.getZ() + 0.5,
            net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE,
            net.minecraft.sounds.SoundSource.BLOCKS,
            1.5f, 1.2f
        );
    }

    private static BlockPos findSafeSpawn(ServerLevel level) {
        BlockPos spawnPos = level.getSharedSpawnPos();
        if (spawnPos == null) spawnPos = new BlockPos(0, 64, 0);
        
        // Encontra o terreno a partir da altura
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(spawnPos.getX(), y, spawnPos.getZ());
            if (!level.getBlockState(checkPos).getMaterial().isReplaceable()) {
                return checkPos.above(2);
            }
        }
        
        return new BlockPos(spawnPos.getX(), 64, spawnPos.getZ());
    }
}
