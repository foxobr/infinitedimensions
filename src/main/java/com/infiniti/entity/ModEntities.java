package com.infiniti.entity;

import com.infiniti.InfinitMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModEntities — registro de todas as criaturas da Dimensão Infiniti.
 *
 * ═══════════════════════════════════════════════════════════════
 * CRIATURAS PASSIVAS
 * ═══════════════════════════════════════════════════════════════
 *
 * DUNE_MOUSE (DuneMouseEntity)
 *   Inspiração: Rato de areia / Muriformes de Duna
 *   Comportamento: Corre pela superfície, se esconde em buracos no chão.
 *   Drop: DuneMouseFur (artesanato menor)
 *   Especial: Nunca é atingido pelo SandLeviathan (ele os ignora).
 *             Seguir um DuneMouse frequentemente leva a oásis ou ruínas escondidas.
 *
 * SAND_BEETLE (SandBeetleEntity)
 *   Inspiração: Besouro scarab gigante
 *   Comportamento: Escavação visível na areia; faz trilhas.
 *   Drop: SandcrawlerChitin (pequena quantidade)
 *   Especial: Seguir 5+ Sand Beetles leva ao Nest, onde há SpiceOre concentrado.
 *
 * DUNE_HAWK (DuneHawkEntity)
 *   Inspiração: Falcão do deserto
 *   Comportamento: Voa em círculos acima de pontos de interesse.
 *   Drop: DuneHawkFeather
 *   Especial: Círculos de hawk = estrutura abaixo. Recurso de exploração.
 *
 * ═══════════════════════════════════════════════════════════════
 * CRIATURAS NEUTRAS
 * ═══════════════════════════════════════════════════════════════
 *
 * SAND_CAMEL (SandCamelEntity)
 *   Inspiração: Camelo adaptado ao deserto extremo
 *   Comportamento: Passivo; pode ser montado com SandFlask na mão.
 *   Montado: Velocidade 1.5x, não afunda em areia, carrega inventário (6 slots).
 *   Drop: SandCamelHide (armadura básica) + SandFlask cheia
 *   Especial: SandLeviathan NÃO ataca SandCamels (evolução mútua).
 *
 * DUNE_SCAVENGER (DuneScavengerEntity — tipo Goblin/Tuareg)
 *   Inspiração: Nômade sobrevivente das dunas
 *   Comportamento: Neutro; ataca se provocado ou se você roubou dele.
 *   Drop: ScavengerToken, DuneMap aleatório, itens misc.
 *   Especial: Pode ser "convencido" com ScavengerTokens a revelar localização
 *             de estruturas próximas (diálogo simples de troca).
 *
 * ═══════════════════════════════════════════════════════════════
 * CRIATURAS HOSTIS
 * ═══════════════════════════════════════════════════════════════
 *
 * SAND_CRAWLER (SandCrawlerEntity)
 *   Inspiração: Worm menor / artrópode do deserto
 *   Tamanho: 2 blocos de comprimento, 1 de altura
 *   Comportamento: Emerge da areia quando sente vibração (passos).
 *                  Fica parado se jogador usar StillsuitLeggings.
 *   Drop: SandcrawlerChitin (2-4)
 *   Especial: Atraído por ThumperDevice. Pode ser usado para farming.
 *             Se em grupo de 5+, atacam em coordenação (cerco).
 *
 * SAND_SHADE (SandShadeEntity)
 *   Inspiração: Fantasma das tempestades de areia / Djinn
 *   Comportamento: Só aparece durante TempestadeDeAreia (evento climático).
 *                  Ataca cegando o jogador e sugando velocidade.
 *   Drop: SpiceDust (raro), SandShadeEssence
 *   Especial: Capacete Stillsuit previne cegueira do SandShade.
 *
 * ═══════════════════════════════════════════════════════════════
 * BOSS / MEGA-CRIATURA
 * ═══════════════════════════════════════════════════════════════
 *
 * SAND_LEVIATHAN (SandLeviathanEntity)
 *   Inspiração: Shai-Hulud (o verme gigante de Duna)
 *   Tamanho: 40+ blocos de comprimento, 5 de diâmetro
 *   Spawn: Invocado por WormCaller, ou naturalmente em certas áreas.
 *          Não spawna espontaneamente perto do spawn da dimensão.
 *   Comportamento:
 *     - Nada pela areia como se fosse água (animação de duna se movendo)
 *     - Detecta jogadores por vibração (passos na areia)
 *     - StillsuitLeggings reduz chance de detecção em 70%
 *     - SandAnchor ativo: Leviathan passa sem atacar
 *     - Sem proteção: morte instantânea por "devoração" (tp para fora)
 *   HP: 500 (extremamente alto)
 *   Drop: LeviathanScale (1-3), SpiceEssence (2-5), BoneCrystalIngot (3-6)
 *   Especial: Única fonte de LeviathanScale para endgame.
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, InfinitMod.MOD_ID);

    // ── Passivas ──
    public static final RegistryObject<EntityType<DuneMouseEntity>> DUNE_MOUSE =
            ENTITY_TYPES.register("dune_mouse",
                    () -> EntityType.Builder.<DuneMouseEntity>of(DuneMouseEntity::new, MobCategory.CREATURE)
                            .sized(0.4f, 0.3f)
                            .build("dune_mouse"));

    public static final RegistryObject<EntityType<SandBeetleEntity>> SAND_BEETLE =
            ENTITY_TYPES.register("sand_beetle",
                    () -> EntityType.Builder.<SandBeetleEntity>of(SandBeetleEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 0.4f)
                            .build("sand_beetle"));

    public static final RegistryObject<EntityType<DuneHawkEntity>> DUNE_HAWK =
            ENTITY_TYPES.register("dune_hawk",
                    () -> EntityType.Builder.<DuneHawkEntity>of(DuneHawkEntity::new, MobCategory.CREATURE)
                            .sized(0.9f, 0.6f)
                            .build("dune_hawk"));

    // ── Neutras ──
    public static final RegistryObject<EntityType<SandCamelEntity>> SAND_CAMEL =
            ENTITY_TYPES.register("sand_camel",
                    () -> EntityType.Builder.<SandCamelEntity>of(SandCamelEntity::new, MobCategory.CREATURE)
                            .sized(1.3f, 2.2f)
                            .build("sand_camel"));

    public static final RegistryObject<EntityType<DuneScavengerEntity>> DUNE_SCAVENGER =
            ENTITY_TYPES.register("dune_scavenger",
                    () -> EntityType.Builder.<DuneScavengerEntity>of(DuneScavengerEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.9f)
                            .build("dune_scavenger"));

    // ── Hostis ──
    public static final RegistryObject<EntityType<SandCrawlerEntity>> SAND_CRAWLER =
            ENTITY_TYPES.register("sand_crawler",
                    () -> EntityType.Builder.<SandCrawlerEntity>of(SandCrawlerEntity::new, MobCategory.MONSTER)
                            .sized(2.0f, 0.9f)
                            .build("sand_crawler"));

    public static final RegistryObject<EntityType<SandShadeEntity>> SAND_SHADE =
            ENTITY_TYPES.register("sand_shade",
                    () -> EntityType.Builder.<SandShadeEntity>of(SandShadeEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 2.4f)
                            .fireImmune()
                            .build("sand_shade"));

    // ── Boss ──
    public static final RegistryObject<EntityType<SandLeviathanEntity>> SAND_LEVIATHAN =
            ENTITY_TYPES.register("sand_leviathan",
                    () -> EntityType.Builder.<SandLeviathanEntity>of(SandLeviathanEntity::new, MobCategory.MONSTER)
                            .sized(5.0f, 5.0f) // bounding box central; entidade segmentada
                            .fireImmune()
                            .build("sand_leviathan"));
}
