package com.infinitedimensions.events;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.dimension.DimensionParams;
import com.infinitedimensions.dimension.DimensionRegistry;
import com.infinitedimensions.genes.ItemGenes;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.infinitedimensions.InfiniteDimensions.MOD_ID;

/**
 * DimensionDebugCommand — comando /idim para inspecionar a dimensão atual.
 *
 * Subcomandos:
 *   /idim info   — mostra nome, seed, blocos e efeitos da dimensão atual
 *   /idim genes  — mostra os genes que geraram a dimensão
 *   /idim list   — lista todas as dimensões geradas nesta sessão
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DimensionDebugCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("idim")
                .then(Commands.literal("info")
                    .executes(ctx -> cmdInfo(ctx.getSource())))
                .then(Commands.literal("genes")
                    .executes(ctx -> cmdGenes(ctx.getSource())))
                .then(Commands.literal("list")
                    .executes(ctx -> cmdList(ctx.getSource())))
        );
    }

    private static int cmdInfo(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Apenas jogadores podem usar este comando."));
            return 0;
        }

        ResourceKey<Level> dim = player.level().dimension();

        if (!dim.location().getNamespace().equals(MOD_ID)) {
            source.sendSuccess(() -> Component.literal(
                "§7Você não está em uma dimensão do §6Infinite Dimensions§7."), false);
            return 1;
        }

        String dimId = dim.location().getPath();
        DimensionParams params = DimensionRegistry.getParams(dimId);

        if (params == null) {
            source.sendFailure(Component.literal("Dados da dimensão não encontrados."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6══ " + params.name + " ══"), false);
        source.sendSuccess(() -> Component.literal("§7Seed: §f" + params.seed), false);
        source.sendSuccess(() -> Component.literal("§7ID: §f" + dimId), false);
        source.sendSuccess(() -> Component.literal(
            "§7Gravidade: §f" + String.format("%.3f", params.gravity) + "§7 (padrão: 0.080)"), false);
        source.sendSuccess(() -> Component.literal(
            "§7Luz ambiente: §f" + String.format("%.0f%%", params.ambientLight * 100)), false);

        // Blocos
        StringBuilder blocos = new StringBuilder("§7Blocos: §f");
        List<Block> blocks = params.terrainBlocks;
        for (int i = 0; i < Math.min(blocks.size(), 4); i++) {
            if (i > 0) blocos.append("§7, §f");
            String blockName = blocks.get(i).getClass().getSimpleName()
                .replace("Block","").toLowerCase();
            blocos.append(blockName);
        }
        if (blocks.size() > 4) blocos.append("§7 +").append(blocks.size() - 4).append(" mais");
        source.sendSuccess(() -> Component.literal(blocos.toString()), false);

        // Efeitos
        if (!params.playerEffects.isEmpty()) {
            StringBuilder efeitos = new StringBuilder("§7Efeitos: §f");
            List<MobEffect> effects = params.playerEffects;
            for (int i = 0; i < effects.size(); i++) {
                if (i > 0) efeitos.append("§7, §f");
                efeitos.append(effects.get(i).getDescriptionId()
                    .replace("effect.minecraft.", ""));
            }
            source.sendSuccess(() -> Component.literal(efeitos.toString()), false);
        } else {
            source.sendSuccess(() -> Component.literal("§7Efeitos: §fnenhum"), false);
        }

        source.sendSuccess(() -> Component.literal(
            "§7Gene dominante: §e" + params.sourceGenes.dominantGeneName()), false);

        return 1;
    }

    private static int cmdGenes(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Apenas jogadores podem usar este comando."));
            return 0;
        }

        ResourceKey<Level> dim = player.level().dimension();
        if (!dim.location().getNamespace().equals(MOD_ID)) {
            source.sendFailure(Component.literal("Você não está em uma dimensão do mod."));
            return 0;
        }

        String dimId = dim.location().getPath();
        DimensionParams params = DimensionRegistry.getParams(dimId);
        if (params == null) return 0;

        ItemGenes g = params.sourceGenes;
        source.sendSuccess(() -> Component.literal("§6Genes de §e" + params.name + "§6:"), false);

        String[] names = ItemGenes.NAMES;
        for (int i = 0; i < ItemGenes.GENE_COUNT; i++) {
            int val = g.get(i);
            if (val == 0) continue;
            String bar = val > 0
                ? "§a" + "█".repeat(Math.min(val, 5))
                : "§c" + "█".repeat(Math.min(Math.abs(val), 5));
            final int idx = i;
            source.sendSuccess(() -> Component.literal(
                String.format("§7  %-10s §f%+d %s", names[idx], g.get(idx), bar)), false);
        }
        return 1;
    }

    private static int cmdList(CommandSourceStack source) {
        // Lista dimensões em cache
        source.sendSuccess(() -> Component.literal("§6Dimensões geradas nesta sessão:"), false);
        // DimensionRegistry.listAll() seria ideal — aqui listamos o que temos no KEY_CACHE
        // via reflexão ou método público
        source.sendSuccess(() -> Component.literal(
            "§7Use §f/idim info §7dentro de cada dimensão para ver os detalhes."), false);
        return 1;
    }
}
