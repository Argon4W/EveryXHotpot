package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.recipes.ingredients.actions.HotpotSoupConsumeAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.actions.HotpotSoupReplaceItemAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.conditions.HotpotSoupContentCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.conditions.HotpotSoupItemCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HotpotSoupIngredients {
    public static final ResourceKey<Registry<IHotpotSoupIngredientConditionSerializer<?>>> CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "condition"));
    public static final DeferredRegister<IHotpotSoupIngredientConditionSerializer<?>> CONDITIONS = DeferredRegister.create(CONDITION_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupIngredientConditionSerializer<?>> CONDITION_REGISTRY = CONDITIONS.makeRegistry(builder -> {});

    public static final ResourceKey<Registry<IHotpotSoupIngredientActionSerializer<?>>> ACTION_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "actions"));
    public static final DeferredRegister<IHotpotSoupIngredientActionSerializer<?>> ACTIONS = DeferredRegister.create(ACTION_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupIngredientActionSerializer<?>> ACTION_REGISTRY = ACTIONS.makeRegistry(builder -> {});

    public static final DeferredHolder<IHotpotSoupIngredientConditionSerializer<?>, HotpotSoupContentCondition.Serializer> CONTENT_CONDITION_SERIALIZER = CONDITIONS.register("content", HotpotSoupContentCondition.Serializer::new);
    public static final DeferredHolder<IHotpotSoupIngredientConditionSerializer<?>, HotpotSoupItemCondition.Serializer> ITEM_CONDITION_SERIALIZER = CONDITIONS.register("item", HotpotSoupItemCondition.Serializer::new);

    public static final DeferredHolder<IHotpotSoupIngredientActionSerializer<?>, HotpotSoupConsumeAction.Serializer> CONSUME_ACTION_SERIALIZER = ACTIONS.register("consume", HotpotSoupConsumeAction.Serializer::new);
    public static final DeferredHolder<IHotpotSoupIngredientActionSerializer<?>, HotpotSoupReplaceItemAction.Serializer> REPLACE_ACTION_SERIALIZER = ACTIONS.register("replace", HotpotSoupReplaceItemAction.Serializer::new);

    public static Registry<IHotpotSoupIngredientConditionSerializer<?>> getConditionRegistry() {
        return CONDITION_REGISTRY;
    }

    public static Registry<IHotpotSoupIngredientActionSerializer<?>> getActionRegistry() {
        return ACTION_REGISTRY;
    }

    public static IHotpotSoupIngredientConditionSerializer<?> getConditionSerializer(ResourceLocation resourceLocation) {
        return getConditionRegistry().get(resourceLocation);
    }

    public static IHotpotSoupIngredientActionSerializer<?> getActionSerializer(ResourceLocation resourceLocation) {
        return getActionRegistry().get(resourceLocation);
    }
}
