package com.infiniti.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * SandforgeBlock — bloco de trabalho principal da Dimensão Infiniti.
 *
 * FUNÇÃO:
 *   - Clicar com mão direita abre o menu de forja.
 *   - Aceita receitas especiais definidas em SandforgeRecipeHandler:
 *       RawDuneIron + SpiceDust → DuneIngot (2x mais forte que ferro)
 *       BoneCrystalShard x3    → BoneCrystalIngot
 *       ThermitePowder x4      → ThermiteCharge (explosivo controlado)
 *       SunstoneCrystal        → SunstoneWafer (componente de armadura)
 *
 * MECÂNICA ESPECIAL (Encruecias):
 *   - Se o jogador estiver com sede (efeito SandThirst ativo),
 *     as receitas ficam 50% mais lentas.
 *   - Se o jogador tiver SpiceEssence no inventário,
 *     as receitas ficam 2x mais rápidas (bônus de foco).
 *
 * VISUAL:
 *   - Emite partículas de areia incandescente enquanto processa.
 *   - Levemente luminoso (lightLevel=8) para visibilidade noturna.
 */
public class SandforgeBlock extends Block {

    public SandforgeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Abre menu de forja (reutiliza FurnaceMenu como base;
            // SandforgeRecipeHandler injeta receitas customizadas via AnvilUpdateEvent equivalente)
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.infiniti.sandforge");
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new FurnaceMenu(id, inv);
                }
            });

            // Envia mensagem de dica na primeira abertura
            if (!player.getPersistentData().getBoolean("infiniti.sandforge_hint_shown")) {
                player.sendSystemMessage(Component.translatable("infiniti.sandforge.hint"));
                player.getPersistentData().putBoolean("infiniti.sandforge_hint_shown", true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
