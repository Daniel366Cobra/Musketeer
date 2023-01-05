package com.daniel366cobra.musketeer.init;


import com.daniel366cobra.musketeer.entity.projectile.MusketProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.musketeer.Musketeer.LOGGER;
import static com.daniel366cobra.musketeer.Musketeer.MOD_ID;

public class ModEntities {

    // Entities declaration
    public static final EntityType<MusketProjectileEntity> MUSKET_PROJECTILE = FabricEntityTypeBuilder.<MusketProjectileEntity>create(SpawnGroup.MISC, MusketProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.4f)).trackedUpdateRate(10).trackRangeBlocks(20).forceTrackedVelocityUpdates(true).build();

    public static void register() {
        LOGGER.info("Registering entities");
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "musket_projectile"), MUSKET_PROJECTILE);
    }

}
