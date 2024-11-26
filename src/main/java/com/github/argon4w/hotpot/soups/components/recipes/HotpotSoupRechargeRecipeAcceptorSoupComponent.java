package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.fancytoys.items.ItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class HotpotSoupRechargeRecipeAcceptorSoupComponent extends AbstractHotpotSoupComponent {

    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupRechargeRecipe> SOUP_RECHARGE_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get());

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup) {
        Player player = context.player();
        LevelBlockPos pos = context.pos();

        if (result.isPresent()) {
            return result;
        }

        Optional<HotpotSoupRechargeRecipe> optional = SOUP_RECHARGE_RECIPE_QUICK_CHECK
                .getRecipeFor(new HotpotRecipeInput(itemStack, hotpotBlockEntity.getSoup()), pos.level())
                .map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return result;
        }

        HotpotSoupRechargeRecipe recipe = optional.get();

        ItemUtils.consumeAndReturnRemaining(player, itemStack, recipe.getRemainingItem());
        soup.setWaterLevelWithOverflow(soup.getWaterLevel() + recipe.getRechargeWaterLevel(), hotpotBlockEntity, pos);
        pos.playSound(recipe.getSoundEvent());

        return IHotpotResult.blocked();
    }
}
