package com.daniel366cobra.musketeer.util;

import com.daniel366cobra.musketeer.mixin.SmithingRecipeAccessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

/**
 * NBT data-enabled smithing recipe. Allows setting NBT data of items produced by smithing.
 * This code was sourced from CammiePone's Hookshot mod.
 */
public class NBTEnabledSmithingRecipe extends SmithingRecipe {

    public NBTEnabledSmithingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
    }

    @Override
    public ItemStack craft(Inventory inv)
    {
        ItemStack stack = ((SmithingRecipeAccessor) this).getResult().copy();
        NbtCompound tag = inv.getStack(0).getNbt();

        if(tag != null)
            stack.getOrCreateNbt().copyFrom(tag);

        return stack;
    }

    public static ItemStack getItemStack(JsonObject json)
    {
        String string = JsonHelper.getString(json, "item");

        Item item = Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() ->
                new JsonSyntaxException("Unknown item '" + string + "'"));

        if(json.has("data"))
        {
            throw new JsonParseException("Disallowed data tag found");
        }
        else
        {
            int count = JsonHelper.getInt(json, "count", 1);
            String nbt = JsonHelper.getString(json, "nbt");
            ItemStack stack = new ItemStack(item, count);

            stack.getOrCreateNbt().putBoolean(nbt, true);

            return stack;
        }
    }

    public static class Serializer implements RecipeSerializer<NBTEnabledSmithingRecipe>
    {
        @Override
        public NBTEnabledSmithingRecipe read(Identifier identifier, JsonObject jsonObject)
        {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack result = NBTEnabledSmithingRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));

            return new NBTEnabledSmithingRecipe(identifier, base, addition, result);
        }

        @Override
        public NBTEnabledSmithingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf)
        {
            Ingredient base = Ingredient.fromPacket(packetByteBuf);
            Ingredient addition = Ingredient.fromPacket(packetByteBuf);
            ItemStack result = packetByteBuf.readItemStack();

            return new NBTEnabledSmithingRecipe(identifier, base, addition, result);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, NBTEnabledSmithingRecipe smithingRecipe)
        {
            ((SmithingRecipeAccessor) smithingRecipe).getBase().write(packetByteBuf);
            ((SmithingRecipeAccessor) smithingRecipe).getAddition().write(packetByteBuf);
            packetByteBuf.writeItemStack(((SmithingRecipeAccessor) smithingRecipe).getResult());
        }
    }

}
