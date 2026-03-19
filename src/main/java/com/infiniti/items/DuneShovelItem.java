package com.infiniti.items;

import net.minecraft.world.item.*;

/**
 * DuneShovelItem — pá de Dune Iron.
 * Velocidade altíssima em areia. Ideal para exploradores.
 */
public class DuneShovelItem extends ShovelItem {

    private static final Tier DUNE_TIER = new Tier() {
        public int getUses()                    { return 750; }
        public float getSpeed()                 { return 9.0f; }
        public float getAttackDamageBonus()     { return 1.5f; }
        public int getLevel()                   { return 3; }
        public int getEnchantmentValue()        { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DuneShovelItem(Properties props) {
        super(DUNE_TIER, 1.5f, -3.0f, props);
    }
}
