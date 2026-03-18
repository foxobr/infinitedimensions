package com.infinitedimensions.genes;

/**
 * Representa os 16 genes dimensionais de um item ou combinação.
 *
 * Índices:
 *  0=heat, 1=cold, 2=dark, 3=light, 4=poison, 5=undead,
 *  6=crystal, 7=wetness, 8=bouncy, 9=hostile, 10=stable,
 *  11=void, 12=sky, 13=nature, 14=chaos, 15=magic
 */
public class ItemGenes {

    public static final int GENE_COUNT = 16;
    public static final String[] NAMES = {
        "heat","cold","dark","light","poison","undead",
        "crystal","wetness","bouncy","hostile","stable",
        "void","sky","nature","chaos","magic"
    };

    public static final ItemGenes EMPTY = new ItemGenes(
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
    );

    private final int[] values;

    public ItemGenes(int heat, int cold, int dark, int light,
                     int poison, int undead, int crystal, int wetness,
                     int bouncy, int hostile, int stable, int voidG,
                     int sky, int nature, int chaos, int magic) {
        this.values = new int[]{
            heat, cold, dark, light, poison, undead,
            crystal, wetness, bouncy, hostile, stable, voidG,
            sky, nature, chaos, magic
        };
    }

    /** Constrói a partir de pares (índice, valor) — ex: of(new int[]{0,3}, new int[]{4,2}) */
    public static ItemGenes of(int[]... pairs) {
        int[] v = new int[GENE_COUNT];
        for (int[] pair : pairs) {
            v[pair[0]] += pair[1];
        }
        return fromArray(v);
    }

    private static ItemGenes fromArray(int[] v) {
        return new ItemGenes(
            v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],
            v[8],v[9],v[10],v[11],v[12],v[13],v[14],v[15]
        );
    }

    public int get(int index) {
        return values[index];
    }

    public int get(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) return values[i];
        }
        return 0;
    }

    /** Soma este gene com outro e retorna o resultado. */
    public ItemGenes add(ItemGenes other) {
        int[] r = new int[GENE_COUNT];
        for (int i = 0; i < GENE_COUNT; i++) {
            r[i] = this.values[i] + other.values[i];
        }
        return fromArray(r);
    }

    /** Retorna o gene dominante (maior valor absoluto). */
    public String dominantGeneName() {
        int maxIdx = 0;
        for (int i = 1; i < GENE_COUNT; i++) {
            if (Math.abs(values[i]) > Math.abs(values[maxIdx])) maxIdx = i;
        }
        return NAMES[maxIdx];
    }

    public int[] toArray() {
        return values.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ItemGenes{");
        for (int i = 0; i < GENE_COUNT; i++) {
            if (values[i] != 0) {
                sb.append(NAMES[i]).append("=").append(values[i]).append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
