package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.recipes.ingredients.actions.HotpotSoupConsumeAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.actions.HotpotSoupReplaceItemAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.conditions.HotpotSoupContentCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.conditions.HotpotSoupItemCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotSoupIngredients {
    public static final ResourceKey<Registry<IHotpotSoupIngredientConditionSerializer<?>>> CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "condition"));
    public static final DeferredRegister<IHotpotSoupIngredientConditionSerializer<?>> CONDITIONS = DeferredRegister.create(CONDITION_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotSoupIngredientConditionSerializer<?>>> CONDITION_REGISTRY = CONDITIONS.makeRegistry(RegistryBuilder::new);

    public static final ResourceKey<Registry<IHotpotSoupIngredientActionSerializer<?>>> ACTION_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "actions"));
    public static final DeferredRegister<IHotpotSoupIngredientActionSerializer<?>> ACTIONS = DeferredRegister.create(ACTION_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotSoupIngredientActionSerializer<?>>> ACTION_REGISTRY = ACTIONS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<HotpotSoupContentCondition.Serializer> CONTENT_CONDITION_SERIALIZER = CONDITIONS.register("content", HotpotSoupContentCondition.Serializer::new);
    public static final RegistryObject<HotpotSoupItemCondition.Serializer> ITEM_CONDITION_SERIALIZER = CONDITIONS.register("item", HotpotSoupItemCondition.Serializer::new);

    public static final RegistryObject<HotpotSoupConsumeAction.Serializer> CONSUME_ACTION_SERIALIZER = ACTIONS.register("consume", HotpotSoupConsumeAction.Serializer::new);
    public static final RegistryObject<HotpotSoupReplaceItemAction.Serializer> REPLACE_ACTION_SERIALIZER = ACTIONS.register("replace", HotpotSoupReplaceItemAction.Serializer::new);

    public static IForgeRegistry<IHotpotSoupIngredientConditionSerializer<?>> getConditionRegistry() {
        return CONDITION_REGISTRY.get();
    }

    public static IForgeRegistry<IHotpotSoupIngredientActionSerializer<?>> getActionRegistry() {
        return ACTION_REGISTRY.get();
    }

    public static IHotpotSoupIngredientConditionSerializer<?> getConditionSerializer(ResourceLocation resourceLocation) {
        return getConditionRegistry().getValue(resourceLocation);
    }

    public static IHotpotSoupIngredientActionSerializer<?> getActionSerializer(ResourceLocation resourceLocation) {
        return getActionRegistry().getValue(resourceLocation);
    }
}
