package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotContents {
    public static final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> CAMPFIRE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    public static final RecipeManager.CachedCheck<Container, BlastingRecipe> BLAST_FURNACE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.BLASTING);

    public static final ResourceLocation EMPTY_CONTENT_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_content");

    public static final ResourceKey<Registry<HotpotContentType<?>>> CONTENT_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "content"));
    public static final DeferredRegister<HotpotContentType<?>> CONTENTS = DeferredRegister.create(CONTENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<HotpotContentType<?>>> CONTENT_REGISTRY = CONTENTS.makeRegistry(() -> new RegistryBuilder<HotpotContentType<?>>().setDefaultKey(EMPTY_CONTENT_LOCATION));

    public static final RegistryObject<HotpotContentType<HotpotCampfireRecipeContent>> CAMPFIRE_RECIPE_CONTENT = CONTENTS.register("campfire_recipe_content", () -> HotpotCampfireRecipeContent::new);
    public static final RegistryObject<HotpotContentType<HotpotBlastFurnaceRecipeContent>> BLASTING_RECIPE_CONTENT = CONTENTS.register("blasting_recipe_content", () -> HotpotBlastFurnaceRecipeContent::new);
    public static final RegistryObject<HotpotContentType<HotpotPlayerContent>> PLAYER_CONTENT = CONTENTS.register("player_content", () -> HotpotPlayerContent::new);
    public static final RegistryObject<HotpotContentType<HotpotEmptyContent>> EMPTY_CONTENT = CONTENTS.register("empty_content", () -> HotpotEmptyContent::new);

    public static HotpotContentType<HotpotEmptyContent> getEmptyContent() {
        return () -> EMPTY_CONTENT.get().createContent();
    }

    public static IForgeRegistry<HotpotContentType<?>> getContentRegistry() {
        return CONTENT_REGISTRY.get();
    }
}
