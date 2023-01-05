package com.daniel366cobra.musketeer.client;

import com.daniel366cobra.musketeer.client.render.entity.MusketProjectileEntityRenderer;
import com.daniel366cobra.musketeer.init.ModEntities;
import com.daniel366cobra.musketeer.init.ModItems;
import com.daniel366cobra.musketeer.item.MusketItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

@Environment(EnvType.CLIENT)
public class MusketeerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * Registers our Entity renderers, which provides a model and texture for the entity.
         */

        EntityRendererRegistry.register(ModEntities.MUSKET_PROJECTILE, MusketProjectileEntityRenderer::new);

        /*
         * Register model override predicates for the Musket item loading process.
         */
        ModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("load_progress"), (itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });

        ModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("loading"),(itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : 1.0F;
        });

        ModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("loaded"), (itemStack, clientWorld, livingEntity, intValue) -> MusketItem.isLoaded(itemStack) ? 1.0F : 0.0F);

    }
}
