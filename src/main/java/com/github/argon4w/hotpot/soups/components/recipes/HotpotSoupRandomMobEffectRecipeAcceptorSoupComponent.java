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
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotDynamicMobEffectContainerSoupComponent;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRandomMobEffectRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class HotpotSoupRandomMobEffectRecipeAcceptorSoupComponent extends AbstractHotpotSoupComponent {

    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupRandomMobEffectRecipe> SOUP_RANDOM_MOB_EFFECT_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_RANDOM_MOB_EFFECT_RECIPE_TYPE.get());

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

        Optional<HotpotSoupRandomMobEffectRecipe> optional = SOUP_RANDOM_MOB_EFFECT_RECIPE_QUICK_CHECK
                .getRecipeFor(new HotpotRecipeInput(itemStack, hotpotBlockEntity.getSoup()), pos.level())
                .map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return result;
        }

        HotpotSoupRandomMobEffectRecipe recipe = optional.get();
        Optional<MobEffectInstance> mobEffect = recipe.getMobEffect();
        List<HotpotDynamicMobEffectContainerSoupComponent> containers = soup.getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER);

        if (mobEffect.isEmpty()) {
            return result;
        }

        ItemUtils.consumeAndReturnRemaining(player, itemStack, recipe.getRemainingItem());
        containers.forEach(component -> component.putScheduledEffect(mobEffect.get()));
        pos.playSound(recipe.getSoundEvent());

        return IHotpotResult.blocked();
    }

    @Override
    public void onEntityInside(
            Entity entity,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack itemStack = itemEntity.getItem();

        if (itemStack.isEmpty()) {
            return;
        }

        Optional<HotpotSoupRandomMobEffectRecipe> optional = SOUP_RANDOM_MOB_EFFECT_RECIPE_QUICK_CHECK
                .getRecipeFor(new HotpotRecipeInput(itemStack, hotpotBlockEntity.getSoup()), pos.level())
                .map(RecipeHolder::value);

        if (optional.isEmpty()) {
            return;
        }

        HotpotSoupRandomMobEffectRecipe recipe = optional.get();
        List<HotpotDynamicMobEffectContainerSoupComponent> containers = soup.getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER);

        for (int i = 0; i < itemStack.getCount(); i++) {
            containers.forEach(component -> recipe.getMobEffect().ifPresent(component::putScheduledEffect));

            pos.playSound(recipe.getSoundEvent());
            pos.dropItemStack(recipe.getRemainingItem());
        }

        itemEntity.setItem(ItemStack.EMPTY);
        itemEntity.discard();
    }
}
