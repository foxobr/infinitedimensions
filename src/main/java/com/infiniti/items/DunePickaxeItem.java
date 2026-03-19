package com.infiniti.items;

import net.minecraft.world.item.*;

/**
 * DunePickaxeItem — picareta de Dune Iron.
 *
 * STATS (entre ferro e diamante):
 *   Durabilidade: 750
 *   Velocidade:   7.5
 *   Nível:        3 (pode minerar qualquer bloco exceto Bedrock)
 *
 * ESPECIAL:
 *   - Minera DuneSand/DuneSandstone instantaneamente
 *   - 20% de chance de drop duplo em SpiceOre
 */
public class DunePickaxeItem extends PickaxeItem {

    private static final Tier DUNE_TIER = new Tier() {
        public int getUses()                    { return 750; }
        public float getSpeed()                 { return 7.5f; }
        public float getAttackDamageBonus()     { return 1.5f; }
        public int getLevel()                   { return 3; }
        public int getEnchantmentValue()        { return 14; }
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.DUNE_INGOT.get());
        }
    };

    public DunePickaxeItem(Properties props) {
        super(DUNE_TIER, 1, -2.8f, props);
    }
}
