package com.infiniti.entity;

import com.infiniti.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerBossEvent;

/**
 * SandLeviathanEntity — o Verme Gigante das Dunas. Boss da Dimensão Infiniti.
 *
 * INSPIRAÇÃO: Shai-Hulud de Duna (Frank Herbert).
 *
 * MECÂNICAS DE COMBATE:
 *
 *   Fase 1 (HP > 300): "Caça"
 *     - Nada sob a areia em direção ao jogador (partículas de duna se movendo)
 *     - A cada 5s, tenta emergir e engolir jogador sem SandAnchor
 *     - Se engolir: teleporta jogador 30 blocos longe + 40 de dano
 *
 *   Fase 2 (HP 100-300): "Raiva"
 *     - Velocidade aumenta 50%
 *     - Começa a destruir blocos ao passar (HardedSand, DuneSandstone)
 *     - Chama 3-5 SandCrawlers como "filhotes"
 *
 *   Fase 3 (HP < 100): "Agonia"
 *     - Emerge completamente da areia (visível inteiro)
 *     - Barra de boss visível para todos os jogadores na dimensão
 *     - Ataques tipo slam que criam tremores (KnockBack forte)
 *     - Se morrer nessa fase: explosão de SpiceEssence (partículas)
 *
 * FRAQUEZAS:
 *   - BoneCrystalIngot weapons causam 2x dano
 *   - Atacar nos olhos (hitbox especial na cabeça) = vulnerabilidade 3x
 *   - Parar de se mover confunde o Leviathan (2s de pausa)
 *
 * DROPS:
 *   LeviathanScale: 1-3 (base) + 1/looting
 *   SpiceEssence: 2-5
 *   BoneCrystalIngot: 3-6
 *   (Raro 10%) WormCaller: 1 (o verme "doa" seu chamado ao morrer)
 *
 * HP: 500     Armadura: 15     Resistência a knockback: 100%
 */
public class SandLeviathanEntity extends Monster {

    private final ServerBossEvent bossEvent;
    private int swallowCooldown = 0;

    public SandLeviathanEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(
                Component.translatable("entity.infiniti.sand_leviathan"),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 25.0)
                .add(Attributes.ARMOR, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 80.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // Atualiza barra de boss
            bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

            // Fase de comportamento baseada em HP
            float hp = this.getHealth();

            if (hp <= 100 && hp > 0) {
                // Fase 3: Agonia — emerge e barra de boss aparece
                if (!bossEvent.isVisible()) {
                    bossEvent.setVisible(true);
                }
            }

            // Tentativa de engolir jogador próximo
            if (swallowCooldown > 0) swallowCooldown--;

            if (swallowCooldown == 0) {
                Player target = level().getNearestPlayer(this, 4.0);
                if (target != null && !(target instanceof ServerPlayer sp &&
                        sp.getPersistentData().getBoolean("infiniti.sand_anchored"))) {
                    swallowPlayer(target);
                    swallowCooldown = 100; // 5s cooldown
                }
            }

            // Fase 2: Chama SandCrawlers
            if (hp <= 300 && tickCount % 200 == 0) {
                summonCrawlers();
            }
        }
    }

    private void swallowPlayer(Player player) {
        // "Engole" — teleporta longe e causa dano
        double angle = level().random.nextDouble() * Math.PI * 2;
        player.teleportTo(
                this.getX() + Math.cos(angle) * 30,
                this.getY() + 10,
                this.getZ() + Math.sin(angle) * 30
        );
        player.hurt(level().damageSources().mobAttack(this), 40.0f);
        level().playSound(null, this.blockPosition(),
                SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 5.0f, 0.3f);

        if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.translatable("infiniti.leviathan.swallowed"));
        }
    }

    private void summonCrawlers() {
        for (int i = 0; i < 3 + level().random.nextInt(3); i++) {
            SandCrawlerEntity crawler = ModEntities.SAND_CRAWLER.get()
                    .create(level());
            if (crawler != null) {
                double angle = level().random.nextDouble() * Math.PI * 2;
                crawler.setPos(
                        this.getX() + Math.cos(angle) * 8,
                        this.getY(),
                        this.getZ() + Math.sin(angle) * 8
                );
                level().addFreshEntity(crawler);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        // LeviathanScale
        int scales = 1 + level().random.nextInt(3) + looting;
        spawnAtLocation(new ItemStack(ModItems.LEVIATHAN_SCALE.get(), scales));

        // SpiceEssence
        int spice = 2 + level().random.nextInt(4) + looting;
        spawnAtLocation(new ItemStack(ModItems.SPICE_ESSENCE.get(), spice));

        // BoneCrystalIngot
        int crystals = 3 + level().random.nextInt(4);
        spawnAtLocation(new ItemStack(ModItems.BONE_CRYSTAL_INGOT.get(), crystals));

        // 10% WormCaller
        if (level().random.nextFloat() < 0.10f) {
            spawnAtLocation(new ItemStack(ModItems.WORM_CALLER.get(), 1));
        }
    }

    // Barra de boss: adiciona/remove jogadores

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}
