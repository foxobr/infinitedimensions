package com.infiniti.dimension;

import com.infiniti.InfinitMod;
import com.infiniti.worldgen.InfiniteDesertChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * InfinitDimension — gerencia a única dimensão do mod: Dimensão Infiniti.
 *
 * IDENTIFICADOR DA DIMENSÃO:
 *   infiniti:infiniti_dimension
 *
 * CARACTERÍSTICAS:
 *   - Tema: Deserto Infinito estilo Duna
 *   - Sem chuva, sem neve, sem água (dimensão árida)
 *   - Céu laranja-avermelhado com névoa de areia
 *   - Sol enorme — "planeta distante do sol vermelho"
 *   - Noite: céu roxo profundo, estrelas grandes
 *   - Gravidade normal (sem alteração)
 *   - Sem void abaixo de Y=-64 (bedrock normal)
 *
 * EVENTOS CLIMÁTICOS (periódicos):
 *   Tempestade de Areia (Sandstorm):
 *     - Ocorre a cada 30-60 minutos (aleatório)
 *     - Dura 3-8 minutos
 *     - Efeitos: Cegueira leve + Lentidão para quem não usa capacete Stillsuit
 *     - Spawna SandShades durante a tempestade
 *     - Cobre a tela de partículas de areia (lado client)
 *
 * SPAWN DA DIMENSÃO:
 *   Jogador aparece em Y=120, no topo de uma duna elevada.
 *   Estrutura de Ruínas gerada no spawn (boas-vindas ao deserto).
 *
 * RETORNO:
 *   Usar InfitiPortalKey novamente — ou morrer (sem punição extra).
 *   Coordenadas de retorno salvas no jogador ao entrar.
 */
@Mod.EventBusSubscriber(modid = InfinitMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfinitDimension {

    public static final ResourceKey<Level> DIMENSION_KEY = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(InfinitMod.MOD_ID, "infiniti_dimension")
    );

    // ── Salva posição de retorno ao entrar na dimensão ──
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (event.getTo().equals(DIMENSION_KEY)) {
            // Entrando na Dimensão Infiniti — salva coords de retorno
            player.getPersistentData().putDouble("infiniti.return_x", player.getX());
            player.getPersistentData().putDouble("infiniti.return_y", player.getY());
            player.getPersistentData().putDouble("infiniti.return_z", player.getZ());

            // Mensagem de boas-vindas
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("infiniti.dimension.welcome"));
        }

        if (event.getFrom().equals(DIMENSION_KEY)) {
            // Saindo da dimensão — remove efeitos de dependência temporária
            player.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
        }
    }

    // ── Cria o ServerLevel da dimensão (chamado no startup do servidor) ──
    public static ServerLevel getOrCreateDimension(MinecraftServer server) {
        ServerLevel existing = server.getLevel(DIMENSION_KEY);
        if (existing != null) return existing;

        // DynamicLevelHelper cria o level programaticamente
        return DynamicLevelHelper.createLevel(server, DIMENSION_KEY);
    }

    // ── Teleporte para a dimensão ──
    public static void teleportToDimension(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel dim = getOrCreateDimension(server);

        // Encontra posição segura no topo de uma duna
        BlockPos spawnPos = findSafeSpawn(dim);

        player.teleportTo(dim,
                spawnPos.getX() + 0.5,
                spawnPos.getY() + 1,
                spawnPos.getZ() + 0.5,
                player.getYRot(), 0);
    }

    private static BlockPos findSafeSpawn(ServerLevel dim) {
        // Busca de baixo para cima num raio pequeno do spawn
        BlockPos spawn = dim.getSharedSpawnPos();
        for (int y = 200; y > 60; y--) {
            BlockPos pos = new BlockPos(spawn.getX(), y, spawn.getZ());
            if (!dim.isEmptyBlock(pos) && dim.isEmptyBlock(pos.above())) {
                return pos;
            }
        }
        return new BlockPos(spawn.getX(), 100, spawn.getZ());
    }
}
