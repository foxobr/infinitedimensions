package com.infiniti.items;

import com.infiniti.InfinitMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModItems — todos os itens exclusivos da Dimensão Infiniti.
 *
 * ═══════════════════════════════════════════════════════
 * RECURSOS BRUTOS (drops de minérios)
 * ═══════════════════════════════════════════════════════
 *   sunstone_crystal      — drop de SunstoneOre (brilha dourado)
 *   spice_dust            — drop de SpiceOre (pó alaranjado iridescente)
 *   raw_dune_iron         — drop de DuneIronOre
 *   bone_crystal_shard    — drop de BoneCrystalOre
 *   thermite_powder       — drop de ThermiteOre (CUIDADO: inflamável)
 *
 * ═══════════════════════════════════════════════════════
 * MATERIAIS PROCESSADOS (Sandforge)
 * ═══════════════════════════════════════════════════════
 *   dune_ingot            — RawDuneIron fundido; base de ferramentas/armaduras
 *   bone_crystal_ingot    — BoneCrystalShard x3 fundidos; leve e resistente
 *   thermite_charge       — ThermitePowder x4 comprimido; explosivo preciso
 *   sunstone_wafer        — SunstoneCrystal processado; componente de armadura
 *   spice_essence         — SpiceDust concentrado; bônus cognitivo
 *
 * ═══════════════════════════════════════════════════════
 * ITENS FUNCIONAIS (Encrucias)
 * ═══════════════════════════════════════════════════════
 *   desert_compass        — aponta para o portal de retorno
 *   sand_flask            — armazena "água de areia" (hidratação da dimensão)
 *   spice_pill            — consumível; dá SpiceVision por 60s (night vision + percepção)
 *   thumper_device        — item deployável; atrai SandCrawlers (worms pequenos)
 *   worm_caller           — versão avançada do Thumper; pode chamar o SandLeviathan
 *   dune_map              — mapa especial que revela estruturas da dimensão
 *   sand_anchor           — âncora que previne ser devorado pelo SandLeviathan
 *   stillsuit_patch       — conserta o Stillsuit (armadura especial)
 *
 * ═══════════════════════════════════════════════════════
 * ARMADURAS (Stillsuit — traje de sobrevivência do deserto)
 * ═══════════════════════════════════════════════════════
 *   stillsuit_helmet      — filtra areia, previne dano de tempestade
 *   stillsuit_chestplate  — recupera hidratação lentamente
 *   stillsuit_leggings    — reduz pegada (worms não ouvem)
 *   stillsuit_boots       — caminha sobre areia solta sem afundar
 *
 * ═══════════════════════════════════════════════════════
 * FERRAMENTAS (Dune Iron tier)
 * ═══════════════════════════════════════════════════════
 *   dune_pickaxe          — minera blocos da dimensão eficientemente
 *   dune_sword            — aplica SandBleed (sangramento lento)
 *   dune_shovel           — escava areia/solo instantaneamente
 *
 * ═══════════════════════════════════════════════════════
 * ITENS DE CRIATURA
 * ═══════════════════════════════════════════════════════
 *   sandcrawler_chitin    — drop do SandCrawler (armadura e crafting)
 *   leviathan_scale       — drop raro do SandLeviathan (encantamentos únicos)
 *   dune_hawk_feather     — drop do DuneHawk (leveza e velocidade)
 *   scavenger_token       — drop do DuneScavenger (moeda de troca)
 *
 * ═══════════════════════════════════════════════════════
 * PORTAL
 * ═══════════════════════════════════════════════════════
 *   infiniti_portal_key   — item que ativa o portal para a Dimensão Infiniti
 *                           Crafting: SpiceEssence + DuneIngot + SunstoneWafer
 */
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InfinitMod.MOD_ID);

    // ── Recursos Brutos ──
    public static final RegistryObject<Item> SUNSTONE_CRYSTAL = ITEMS.register("sunstone_crystal",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SPICE_DUST = ITEMS.register("spice_dust",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> RAW_DUNE_IRON = ITEMS.register("raw_dune_iron",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BONE_CRYSTAL_SHARD = ITEMS.register("bone_crystal_shard",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> THERMITE_POWDER = ITEMS.register("thermite_powder",
            () -> new Item(new Item.Properties()));

    // ── Materiais Processados ──
    public static final RegistryObject<Item> DUNE_INGOT = ITEMS.register("dune_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BONE_CRYSTAL_INGOT = ITEMS.register("bone_crystal_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> THERMITE_CHARGE = ITEMS.register("thermite_charge",
            () -> new ThermiteChargeItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SUNSTONE_WAFER = ITEMS.register("sunstone_wafer",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SPICE_ESSENCE = ITEMS.register("spice_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // ── Itens Funcionais ──
    public static final RegistryObject<Item> DESERT_COMPASS = ITEMS.register("desert_compass",
            () -> new DesertCompassItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SAND_FLASK = ITEMS.register("sand_flask",
            () -> new SandFlaskItem(new Item.Properties().stacksTo(1).durability(64)));

    public static final RegistryObject<Item> SPICE_PILL = ITEMS.register("spice_pill",
            () -> new SpicePillItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> THUMPER_DEVICE = ITEMS.register("thumper_device",
            () -> new ThumperDeviceItem(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> WORM_CALLER = ITEMS.register("worm_caller",
            () -> new WormCallerItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DUNE_MAP = ITEMS.register("dune_map",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SAND_ANCHOR = ITEMS.register("sand_anchor",
            () -> new SandAnchorItem(new Item.Properties().stacksTo(1).durability(32)));

    // ── Armaduras Stillsuit ──
    public static final RegistryObject<Item> STILLSUIT_HELMET = ITEMS.register("stillsuit_helmet",
            () -> new StillsuitArmorItem(StillsuitArmorItem.Type.HELMET,
                    new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> STILLSUIT_CHESTPLATE = ITEMS.register("stillsuit_chestplate",
            () -> new StillsuitArmorItem(StillsuitArmorItem.Type.CHESTPLATE,
                    new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> STILLSUIT_LEGGINGS = ITEMS.register("stillsuit_leggings",
            () -> new StillsuitArmorItem(StillsuitArmorItem.Type.LEGGINGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> STILLSUIT_BOOTS = ITEMS.register("stillsuit_boots",
            () -> new StillsuitArmorItem(StillsuitArmorItem.Type.BOOTS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)));

    // ── Ferramentas ──
    public static final RegistryObject<Item> DUNE_PICKAXE = ITEMS.register("dune_pickaxe",
            () -> new DunePickaxeItem(new Item.Properties()));

    public static final RegistryObject<Item> DUNE_SWORD = ITEMS.register("dune_sword",
            () -> new DuneSwordItem(new Item.Properties()));

    public static final RegistryObject<Item> DUNE_SHOVEL = ITEMS.register("dune_shovel",
            () -> new DuneShovelItem(new Item.Properties()));

    // ── Drops de Criaturas ──
    public static final RegistryObject<Item> SANDCRAWLER_CHITIN = ITEMS.register("sandcrawler_chitin",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LEVIATHAN_SCALE = ITEMS.register("leviathan_scale",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> DUNE_HAWK_FEATHER = ITEMS.register("dune_hawk_feather",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SCAVENGER_TOKEN = ITEMS.register("scavenger_token",
            () -> new Item(new Item.Properties().stacksTo(64)));

    // ── Portal ──
    public static final RegistryObject<Item> INFINITI_PORTAL_KEY = ITEMS.register("infiniti_portal_key",
            () -> new InfitiPortalKeyItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
}
