package com.infinitedimensions.crafting;

import com.infinitedimensions.genes.GeneResolver;
import com.infinitedimensions.items.CombinationOrb;
import com.infinitedimensions.items.ModItems;
import com.infinitedimensions.util.NameGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.infinitedimensions.InfiniteDimensions.MOD_ID;

/**
 * AnvilNameHandler — permite ao jogador adicionar nomes de pessoas
 * a um CombinationOrb usando a bigorna.
 *
 * Mecânica:
 *   - Slot esquerdo: CombinationOrb
 *   - Slot direito: qualquer item com nome customizado (renomeado na bigorna)
 *     OU papel escrito (WrittenBookItem) com texto
 *   - Custo: 3 níveis de XP
 *   - Resultado: novo CombinationOrb com o nome adicionado à lista
 *
 * O nome extraído é o nome do item do slot direito (via hoverName).
 * Ex: renomeie um papel para "João" e combine com o orbe.
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilNameHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left  = event.getLeft();
        ItemStack right = event.getRight();

        // Slot esquerdo deve ser um CombinationOrb
        if (!left.is(ModItems.COMBINATION_ORB.get())) return;

        // Slot direito deve ter um nome customizado
        if (!right.hasCustomHoverName()) return;

        String customName = right.getHoverName().getString().trim();
        if (customName.isEmpty()) return;

        // Cria o novo orbe com o nome adicionado
        ItemStack result = left.copy();
        CombinationOrb.addCustomName(result, customName);

        // Recalcula seed e nome com os nomes atualizados
        List<String> ingredients = CombinationOrb.getIngredients(result);
        List<String> names       = CombinationOrb.getCustomNames(result);

        long seed = GeneResolver.buildSeed(ingredients, names);
        CombinationOrb.setSeed(result, seed);
        CombinationOrb.setDimensionName(result, NameGenerator.generate(seed, ""));

        event.setOutput(result);
        event.setCost(3);
        event.setMaterialCost(0);
    }
}
