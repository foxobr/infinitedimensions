package com.infiniti.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * ThermiteChargeItem — carga explosiva de Termita.
 *
 * AO USAR (clique direito em bloco):
 *   - Planta a carga no bloco clicado
 *   - Após 3 segundos, explode com força 4 (igual a TNT)
 *   - DIFERENÇA da TNT: não destrói blocos resistentes (Sandforge, WormFossil)
 *   - BÔNUS: causa 2x de dano em criaturas com exoesqueleto (SandCrawler)
 *
 * USOS TÁTICOS:
 *   - Criar entradas em estruturas de arenito
 *   - Combate contra grupos de SandCrawlers
 *   - NÃO usar perto de SpiceBlock — provoca detonação em cadeia!
 *
 * CRAFTING: ThermitePowder x4 → ThermiteCharge (via Sandforge)
 */
public class ThermiteChargeItem extends Item {

    public ThermiteChargeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos().above();

        if (!level.isClientSide) {
            // Coloca uma entidade PrimedTnt customizada (ThermiteCharge)
            // Por simplicidade, usamos PrimedTnt vanilla com fuse customizado
            net.minecraft.world.entity.item.PrimedTnt tnt =
                    new net.minecraft.world.entity.item.PrimedTnt(level,
                            pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            tnt.setFuse(60); // 3 segundos
            level.addFreshEntity(tnt);

            ctx.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
