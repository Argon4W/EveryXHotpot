package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class HotpotSoupRechargeRecipeAcceptorSoupComponent extends AbstractHotpotSoupComponent {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupRechargeRecipe> SOUP_RECHARGE_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get());

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        if (result.isPresent()) {
            return result;
        }

        Optional<HotpotSoupRechargeRecipe> optional = SOUP_RECHARGE_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotRecipeInput(itemStack, hotpotBlockEntity.getSoup()), pos.level()).map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return result;
        }

        HotpotSoupRechargeRecipe recipe = optional.get();

        soup.setWaterLevel(soup.getWaterLevel() + recipe.getRechargeWaterLevel(), hotpotBlockEntity, pos);
        player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));
        pos.playSound(recipe.getSoundEvent());

        return IHotpotResult.blocked();
    }
}
