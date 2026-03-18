package com.infinitedimensions.events;

import com.infinitedimensions.InfiniteDimensions;
import com.infinitedimensions.dimension.DimensionParams;
import com.infinitedimensions.dimension.DimensionRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.infinitedimensions.InfiniteDimensions.MOD_ID;

/**
 * DimensionEffectTicker — reaplicar efeitos de status a cada 5 minutos
 * para que eles não expirem enquanto o jogador permanece na dimensão.
 *
 * Também aplica gravidade customizada via modificação de velocidade.
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DimensionEffectTicker {

    // 5 minutos = 6000 ticks; reaplicamos com 12000 ticks de duração → margem de 7min
    private static final int REAPPLY_INTERVAL = 6000;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer sp)) return;

        // Só em dimensões do mod
        ResourceKey<Level> dim = sp.level().dimension();
        if (!dim.location().getNamespace().equals(MOD_ID)) return;

        // Reaplicar a cada REAPPLY_INTERVAL ticks
        if (sp.tickCount % REAPPLY_INTERVAL != 0) return;

        String dimId = dim.location().getPath();
        DimensionParams params = DimensionRegistry.getParams(dimId);
        if (params == null) return;

        // Reaplicar efeitos
        for (MobEffect effect : params.playerEffects) {
            int amplifier = resolveAmplifier(params);
            sp.addEffect(new MobEffectInstance(effect, 12000, amplifier, false, true, true));
        }
    }

    private static int resolveAmplifier(DimensionParams params) {
        String dominant = params.sourceGenes.dominantGeneName();
        int val = params.sourceGenes.get(dominant);
        if (val >= 5) return 2;
        if (val >= 3) return 1;
        return 0;
    }
}
