package com.daniel366cobra.musketeer.init;

import com.daniel366cobra.musketeer.util.NBTEnabledSmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.musketeer.Musketeer.MOD_ID;
import static net.fabricmc.fabric.impl.transfer.TransferApiImpl.LOGGER;

public class ModCraftingRecipes {
    public static void register() {
        LOGGER.info("Registering crafting recipes");
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "smithing"), new NBTEnabledSmithingRecipe.Serializer());
    }
}
