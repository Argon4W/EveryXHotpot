package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.items.HotpotSpicePackItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSpicePackRecipe extends CustomRecipe {
    public HotpotSpicePackRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_) {
        super(p_252125_, p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> list = new ArrayList<>();

        return new SimpleRecipeMatcher(craftingContainer)
                .with(itemStack -> itemStack.is(ItemTags.SMALL_FLOWERS)).collect(list::add).atLeast(1)
                .with(itemStack -> matchSpicePackItem(itemStack, list.size())).once()
                .withRemaining().empty()
                .match();
    }

    private boolean matchSpicePackItem(ItemStack itemStack, int count) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            return false;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return count <= 4;
        }

        return HotpotSpicePackItem.getSpicePackItems(itemStack).size() + count <= 4;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return new SimpleRecipeAssembler(craftingContainer)
                .with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get()))
                .feed(this::assembleSpicePack)
                .assemble();
    }

    private void assembleSpicePack(ItemStack assembled, ItemStack ingredient) {
        HotpotSpicePackItem.addSpicePackItems(assembled, ingredient);
        HotpotSpicePackItem.setSpiceCharges(assembled, 20);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SPICE_PACK_SPECIAL_RECIPE.get();
    }
}
