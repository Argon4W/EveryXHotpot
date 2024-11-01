package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.hotpot.HotpotItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class HotpotSoupBaseRecipeAcceptorSoupComponent extends AbstractHotpotSoupComponent {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupBaseRecipe>
            SOUP_BASE_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_TYPE.get());

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            int position,
            Player player,
            InteractionHand hand,
            ItemStack itemStack,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity) {
        if (result.isPresent()) {
            return result;
        }

        Optional<HotpotSoupBaseRecipe> optional = SOUP_BASE_RECIPE_QUICK_CHECK
                .getRecipeFor(new HotpotRecipeInput(itemStack, soup), pos.level())
                .map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return result;
        }

        HotpotSoupBaseRecipe recipe = optional.get();

        HotpotItemUtils.consumeAndReturnRemaining(player, itemStack, recipe.getRemainingItem());
        hotpotBlockEntity.setSoup(recipe.getResultSoup(pos.registryAccess()), pos);
        hotpotBlockEntity.setWaterLevel(recipe.getResultWaterLevel(), pos);
        pos.playSound(recipe.getSoundEvent());

        return IHotpotResult.blocked();
    }
}
