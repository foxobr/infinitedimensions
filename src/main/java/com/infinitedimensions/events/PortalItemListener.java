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

    // Período de verificação em ticks (a cada 10 ticks = 0.5s)
    private static final int CHECK_INTERVAL = 10;
    private int tickCounter = 0;

    // ── Tick: verifica portais ──

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        ServerLevel overworld  = server.overworld();

        // Busca todos os ItemEntity no overworld
        List<ItemEntity> itemEntities = overworld.getEntitiesOfClass(
            ItemEntity.class,
            new AABB(
                -30_000_000, -64, -30_000_000,
                 30_000_000, 320,  30_000_000
            ),
            ie -> !ie.isRemoved() && isOrbInPortal(ie, overworld)
        );

        for (ItemEntity ie : itemEntities) {
            handleOrbInPortal(server, overworld, ie);
        }
    }

    private boolean isOrbInPortal(ItemEntity ie, ServerLevel level) {
        // Verifica se o item é um CombinationOrb
        if (!ie.getItem().is(ModItems.COMBINATION_ORB.get())) return false;

        // Verifica se o bloco onde o item está é um portal do Nether
        BlockPos pos = ie.blockPosition();
        return level.getBlockState(pos).is(Blocks.NETHER_PORTAL);
    }

    private void handleOrbInPortal(MinecraftServer server, ServerLevel overworld, ItemEntity ie) {
        ItemStack orb = ie.getItem();
        List<String> ingredients = CombinationOrb.getIngredients(orb);
        List<String> names       = CombinationOrb.getCustomNames(orb);

        if (ingredients.isEmpty() && names.isEmpty()) {
            // Orbe sem combinação — deixa passar normalmente para o Nether
            return;
        }

        // Resolve parâmetros da dimensão
        DimensionParams params = GeneResolver.resolve(ingredients, names);

        // Obtém (ou cria) a dimensão
        ResourceKey<Level> dimKey = DimensionRegistry.getOrCreateDimension(server, params);
        ServerLevel targetLevel   = server.getLevel(dimKey);

        if (targetLevel == null) {
            InfiniteDimensions.LOGGER.warn("[PortalItemListener] Could not get level for {}",
                dimKey.location());
            return;
        }

        // Remove o item consumido
        ie.discard();

        // Armazena seed e nome no orbe para referência (caso o jogador carregue um cópia)
        CombinationOrb.setSeed(orb, params.seed);
        CombinationOrb.setDimensionName(orb, params.name);

        // Teleporta todos os jogadores dentro ou próximos do portal
        BlockPos portalPos = ie.blockPosition();
        List<ServerPlayer> nearbyPlayers = overworld.getEntitiesOfClass(
            ServerPlayer.class,
            new AABB(portalPos).inflate(4.0)
        );

        for (ServerPlayer player : nearbyPlayers) {
            teleportPlayerToDimension(player, targetLevel, params, portalPos);
        }

        InfiniteDimensions.LOGGER.info("[PortalItemListener] Activated dimension '{}' (seed={}) via portal at {}",
            params.name, params.seed, portalPos);
    }

    private void teleportPlayerToDimension(ServerPlayer player, ServerLevel targetLevel,
                                            DimensionParams params, BlockPos portalPos) {
        // Ponto de spawn na dimensão: centro do chunk 0,0, acima do terreno
        BlockPos spawnPos = findSafeSpawn(targetLevel);

        // Teleporta o jogador
        player.teleportTo(
            targetLevel,
            spawnPos.getX() + 0.5,
            spawnPos.getY() + 1.0,
            spawnPos.getZ() + 0.5,
            player.getYRot(),
            player.getXRot()
        );

        // Notifica o jogador
        player.displayClientMessage(
            Component.literal("§6» §fEntrando em §e" + params.name + "§f..."),
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
