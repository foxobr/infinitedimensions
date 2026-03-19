package com.infinitedimensions.crafting;

import com.infinitedimensions.genes.GeneResolver;
import com.infinitedimensions.items.CombinationOrb;
import com.infinitedimensions.items.ModItems;
import com.infinitedimensions.util.NameGenerator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

import static com.infinitedimensions.InfiniteDimensions.MOD_ID;

/**
 * InfiniteRecipeHandler — receita de crafting que aceita QUALQUER combinação
 * de itens para produzir um CombinationOrb.
 *
 * Regras:
 *   - Precisa de pelo menos 2 itens não-vazios no grid
 *   - Os ingredientes são armazenados no NBT do orbe resultante
 *   - A seed e o nome da dimensão são pré-calculados no craft
 *   - NÃO precisa do CombinationOrb no grid — ele é o resultado
 *
 * Para adicionar um nome ao orbe, coloque-o na bigorna com um papel
 * escrito (ver AnvilNameHandler).
 */
public class InfiniteRecipeHandler extends CustomRecipe {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<RecipeSerializer<InfiniteRecipeHandler>> SERIALIZER =
        SERIALIZERS.register("infinite_crafting",
            () -> new RecipeSerializer<>() {
                @Override
                public InfiniteRecipeHandler fromJson(ResourceLocation id,
                    com.google.gson.JsonObject json) {
                    return new InfiniteRecipeHandler(id, CraftingBookCategory.MISC);
                }

                @Override
                public InfiniteRecipeHandler fromNetwork(ResourceLocation id,
                    net.minecraft.network.FriendlyByteBuf buf) {
                    return new InfiniteRecipeHandler(id, CraftingBookCategory.MISC);
                }

                @Override
                public void toNetwork(net.minecraft.network.FriendlyByteBuf buf,
                    InfiniteRecipeHandler recipe) {}
            });

    public InfiniteRecipeHandler(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int nonEmptyCount = 0;
        boolean hasCombinationOrb = false;
        boolean hasRareItem = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                nonEmptyCount++;
                
                // Não permite combinar dois orbes no mesmo craft
                if (stack.is(ModItems.COMBINATION_ORB.get())) {
                    hasCombinationOrb = true;
                }
                
                // Verifica se tem algum item "valioso"
                if (isRareItem(stack)) {
                    hasRareItem = true;
                }
            }
        }

        // Regras para ativar a receita:
        // 1. Precisa de pelo menos 3 itens (evita interferir com receitas simples de 2 itens)
        // 2. OU ter pelo menos 1 item raro
        // 3. NÃO pode ter CombinationOrb
        return !hasCombinationOrb && ((nonEmptyCount >= 3) || hasRareItem);
    }

    private boolean isRareItem(ItemStack stack) {
        return stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || 
               stack.is(Items.GOLD_INGOT) || stack.is(Items.NETHERITE_INGOT) ||
               stack.is(Items.AMETHYST_SHARD) || stack.is(Items.ECHO_SHARD);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        List<String> ingredientIds = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                String id = net.minecraftforge.registries.ForgeRegistries.ITEMS
                    .getKey(stack.getItem()).toString();
                ingredientIds.add(id);
            }
        }

        // Cria o orbe resultante
        ItemStack result = new ItemStack(ModItems.COMBINATION_ORB.get());

        // Armazena ingredientes no NBT
        for (String id : ingredientIds) {
            CombinationOrb.addIngredient(result, id);
        }

        // Pré-calcula seed e nome (sem nomes customizados ainda)
        long seed = GeneResolver.buildSeed(ingredientIds, List.of());
        CombinationOrb.setSeed(result, seed);

        String dimName = NameGenerator.generate(seed, "");
        CombinationOrb.setDimensionName(result, dimName);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // Funciona em qualquer grid 2x2 ou maior
        return width * height >= 4;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }
}
