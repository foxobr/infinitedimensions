package com.infiniti.dimension;

import com.infiniti.items.ModItems;
import com.infiniti.items.StillsuitArmorItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.infiniti.InfinitMod;

/**
 * DimensionEffectTicker — gerencia efeitos passivos da Dimensão Infiniti.
 *
 * ═══════════════════════════════════════════════════════
 * MECÂNICA DE SEDE (SandThirst)
 * ═══════════════════════════════════════════════════════
 *   A cada 10 segundos dentro da dimensão, o jogador perde
 *   1 ponto de saturação (além do desgaste normal).
 *
 *   Se a saturação chegar a 0 E fome <= 6:
 *     → Aplica Weakness I + Mining Fatigue I (efeito de desidratação)
 *     → Mensagem: "Você sente sede intensa..."
 *
 *   Cura: beber SandFlask remove os efeitos e restaura saturação.
 *   Prevenção: StillsuitChestplate reduz perda de saturação em 50%.
 *
 * ═══════════════════════════════════════════════════════
 * MECÂNICA DE CALOR (HeatDamage)
 * ═══════════════════════════════════════════════════════
 *   A cada 30 segundos, se o jogador estiver sob o sol (sem telhado):
 *     - Sem armadura: 0.5 de dano de calor
 *     - Com Stillsuit parcial: sem dano
 *     - Em caverna/estrutura: sem dano
 *
 * ═══════════════════════════════════════════════════════
 * EVENTO TEMPESTADE DE AREIA (Sandstorm)
 * ═══════════════════════════════════════════════════════
 *   Ocorre aleatoriamente: chance 0.1% a cada segundo (avg. ~17min entre storms).
 *   Duração: 3-8 minutos (random).
 *
 *   Durante a tempestade:
 *     - Sem capacete Stillsuit:
 *         → Blindness + Slowness I
 *         → Mensagem: "Tempestade de areia! Procure abrigo!"
 *     - Com capacete Stillsuit:
 *         → Sem efeitos negativos
 *         → Mensagem sutil: "Filtros do Stillsuit ativados."
 *     - Spawna SandShades ao redor dos jogadores expostos
 *
 * ═══════════════════════════════════════════════════════
 * DEPENDÊNCIA DE ESPECIARIA
 * ═══════════════════════════════════════════════════════
 *   Se "infiniti.spice_addicted" == true E jogador FORA da dimensão:
 *     → Weakness I permanente
 *     → Cura: consumir SpiceEssence 5x seguidas dentro da dimensão
 *        (descondicionamento gradual)
 */
@Mod.EventBusSubscriber(modid = InfinitMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DimensionEffectTicker {

    // Estado da tempestade (por servidor)
    private static boolean sandstormActive = false;
    private static int sandstormTimer = 0;
    private static int sandstormCooldown = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Só aplica dentro da Dimensão Infiniti
        boolean inInfiniti = player.level().dimension().location()
                .getNamespace().equals("infiniti");

        if (!inInfiniti) {
            handleOutOfDimensionEffects(player);
            return;
        }

        long tick = player.level().getGameTime();

        // ── Sede (a cada 200 ticks = 10s) ──
        if (tick % 200 == 0) {
            applyThirst(player);
        }

        // ── Calor (a cada 600 ticks = 30s) ──
        if (tick % 600 == 0) {
            applyHeat(player);
        }

        // ── Tempestade de Areia (lógica global, roda no player 0) ──
        if (tick % 20 == 0) { // checar a cada segundo
            tickSandstorm(player, tick);
        }

        // ── Âncora de Areia (imobiliza jogador) ──
        if (player.getPersistentData().getBoolean("infiniti.sand_anchored")) {
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);

            // Consome durabilidade da âncora a cada 5s
            if (tick % 100 == 0) {
                var anchorSlot = findAnchorInInventory(player);
                if (anchorSlot >= 0) {
                    player.getInventory().getItem(anchorSlot)
                          .hurtAndBreak(1, player, p -> {});
                } else {
                    // Âncora quebrou/não encontrada — desativa
                    player.getPersistentData().putBoolean("infiniti.sand_anchored", false);
                }
            }
        }
    }

    private static void applyThirst(ServerPlayer player) {
        boolean hasChestplate = player.getInventory().armor.get(2)
                .getItem() instanceof StillsuitArmorItem;

        float loss = hasChestplate ? 0.5f : 1.0f;
        player.getFoodData().addExhaustion(loss * 4.0f); // exaustão = drena saturação

        if (player.getFoodData().getFoodLevel() <= 6 &&
            player.getFoodData().getSaturationLevel() <= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 250, 0, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 250, 0, false, true));
            player.sendSystemMessage(Component.translatable("infiniti.thirst.warning"));
        }
    }

    private static void applyHeat(ServerPlayer player) {
        // Só sob o sol (sem bloco sólido acima)
        if (!player.level().canSeeSky(player.blockPosition().above())) return;

        boolean hasAnyArmor = player.getInventory().armor.stream()
                .anyMatch(s -> !s.isEmpty());

        if (!hasAnyArmor) {
            player.hurt(player.level().damageSources().generic(), 0.5f);
        }
    }

    private static void tickSandstorm(ServerPlayer player, long tick) {
        if (sandstormActive) {
            sandstormTimer--;
            if (sandstormTimer <= 0) {
                // Tempestade terminou
                sandstormActive = false;
                sandstormCooldown = 20 * 60 * (5 + player.level().random.nextInt(10)); // 5-15min cooldown
                player.sendSystemMessage(Component.translatable("infiniti.sandstorm.ended"));
            } else {
                // Durante tempestade
                applyStormEffects(player);
            }
        } else {
            if (sandstormCooldown > 0) {
                sandstormCooldown -= 20;
                return;
            }
            // Chance de iniciar tempestade: 0.1% por segundo
            if (player.level().random.nextInt(1000) < 1) {
                sandstormActive = true;
                sandstormTimer = 20 * 60 * (3 + player.level().random.nextInt(6)); // 3-8 min
                player.sendSystemMessage(Component.translatable("infiniti.sandstorm.starting"));
            }
        }
    }

    private static void applyStormEffects(ServerPlayer player) {
        boolean hasHelmet = player.getInventory().armor.get(3)
                .getItem() instanceof StillsuitArmorItem;

        if (!hasHelmet) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false));
        }
    }

    private static void handleOutOfDimensionEffects(ServerPlayer player) {
        // Dependência de especiaria fora da dimensão
        if (player.getPersistentData().getBoolean("infiniti.spice_addicted")) {
            if (player.level().getGameTime() % 200 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 220, 0, false, true));
            }
        }
    }

    private static int findAnchorInInventory(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() == ModItems.SAND_ANCHOR.get()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isSandstormActive() { return sandstormActive; }
}
