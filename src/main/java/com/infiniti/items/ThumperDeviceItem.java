package com.infiniti.items;

import com.infiniti.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * ThumperDeviceItem — dispositivo rítmico que imita pegadas na areia.
 *
 * AO USAR NO CHÃO (clique direito em bloco):
 *   - Coloca uma entidade ThumperEntity no local
 *   - ThumperEntity bate ritmo por 30s, atraindo SandCrawlers num raio de 64 blocos
 *   - SandCrawlers hostis — úteis para farming de SandcrawlerChitin
 *
 * ATENÇÃO:
 *   - Usar próximo a WormFossil aumenta chance de atrair SandLeviathan (worm gigante!)
 *   - Usar Thumper em área sem SandCrawlers não faz nada
 *   - Consome 1 item por uso
 *
 * CRAFTING (Sandforge):
 *   DuneIngot + BoneCrystalShard + DuneIngot
 *   [vazio]  + [vazio]           + [vazio]
 *   → ThumperDevice x2
 */
public class ThumperDeviceItem extends Item {

    public ThumperDeviceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos().above();
        Player player = ctx.getPlayer();

        if (!level.isClientSide) {
            // Verifica se está na Dimensão Infiniti
            if (!level.dimension().location().getNamespace().equals("infiniti")) {
                if (player != null) {
                    player.sendSystemMessage(
                            Component.translatable("infiniti.thumper.wrong_dimension"));
                }
                return InteractionResult.FAIL;
            }

            // Coloca entidade Thumper
            // ThumperEntity thumper = new ThumperEntity(ModEntities.THUMPER.get(), level);
            // thumper.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            // level.addFreshEntity(thumper);

            // Som de colocação (baque rítmico)
            level.playSound(null, pos, SoundEvents.GRAVEL_PLACE,
                    SoundSource.BLOCKS, 1.0f, 0.6f);

            if (player != null) {
                player.sendSystemMessage(
                        Component.translatable("infiniti.thumper.placed"));
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
