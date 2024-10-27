package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import net.minecraft.Util;
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
import java.util.function.Predicate;

public class HotpotSkewerRecipe extends CustomRecipe {
    public HotpotSkewerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return new SimpleRecipeMatcher(input).with(this::isFood).count().atLeast(1).with(this::matchSkewerItem).once().withRemaining().empty().match();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return new SimpleRecipeAssembler(input).basedOn(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SKEWER)).filter(Predicate.not(ItemStack::isEmpty)).feed(this::assembleSkewer).assemble();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SKEWER_SPECIAL_RECIPE.get();
    }

    private ItemStack assembleSkewer(ItemStack assembled, ItemStack ingredient) {
        return Util.make(assembled, assembled1 -> HotpotSkewerItem.addSkewerItems(assembled1, ingredient.copyWithCount(1)));
    }

    private boolean matchSkewerItem(ItemStack itemStack, int count) {
        return itemStack.is(HotpotModEntry.HOTPOT_SKEWER) && HotpotSkewerItem.getSkewerItems(itemStack).size() + count <= 3;
    }

    private boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD) && !itemStack.hasCraftingRemainingItem();
    }
}
