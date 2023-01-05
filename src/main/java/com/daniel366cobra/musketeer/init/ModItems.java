package com.daniel366cobra.musketeer.init;

import com.daniel366cobra.musketeer.item.MusketBallItem;
import com.daniel366cobra.musketeer.item.MusketItem;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.musketeer.Musketeer.LOGGER;
import static com.daniel366cobra.musketeer.Musketeer.MOD_ID;

public class ModItems {

    // Items declaration
    public static ToolItem WOODEN_KNIFE = new SwordItem(ToolMaterials.WOOD, 1, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));
    public static ToolItem STONE_KNIFE = new SwordItem(ToolMaterials.STONE, 1, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));
    public static ToolItem IRON_KNIFE = new SwordItem(ToolMaterials.IRON, 2, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));
    public static ToolItem GOLDEN_KNIFE = new SwordItem(ToolMaterials.GOLD, 2, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));
    public static ToolItem DIAMOND_KNIFE = new SwordItem(ToolMaterials.DIAMOND, 2, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));
    public static ToolItem NETHERITE_KNIFE = new SwordItem(ToolMaterials.NETHERITE, 2, -0.75f, new Item.Settings().group(ItemGroup.COMBAT));


    public static MusketItem FLINTLOCK_MUSKET = new MusketItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(250));

    public static Item MUSKET_BALL = new MusketBallItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(64));
   
    public static void register() {
        // Registering items
        LOGGER.info("Registering items");

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "wooden_knife"), WOODEN_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "stone_knife"), STONE_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_knife"), IRON_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "golden_knife"), GOLDEN_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "diamond_knife"), DIAMOND_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "netherite_knife"), NETHERITE_KNIFE);

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "musket_ball"), MUSKET_BALL);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID,"flintlock_musket"), FLINTLOCK_MUSKET);
    }

}
