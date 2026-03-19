package com.infiniti.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * DesertCompassItem — bússola do deserto que aponta para o portal de retorno.
 *
 * Na Dimensão Infiniti:
 *   Aponta em direção ao ponto de spawn da dimensão (topo das ruínas centrais).
 *
 * No Overworld:
 *   Aponta para o último portal de saída utilizado pelo jogador.
 *
 * Visual: agulha laranja brilhante, mesma mecânica da Recovery Compass.
 */
public class DesertCompassItem extends Item {

    public DesertCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Lê coordenadas salvas do portal
        double px = player.getPersistentData().getDouble("infiniti.portal_x");
        double pz = player.getPersistentData().getDouble("infiniti.portal_z");

        player.sendSystemMessage(Component.literal(
                String.format("§6Portal: X=%.0f  Z=%.0f", px, pz)));

        return InteractionResultHolder.sidedSuccess(
                player.getItemInHand(hand), level.isClientSide);
    }
}
