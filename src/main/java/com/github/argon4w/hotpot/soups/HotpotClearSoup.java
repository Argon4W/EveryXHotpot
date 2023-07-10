package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class HotpotClearSoup extends AbstractHotpotSoup {
    private float nutrition = 0.0f;

    @Override
    public void load(CompoundTag compoundTag) {
        setInternalWaterLevel(compoundTag.getFloat("WaterLevel"));
        nutrition = Math.max(0f, Math.min(1f, compoundTag.getFloat("Nutrition")));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putFloat("WaterLevel", getInternalWaterLevel());
        compoundTag.putFloat("Nutrition", nutrition);

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("WaterLevel", Tag.TAG_FLOAT) && compoundTag.contains("Nutrition", Tag.TAG_FLOAT);
    }

    @Override
    public String getID() {
        return "ClearSoup";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (itemStack.is(Items.WATER_BUCKET)) {
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + 1.0f);
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));

            return Optional.empty();
        }

        return super.interact(hitSection, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (content instanceof HotpotItemStackContent itemStackContent) {
            FoodProperties properties = itemStackContent.getAssembledContent(hotpotBlockEntity, pos).getFoodProperties(null);

            if (properties != null) {
                nutrition = Math.min(1.0f, nutrition + properties.getNutrition() * 0.01f);
            }
        }

        return super.remapContent(content, hotpotBlockEntity, pos);
    }

    @Override
    public float getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0.1f + getInternalWaterLevel() * 1.9f + nutrition * 2f;
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        nutrition = Math.max(0f, nutrition - 0.02f / 20f / 60f);
        setInternalWaterLevel(Math.max(0f, getWaterLevel(hotpotBlockEntity, pos) - 0.5f / 20f / 60f));
    }

    @Override
    public ResourceLocation getBubbleResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "effect/hotpot_clear_soup_bubble");
    }

    @Override
    public ResourceLocation getSoupResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_clear_soup");
    }
}
