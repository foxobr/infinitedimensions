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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * PortalItemListener — escuta dois eventos:
 *
 * 1. ServerTickEvent: verifica se há CombinationOrbs dentro de portais do Nether
 *    e troca a dimensão do portal para a dimensão personalizada.
 *
 * 2. PlayerChangedDimensionEvent: ao entrar na dimensão personalizada,
 *    aplica os efeitos de status ao jogador.
 */
public class PortalItemListener {

    // Período de verificação em ticks (a cada 5 ticks = 0.25s) — bem mais rápido
    private static final int CHECK_INTERVAL = 5;
    private int tickCounter = 0;

    // ── Tick: verifica portais ──

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        if (server == null) return;
        
        ServerLevel overworld  = server.overworld();
        if (overworld == null) return;

        // Busca todos os ItemEntity no overworld
        List<ItemEntity> itemEntities = overworld.getEntitiesOfClass(
            ItemEntity.class,
            new AABB(
                -30_000_000, -64, -30_000_000,
                 30_000_000, 320,  30_000_000
            ),
            ie -> !ie.isRemoved() && ie.getItem().is(ModItems.COMBINATION_ORB.get())
        );

        for (ItemEntity ie : itemEntities) {
            if (isOrbInPortal(ie, overworld)) {
                handleOrbInPortal(server, overworld, ie);
            }
        }
    }

    private boolean isOrbInPortal(ItemEntity ie, ServerLevel level) {
        BlockPos pos = ie.blockPosition();
        return level.getBlockState(pos).is(Blocks.NETHER_PORTAL);
    }

    private void handleOrbInPortal(MinecraftServer server, ServerLevel overworld, ItemEntity ie) {
        ItemStack orb = ie.getItem();
        List<String> ingredients = CombinationOrb.getIngredients(orb);
        List<String> names       = CombinationOrb.getCustomNames(orb);
        BlockPos portalPos = ie.blockPosition();

        InfiniteDimensions.LOGGER.info("╔════════════════════════════════════════╗");
        InfiniteDimensions.LOGGER.info("║ ⚡ ORBE DETECTADO NO PORTAL ⚡         ║");
        InfiniteDimensions.LOGGER.info("╠════════════════════════════════════════╣");
        InfiniteDimensions.LOGGER.info("║ Localização: X={}, Y={}, Z={}", portalPos.getX(), portalPos.getY(), portalPos.getZ());
        InfiniteDimensions.LOGGER.info("║ Ingredientes: {}", ingredients.size() > 0 ? ingredients : "NENHUM");
        InfiniteDimensions.LOGGER.info("║ Nomes: {}", names.size() > 0 ? names : "NENHUM");
        InfiniteDimensions.LOGGER.info("╚════════════════════════════════════════╝");

        if (ingredients.isEmpty() && names.isEmpty()) {
            // Orbe sem combinação — deixa passar normalmente para o Nether
            InfiniteDimensions.LOGGER.info("⚠ Orbe SEM combinação - passando para Nether normal");
            return;
        }

        // Resolve parâmetros da dimensão
        DimensionParams params = GeneResolver.resolve(ingredients, names);
        InfiniteDimensions.LOGGER.info("✓ Dimensão gerada: {} (seed: {})", params.name, params.seed);

        // Obtém (ou cria) a dimensão
        ResourceKey<Level> dimKey = DimensionRegistry.getOrCreateDimension(server, params);
        ServerLevel targetLevel   = server.getLevel(dimKey);

        if (targetLevel == null) {
            InfiniteDimensions.LOGGER.error("✗ ERRO: Não foi possível carregar a dimensão {}",
                dimKey.location());
            return;
        }

        InfiniteDimensions.LOGGER.info("✓ Dimensão carregada com sucesso");

        // ── Efeitos visuais intensos ──
        InfiniteDimensions.LOGGER.info("✓ Gerando efeitos de ativação...");
        spawnPortalActivationEffects(overworld, portalPos);

        // Remove o item consumido
        ie.discard();
        InfiniteDimensions.LOGGER.info("✓ Orbe consumido");

        // Armazena seed e nome no orbe para referência (caso o jogador carregue uma cópia)
        CombinationOrb.setSeed(orb, params.seed);
        CombinationOrb.setDimensionName(orb, params.name);

        // Teleporta todos os jogadores dentro ou próximos do portal
        List<ServerPlayer> nearbyPlayers = overworld.getEntitiesOfClass(
            ServerPlayer.class,
            new AABB(portalPos).inflate(5.0)  // Detecta até 5 blocos de distância
        );

        InfiniteDimensions.LOGGER.info("✓ Encontrados {} jogadores próximos ao portal", nearbyPlayers.size());

        if (nearbyPlayers.isEmpty()) {
            InfiniteDimensions.LOGGER.warn("⚠ Nenhum jogador foi teleportado!");
        }

        for (ServerPlayer player : nearbyPlayers) {
            InfiniteDimensions.LOGGER.info("  → Teleportando: {}", player.getName().getString());
            teleportPlayerToDimension(player, targetLevel, params, portalPos);
        }

        InfiniteDimensions.LOGGER.info("✓✓✓ DIMENSÃO '{}' (seed={}) ATIVADA COM SUCESSO ✓✓✓",
            params.name, params.seed);
    }

    private void spawnPortalActivationEffects(ServerLevel level, BlockPos portalPos) {
        // Partículas coloridas intensas (amethyst para mostrar ativação)
        for (int i = 0; i < 60; i++) {
            double x = portalPos.getX() + 0.5 + (Math.random() - 0.5) * 4;
            double y = portalPos.getY() + 0.5 + (Math.random() - 0.5) * 4;
            double z = portalPos.getZ() + 0.5 + (Math.random() - 0.5) * 4;
            
            double vx = (Math.random() - 0.5) * 0.6;
            double vy = (Math.random() - 0.5) * 0.6 + 0.1;
            double vz = (Math.random() - 0.5) * 0.6;
            
            // Partículas roxas/violeta do portal
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                x, y, z,
                1, vx, vy, vz,
                1.0
            );
        }

        // Mais algumas partículas especiais de ativação
        for (int i = 0; i < 15; i++) {
            double x = portalPos.getX() + 0.5 + (Math.random() - 0.5) * 2;
            double y = portalPos.getY() + 0.5 + (Math.random() - 0.5) * 2;
            double z = portalPos.getZ() + 0.5 + (Math.random() - 0.5) * 2;
            
            level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.AMETHYST,
                x, y, z,
                1, 0, 0.1, 0, 0.3
            );
        }

        // Som de ativação (bem alto para indicar que funcionou)
        level.playSound(
            null,
            portalPos.getX() + 0.5,
            portalPos.getY() + 0.5,
            portalPos.getZ() + 0.5,
            net.minecraft.sounds.SoundEvents.PORTAL_TRIGGER,
            net.minecraft.sounds.SoundSource.BLOCKS,
            2.0f, 0.8f
        );

        // Som adicional de "sucesso"
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

    private void teleportPlayerToDimension(ServerPlayer player, ServerLevel targetLevel,
                                            DimensionParams params, BlockPos portalPos) {
        // Ponto de spawn na dimensão: centro do chunk 0,0, acima do terreno
        BlockPos spawnPos = findSafeSpawn(targetLevel);

        ServerLevel playerLevel = (ServerLevel) player.level();

        // Efeito antes de teleportar
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        for (int i = 0; i < 20; i++) {
            double x = px + (Math.random() - 0.5) * 2;
            double y = py + (Math.random() - 0.5) * 2;
            double z = pz + (Math.random() - 0.5) * 2;
            playerLevel.sendParticles(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                x, y, z, 1, 0, 0, 0, 0.5
            );
        }

        // Teleporta o jogador
        player.teleportTo(
            targetLevel,
            spawnPos.getX() + 0.5,
            spawnPos.getY() + 1.0,
            spawnPos.getZ() + 0.5,
            player.getYRot(),
            player.getXRot()
        );

        // Efeito após teleporte na nova dimensão
        for (int i = 0; i < 25; i++) {
            double x = spawnPos.getX() + 0.5 + (Math.random() - 0.5) * 2;
            double y = spawnPos.getY() + 1.0 + (Math.random() - 0.5) * 2;
            double z = spawnPos.getZ() + 0.5 + (Math.random() - 0.5) * 2;
            targetLevel.sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y, z, 1, 0, 0, 0, 0.5
            );
        }

        // Som na partida
        playerLevel.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
            net.minecraft.sounds.SoundSource.PLAYERS,
            1.0f, 1.2f
        );

        // Notifica o jogador com mensagens bem visíveis
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
        player.displayClientMessage(
            Component.literal("§7[Digite /retornid para voltar ao Overworld]"),
            true
        );

        // Armazena a posição de retorno (overworld)
        player.getPersistentData().putLong("id_return_pos", portalPos.asLong());
        player.getPersistentData().putString("id_return_dim", "minecraft:overworld");

        InfiniteDimensions.LOGGER.info("[PortalItemListener] Teleported {} to {}",
            player.getName().getString(), params.name);
    }

    // ── Efeitos ao entrar na dimensão ──

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceKey<Level> dim = event.getTo();

        // Verifica se é uma dimensão do mod
        if (!dim.location().getNamespace().equals(InfiniteDimensions.MOD_ID)) return;

        String dimId = dim.location().getPath();
        DimensionParams params = DimensionRegistry.getParams(dimId);

        if (params == null) return;

        // Aplica efeitos de status (duração: 10 minutos = 12000 ticks)
        for (MobEffect effect : params.playerEffects) {
            int amplifier = resolveAmplifier(params, effect);
            player.addEffect(new MobEffectInstance(effect, 12000, amplifier, false, true, true));
        }

        // Mensagem de boas-vindas com informações da dimensão
        player.displayClientMessage(
            Component.literal("§8[§6" + params.name + "§8]"),
            false
        );

        if (!params.playerEffects.isEmpty()) {
            player.displayClientMessage(
                Component.literal("§7Efeitos ativos: §f" + params.playerEffects.size()),
                false
            );
        }
    }

    // ── Helpers ──

    private BlockPos findSafeSpawn(ServerLevel level) {
        // Procura um ponto seguro acima do terreno no chunk 0,0
        for (int y = 200; y > -60; y--) {
            BlockPos pos = new BlockPos(8, y, 8);
            if (!level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.above()).isAir() &&
                level.getBlockState(pos.above(2)).isAir()) {
                return pos;
            }
        }
        return new BlockPos(8, 80, 8); // fallback
    }

    private int resolveAmplifier(DimensionParams params, MobEffect effect) {
        // Genes mais fortes = amplificador maior
        String dominant = params.sourceGenes.dominantGeneName();
        int dominantValue = params.sourceGenes.get(dominant);
        if (dominantValue >= 5) return 2;
        if (dominantValue >= 3) return 1;
        return 0;
    }
}
