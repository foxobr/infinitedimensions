# Infinite Dimensions Mod

Mod para Minecraft 1.20.1 (Forge) que gera dimensões e crafts infinitos
de forma procedural — sem IA externa, sem internet, 100% offline.

---

## Como funciona

### 1. Craftando o Orbe de Combinação

Coloque **qualquer 2 ou mais itens** no grid de crafting (bancada 2×2 ou maior).
O resultado será um **Orbe de Combinação** com aqueles itens gravados no NBT.

```
[Olho de Aranha] [Pó de Blaze]     →   [Orbe de Combinação]
[Diamante]       [Osso]
```

Cada combinação diferente gera um orbe único, que abre uma dimensão única.

---

### 2. Adicionando nomes de pessoas (opcional)

Na **bigorna**, combine:
- Slot esquerdo: Orbe de Combinação
- Slot direito: qualquer item **renomeado** com o nome desejado (ex: papel renomeado "João")

Isso altera a seed da dimensão de forma significativa.
Custo: 3 níveis de XP.

Exemplos:
- Orbe(Diamante+Osso) + "Maria" → dimensão diferente de Orbe(Diamante+Osso) + "João"
- Você pode adicionar múltiplos nomes repetindo o processo

---

### 3. Abrindo o portal

1. Construa um **portal do Nether** normalmente
2. Acenda o portal
3. **Jogue o Orbe de Combinação dentro do portal** (clique direito com o orbe na mão apontando para o portal)
4. Entre no portal — você será teleportado para a dimensão do orbe, não para o Nether

O portal continua funcionando normalmente para outros jogadores sem orbe.

---

### 4. Voltando ao Overworld

Dentro de qualquer dimensão do mod:

```
Agachar (Shift) + Clique Direito em qualquer bloco → Retorna ao Overworld
```

Você aparece na mesma posição de onde entrou no portal.
Todos os efeitos de dimensão são removidos ao retornar.

---

## Sistemas de genes

Cada item contribui com "genes" que definem as propriedades da dimensão:

| Gene     | Itens que contribuem                    | Efeito na dimensão               |
|----------|-----------------------------------------|----------------------------------|
| heat     | Blaze powder, lava, fogo               | Magma, calor, fire resistance    |
| cold     | Gelo, neve, pó de neve                 | Gelo, slowness                   |
| dark     | Obsidiana, soul sand, caveiras         | Escuridão, blindness, wither     |
| light    | Glowstone, nether star                 | Glowing, night vision            |
| poison   | Olho de aranha, peixe baiacu           | Veneno, slime blocks             |
| undead   | Osso, carne podre, caveiras            | Bone blocks, soul, wither        |
| crystal  | Diamante, ametista, quartzo            | Cristais, resistance             |
| wetness  | Água, prismerina, coração do mar       | Argila, water breathing          |
| bouncy   | Slimeball, wind charge                 | Slime, jump boost                |
| hostile  | TNT, pólvora, cabeças de wither        | Mobs agressivos, weakness        |
| stable   | Pedra, obsidiana, ferro                | Terreno plano, absorption        |
| void     | Sculk, ender pearl, ancient debris     | Sculk, darkness                  |
| sky      | Pena, élitras, firework               | End stone, levitation            |
| nature   | Mudas, musgo, azaleia                  | Grama, regeneration              |
| chaos    | TNT, pólvora, fermented spider eye    | Terreno caótico, nausea          |
| magic    | Ender eye, experiência, livros         | Night vision, efeitos mágicos    |

**Nomes de pessoas** distribuem genes aleatoriamente baseados nas letras do nome,
funcionando como um modificador único que não se repete.

---

## Estrutura do projeto

```
src/main/java/com/infinitedimensions/
├── InfiniteDimensions.java         ← Classe principal do mod
├── items/
│   ├── ModItems.java               ← Registro dos itens
│   └── CombinationOrb.java        ← Item Orbe (armazena combinação em NBT)
├── crafting/
│   ├── InfiniteRecipeHandler.java  ← Receita infinita (qualquer item → orbe)
│   └── AnvilNameHandler.java      ← Adicionar nomes via bigorna
├── events/
│   ├── PortalItemListener.java     ← Detecta orbe no portal, cria dimensão
│   ├── ReturnPortalHandler.java    ← Shift+clique para voltar ao overworld
│   └── DimensionEffectTicker.java  ← Reaplicar efeitos periodicamente
├── dimension/
│   ├── DimensionParams.java        ← Dados completos de uma dimensão
│   ├── DimensionRegistry.java      ← Registro dinâmico de dimensões
│   └── DynamicLevelHelper.java    ← Criação de ServerLevel em runtime
├── genes/
│   ├── ItemGenes.java              ← Estrutura de dados dos genes (16 valores)
│   ├── GeneTable.java              ← Tabela: item → genes (todos os itens vanilla)
│   └── GeneResolver.java          ← Soma genes → DimensionParams completo
├── worldgen/
│   ├── CustomChunkGenerator.java   ← Gerador procedural com simplex noise
│   └── SimplexNoise.java           ← Implementação pura de simplex noise
└── util/
    └── NameGenerator.java          ← Gera nomes místicos a partir da seed
```

---

## Compilando

```bash
# Requer Java 17 e Gradle
./gradlew build

# O .jar estará em: build/libs/infinite-dimensions-1.0.0.jar
# Copie para a pasta mods/ do Minecraft com Forge 1.20.1
```

---

## Notas técnicas

- **Dimensões são persistentes**: a mesma combinação sempre gera a mesma dimensão
- **Sem limite de dimensões**: cada combinação única é uma nova dimensão
- **Sem internet**: nenhuma chamada de API, tudo gerado localmente
- **Thread-safe**: criação de dimensões é feita no thread do servidor
- **Cache em memória**: dimensões já visitadas ficam em cache para acesso instantâneo

---

## Compatibilidade

- Minecraft: 1.20.1
- Forge: 47.4.x
- Java: 17+
- RAM recomendada: 4GB+
