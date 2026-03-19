package com.infiniti.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// ─────────────────────────────────────────────────────────────
// DuneMouseEntity — rato de areia passivo, guia de exploração
// ─────────────────────────────────────────────────────────────
class DuneMouseEntity extends Animal {
    public DuneMouseEntity(EntityType<? extends Animal> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5); // muito rápido
    }

    @Override
    public ItemStack getBreedOffspring(net.minecraft.server.level.ServerLevel l,
                                       AgeableMob a) { return ItemStack.EMPTY; }
}

// ─────────────────────────────────────────────────────────────
// SandBeetleEntity — besouro escaravelho, leva ao SpiceOre
// ─────────────────────────────────────────────────────────────
class SandBeetleEntity extends Animal {
    public SandBeetleEntity(EntityType<? extends Animal> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public ItemStack getBreedOffspring(net.minecraft.server.level.ServerLevel l,
                                       AgeableMob a) { return ItemStack.EMPTY; }
}

// ─────────────────────────────────────────────────────────────
// DuneHawkEntity — falcão do deserto, voa sobre estruturas
// ─────────────────────────────────────────────────────────────
class DuneHawkEntity extends Animal {
    public DuneHawkEntity(EntityType<? extends Animal> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6);
    }

    @Override
    public ItemStack getBreedOffspring(net.minecraft.server.level.ServerLevel l,
                                       AgeableMob a) { return ItemStack.EMPTY; }
}

// ─────────────────────────────────────────────────────────────
// SandCamelEntity — camelo montável, imune ao SandLeviathan
// ─────────────────────────────────────────────────────────────
class SandCamelEntity extends Animal {
    public SandCamelEntity(EntityType<? extends Animal> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public ItemStack getBreedOffspring(net.minecraft.server.level.ServerLevel l,
                                       AgeableMob a) { return ItemStack.EMPTY; }
}

// ─────────────────────────────────────────────────────────────
// DuneScavengerEntity — nômade neutro, troca informações
// ─────────────────────────────────────────────────────────────
class DuneScavengerEntity extends PathfinderMob {
    public DuneScavengerEntity(EntityType<? extends PathfinderMob> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 5.0);
    }
}

// ─────────────────────────────────────────────────────────────
// SandShadeEntity — fantasma das tempestades, cega o jogador
// ─────────────────────────────────────────────────────────────
class SandShadeEntity extends Monster {
    public SandShadeEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }
}

// ─────────────────────────────────────────────────────────────
// Classe pública de referência (Java exige uma classe pública por arquivo)
// ─────────────────────────────────────────────────────────────
public class EntityStubs {
    // Este arquivo contém os stubs de todas as entidades menores.
    // Cada uma deve ter seu próprio arquivo em produção.
    // Aqui reunidas para compacidade do projeto base.
}
