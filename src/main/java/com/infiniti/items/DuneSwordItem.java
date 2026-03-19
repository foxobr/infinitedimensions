package com.infiniti.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

/**
 * DuneSwordItem — espada de Dune Iron com habilidade SandBleed.
 *
 * STATS: 7 dano total, velocidade padrão.
 *
 * HABILIDADE SandBleed:
 *   Todo acerto tem 30% de chance de aplicar Poison I (3s) ao alvo.
 *   Representa a areia abrasiva que fica nas feridas abertas.
 *   Criaturas imunes a veneno (esqueletos, etc.) são imunes ao SandBleed.
 */
public class DuneSwordItem extends SwordItem {

    private static final Tier DUNE_TIER = new Tier() {
        public int getUses()                    { return 750; }
        public float getSpeed()                 { return 7.5f; }
        public float getAttackDamageBonus()     { return 4.0f; }
        public int getLevel()                   { return 3; }
        public int getEnchantmentValue()        { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DuneSwordItem(Properties props) {
        super(DUNE_TIER, 3, -2.4f, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hit = super.hurtEnemy(stack, target, attacker);
        if (hit && !target.level().isClientSide) {
            // SandBleed: 30% de chance
            if (target.level().random.nextFloat() < 0.30f) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.POISON, 60, 0, false, true));
            }
        }
        return hit;
    }
}
