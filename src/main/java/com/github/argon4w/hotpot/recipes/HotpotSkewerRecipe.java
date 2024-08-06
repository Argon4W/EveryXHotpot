package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSkewerRecipe extends CustomRecipe {
    public HotpotSkewerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> list = new ArrayList<>();
        return new SimpleRecipeMatcher(input).with(this::isFood).collect(list::add).atLeast(1).with(itemStack -> matchSkewerItem(itemStack, list.size())).once().withRemaining().empty().match();
    }

    private boolean matchSkewerItem(ItemStack itemStack, int count) {
        return itemStack.is(HotpotModEntry.HOTPOT_SKEWER) && HotpotSkewerItem.getSkewerItems(itemStack).size() + count <= 3;
    }

    private boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD) && !itemStack.hasCraftingRemainingItem();
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return new SimpleRecipeAssembler(input).with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SKEWER)).feed(this::assembleSkewerItem).assemble();
    }

    private ItemStack assembleSkewerItem(ItemStack assembled, ItemStack ingredient) {
        HotpotSkewerItem.addSkewerItems(assembled, ingredient.copyWithCount(1));
        return assembled;
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
