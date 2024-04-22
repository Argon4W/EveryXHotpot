package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSkewerRecipe extends CustomRecipe {
    public HotpotSkewerRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_) {
        super(p_252125_, p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> list = new ArrayList<>();

        return new SimpleRecipeMatcher(craftingContainer)
                .with(ItemStack::isEdible).collect(list::add).atLeast(1)
                .with(itemStack -> matchSkewerItem(itemStack, list.size())).once()
                .withRemaining().empty()
                .match();
    }

    private boolean matchSkewerItem(ItemStack itemStack, int count) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SKEWER.get())) {
            return false;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return count <= 3;
        }

        return HotpotSkewerItem.getSkewerItems(itemStack).size() + count <= 3;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return new SimpleRecipeAssembler(craftingContainer)
                .with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SKEWER.get()))
                .feed(this::assembleSkewerItem)
                .assemble();
    }

    private void assembleSkewerItem(ItemStack assembled, ItemStack ingredient) {
        HotpotSkewerItem.addSkewerItems(assembled, ingredient.copyWithCount(1));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SKEWER_SPECIAL_RECIPE.get();
    }
}
