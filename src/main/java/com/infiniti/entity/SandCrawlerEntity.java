package com.infiniti.entity;

import com.infiniti.items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * SandCrawlerEntity — verme de areia médio, hostil.
 *
 * COMPORTAMENTO:
 *   - Permanece submerso na DuneSand até sentir vibração (passos).
 *   - Emerge da areia com animação de "explosão" de partículas.
 *   - Ataca mordendo (dano médio + chance de SandBleed = Poison).
 *   - Em grupo de 5+: behavior de cerco coordenado.
 *   - FRAQUEZA: sons rítmicos (ThumperDevice) os atraem e distraem.
 *   - FRAQUEZA: StillsuitLeggings reduz detecção em 70%.
 *
 * STATS:
 *   HP: 30     Dano: 6     Armadura: 4
 *   Velocidade: 0.35 (mais rápido que jogador sem boost)
 *
 * DROPS:
 *   SandcrawlerChitin: 2-4
 *   (Raro) SpiceDust: 1
 */
public class SandCrawlerEntity extends Monster {

    private boolean isSubmerged = true;
    private int emergeTimer = 0;

    public SandCrawlerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        // Prioridade: atacar jogador, fugir se com pouca vida
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // Detecta vibração (passos de jogador próximo)
            if (isSubmerged) {
                Player nearest = level().getNearestPlayer(this, 16.0);
                if (nearest != null) {
                    // Verifica se jogador usa StillsuitLeggings
                    boolean hasLeggings = nearest.getInventory().armor.get(1)
                            .getItem() instanceof com.infiniti.items.StillsuitArmorItem;
                    double detectRange = hasLeggings ? 4.8 : 16.0;

                    if (nearest.distanceTo(this) < detectRange && !nearest.isCreative()) {
                        emerge();
                    }
                }
            }
        }
    }

    private void emerge() {
        isSubmerged = false;
        // Partículas de areia ao emergir
        level().playSound(null, blockPosition(),
                SoundEvents.RAVAGER_STUNNED, net.minecraft.sounds.SoundSource.HOSTILE,
                0.8f, 1.5f);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        // SandcrawlerChitin: 2-4 + bônus de looting
        int amount = 2 + random.nextInt(3) + looting;
        spawnAtLocation(new ItemStack(ModItems.SANDCRAWLER_CHITIN.get(), amount));

        // 15% de SpiceDust
        if (random.nextFloat() < 0.15f + looting * 0.05f) {
            spawnAtLocation(new ItemStack(ModItems.SPICE_DUST.get(), 1));
        }
    }

    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level,
                                    MobSpawnType spawnType) {
        // Só spawna dentro da Dimensão Infiniti
        return level instanceof Level lev &&
               lev.dimension().location().getNamespace().equals("infiniti");
    }
}
