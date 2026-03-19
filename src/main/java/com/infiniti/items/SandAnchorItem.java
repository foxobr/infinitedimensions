package com.infiniti.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * SandAnchorItem — âncora de areia que crava o jogador no chão.
 *
 * MECÂNICA:
 *   Clicar com direito na mão: ativa/desativa a âncora.
 *   Quando ativa:
 *     - Jogador fica imóvel (velocidade = 0)
 *     - SandLeviathan NÃO consegue devorar o jogador (ignora ao passar)
 *     - Reduz dano de SandCrawlers em 50%
 *     - Consome 1 durabilidade a cada 5 segundos de uso
 *
 *   Quando inativa:
 *     - Jogador pode se mover normalmente
 *     - SandLeviathan PODE devorar se passar sobre o jogador
 *
 * ESTRATÉGIA:
 *   - Ativar quando ouvir o tremor do SandLeviathan se aproximando
 *   - Desativar para fugir entre passagens do worm
 *   - Combinação ideal: SandAnchor + StillsuitBoots (menos vibração)
 *
 * CRAFTING (Sandforge):
 *   BoneCrystalIngot + DuneIngot + BoneCrystalIngot
 *   [vazio]          + DuneIngot + [vazio]
 *   → SandAnchor x1  (32 durabilidade)
 */
public class SandAnchorItem extends Item {

    public SandAnchorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Toggle âncora
        boolean currentlyAnchored = player.getPersistentData()
                .getBoolean("infiniti.sand_anchored");

        if (!currentlyAnchored) {
            // Ativar âncora
            player.getPersistentData().putBoolean("infiniti.sand_anchored", true);
            player.sendSystemMessage(Component.translatable("infiniti.anchor.activated"));

            // Efeito visual: partículas de areia ao redor dos pés
            if (!level.isClientSide) {
                // Partículas serão adicionadas via EffectTicker
            }
        } else {
            // Desativar âncora
            player.getPersistentData().putBoolean("infiniti.sand_anchored", false);
            player.sendSystemMessage(Component.translatable("infiniti.anchor.deactivated"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
