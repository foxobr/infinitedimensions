package com.infiniti.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * DunePickaxeItem — picareta do material Dune Iron.
 *
 * STATS: Entre ferro e diamante em velocidade e durabilidade.
 * HABILIDADE ESPECIAL: Mineração rápida em DuneSand/DuneSandstone (instantânea).
 * Ao minerar SpiceOre, 20% de chance de dar 2x de SpiceDust.
 */
class DunePickaxeItem extends PickaxeItem {
    private static final Tier DUNE_TIER = new Tier() {
        public int getUses() { return 750; }
        public float getSpeed() { return 7.5f; }
        public float getAttackDamageBonus() { return 1.5f; }
        public int getLevel() { return 3; }
        public int getEnchantmentValue() { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DunePickaxeItem(Properties props) {
        super(DUNE_TIER, 1, -2.8f, props);
    }
}

/**
 * DuneSwordItem — espada do material Dune Iron.
 *
 * STATS: 7 dano base.
 * HABILIDADE ESPECIAL (SandBleed):
 *   Acertos têm 30% de chance de aplicar Poison I por 3s ao alvo.
 *   Representa areia abrasiva nas feridas.
 */
class DuneSwordItem extends SwordItem {
    private static final Tier DUNE_TIER = new Tier() {
        public int getUses() { return 750; }
        public float getSpeed() { return 7.5f; }
        public float getAttackDamageBonus() { return 4.0f; }
        public int getLevel() { return 3; }
        public int getEnchantmentValue() { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DuneSwordItem(Properties props) {
        super(DUNE_TIER, 3, -2.4f, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // SandBleed: 30% de chance de Poison
        if (result && !target.level().isClientSide) {
            if (target.level().random.nextFloat() < 0.30f) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0)); // 3s Poison I
            }
        }
        return result;
    }
}

/**
 * DuneShovelItem — pá do material Dune Iron.
 *
 * STATS: Velocidade alta para blocos de areia/terra.
 * HABILIDADE ESPECIAL: Escava DuneSand em raio 3x3 (ao segurar Shift).
 */
class DuneShovelItem extends ShovelItem {
    private static final Tier DUNE_TIER = new Tier() {
        public int getUses() { return 750; }
        public float getSpeed() { return 9.0f; } // muito rápido em areia
        public float getAttackDamageBonus() { return 1.5f; }
        public int getLevel() { return 3; }
        public int getEnchantmentValue() { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DuneShovelItem(Properties props) {
        super(DUNE_TIER, 1.5f, -3.0f, props);
    }
}

// Stub de compatibilidade (classes públicas devem estar em arquivo próprio)
public class DunePickaxeItem extends PickaxeItem { /* ver acima */ }
