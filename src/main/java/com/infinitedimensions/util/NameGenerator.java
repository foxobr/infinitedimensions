package com.infinitedimensions.util;

import java.util.Random;

/**
 * NameGenerator — gera nomes de dimensões místicos e únicos
 * baseados na seed e no gene dominante da combinação.
 *
 * Cada gene tem seu próprio conjunto de sílabas que reflete
 * a "personalidade" da dimensão.
 */
public class NameGenerator {

    // Prefixos por gene dominante
    private static final String[][] PREFIXES = {
        // heat
        {"Ignar","Pyron","Khal","Vulcar","Braz","Solum","Ember"},
        // cold
        {"Glaciel","Cryon","Aelan","Frost","Niveth","Blizar","Keld"},
        // dark
        {"Morven","Umbra","Nyxar","Shadul","Groth","Teneb","Void"},
        // light
        {"Lumos","Soleth","Auren","Phael","Radia","Claros","Solen"},
        // poison
        {"Venar","Toxar","Viper","Morbus","Plagan","Velen","Blight"},
        // undead
        {"Nekral","Mort","Ossian","Wither","Diran","Coreph","Burial"},
        // crystal
        {"Crystan","Gemiel","Prism","Faceth","Lapis","Kvart","Shard"},
        // wetness
        {"Aquan","Tidel","Maren","Pelath","Naut","Coran","Delphin"},
        // bouncy
        {"Spron","Elasit","Kinetho","Bounc","Jolen","Vibral","Sphal"},
        // hostile
        {"Wraith","Malex","Dread","Voran","Khaos","Cruel","Mordath"},
        // stable
        {"Lythir","Solith","Firmun","Basalt","Anchur","Gravin","Rauth"},
        // void
        {"Nihil","Varan","Abyss","Oblex","Sculkar","Echos","Dim"},
        // sky
        {"Aeron","Celeth","Nimbus","Straton","Cirron","Altis","Volan"},
        // nature
        {"Florin","Sylvan","Virid","Mossen","Fernis","Boscal","Verdur"},
        // chaos
        {"Entroph","Kadon","Churn","Malix","Fraxis","Hazal","Ruptur"},
        // magic
        {"Arcen","Mystra","Enchel","Rune","Aethir","Glamis","Sortil"},
    };

    private static final String[][] MIDDLES = {
        {"an","ir","os","ul","en","ak","eth"},     // heat
        {"ia","ys","el","an","ar","in","ix"},       // cold
        {"um","ash","or","ar","an","em","ith"},     // dark
        {"us","ae","ix","ol","ar","el","is"},       // light
        {"ex","on","ak","ir","us","em","ul"},       // poison
        {"is","al","en","or","am","eth","ir"},      // undead
        {"al","in","ur","as","il","en","om"},       // crystal
        {"ar","el","in","os","ul","an","ith"},      // wetness
        {"ip","al","on","ur","ix","en","am"},       // bouncy
        {"ath","or","em","ax","ul","ir","on"},      // hostile
        {"ur","el","an","om","is","eth","al"},      // stable
        {"il","om","ar","eth","un","ix","al"},      // void
        {"in","el","ar","us","on","ith","ax"},      // sky
        {"en","or","al","is","um","ir","eth"},      // nature
        {"ax","or","um","al","ix","en","ar"},       // chaos
        {"an","ir","el","us","om","ith","al"},      // magic
    };

    private static final String[][] SUFFIXES = {
        {"dor","thax","uum","vel","ark","orn","ash"},    // heat
        {"iel","wyn","fros","thar","iss","eld","ian"},   // cold
        {"ath","bane","grim","nar","sha","vel","thul"},  // dark
        {"sun","dawn","iel","ael","far","eon","lux"},    // light
        {"vex","fang","tox","rax","blight","ven","nil"}, // poison
        {"tomb","ris","mort","bone","ash","eld","wail"}, // undead
        {"gem","prism","vite","ite","spar","flect","tal"},// crystal
        {"mare","tide","deep","flow","mist","ven","reef"},// wetness
        {"spring","flip","bounce","hop","coil","zap","pop"},// bouncy
        {"rage","wrath","dread","fear","bane","scorn","doom"},// hostile
        {"hold","burg","mark","mont","stone","fort","wall"},// stable
        {"null","deep","void","dark","less","gone","far"}, // void
        {"sky","peak","cloud","high","wind","gust","soar"},// sky
        {"wood","moss","bloom","grove","fen","leaf","bud"},// nature
        {"rupt","flux","spin","burst","snap","riot","storm"},// chaos
        {"spell","rune","weave","cast","charm","ward","hex"},// magic
    };

    private static final int[] GENE_INDEX = buildGeneIndex();

    private static int[] buildGeneIndex() {
        // Mapeia nome do gene para índice das arrays acima
        return new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    }

    private static final String[] GENE_NAMES = {
        "heat","cold","dark","light","poison","undead",
        "crystal","wetness","bouncy","hostile","stable",
        "void","sky","nature","chaos","magic"
    };

    public static String generate(long seed, String dominantGene) {
        Random rng = new Random(seed);

        // Encontra índice do gene dominante
        int geneIdx = 0;
        for (int i = 0; i < GENE_NAMES.length; i++) {
            if (GENE_NAMES[i].equals(dominantGene)) {
                geneIdx = i;
                break;
            }
        }

        // Seleciona sílabas do pool do gene dominante
        String prefix = PREFIXES[geneIdx][rng.nextInt(PREFIXES[geneIdx].length)];
        String mid    = MIDDLES[geneIdx][rng.nextInt(MIDDLES[geneIdx].length)];
        String suffix = SUFFIXES[geneIdx][rng.nextInt(SUFFIXES[geneIdx].length)];

        // Variação: às vezes pula o meio
        String name = rng.nextInt(3) == 0
            ? capitalize(prefix) + suffix
            : capitalize(prefix) + mid + suffix;

        // Adiciona artigo dimensional
        String[] articles = {"o Plano de ", "a Dimensão de ", "o Reino de ", "o Domínio de ", ""};
        String article = articles[rng.nextInt(articles.length)];

        return article + capitalize(name);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
