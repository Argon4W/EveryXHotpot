package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentUnitType;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class HotpotSoupBaseRecipeAcceptorSoupComponent extends AbstractHotpotSoupComponent {
    public static final Supplier<RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupBaseRecipe>> SOUP_BASE_RECIPE_QUICK_CHECK = Suppliers.memoize(() -> RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_TYPE.get()));

    public static final HotpotSoupBaseRecipeAcceptorSoupComponent UNIT = new HotpotSoupBaseRecipeAcceptorSoupComponent();
    public static final HotpotSoupComponentUnitType<HotpotSoupBaseRecipeAcceptorSoupComponent> TYPE = new HotpotSoupComponentUnitType<>(UNIT, HotpotSoupComponentTypeSerializers.SOUP_BASE_RECIPE_ACCEPTOR_SOUP_COMPONENT_TYPE_SERIALIZER);

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        if (result.isPresent()) {
            return result;
        }

        Optional<HotpotSoupBaseRecipe> optional = SOUP_BASE_RECIPE_QUICK_CHECK.get().getRecipeFor(new HotpotRecipeInput(itemStack, soup), pos.level()).map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return result;
        }

        HotpotSoupBaseRecipe recipe = optional.get();

        player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));
        hotpotBlockEntity.setSoup(recipe.getResultSoup(), pos);
        hotpotBlockEntity.setWaterLevel(recipe.getResultWaterLevel(), pos);
        pos.playSound(recipe.getSoundEvent());

        return IHotpotResult.blocked();
    }
}
