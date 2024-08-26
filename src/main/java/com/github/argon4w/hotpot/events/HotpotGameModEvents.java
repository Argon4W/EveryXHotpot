package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.items.IHotpotItemContainer;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.GAME)
public class HotpotGameModEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getSource().is(HotpotModEntry.IN_HOTPOT_DAMAGE_KEY)) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Vec3 vec = event.getSource().getSourcePosition();

        if (vec == null) {
            return;
        }

        LevelBlockPos pos = LevelBlockPos.fromVec3(event.getEntity().level(), vec);

        if (!(pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return;
        }

        ResolvableProfile profile = new ResolvableProfile(player.getGameProfile());

        hotpotBlockEntity.setContentWhenEmpty(0, () -> new HotpotPlayerContent(profile, true), pos);
        hotpotBlockEntity.setContentWhenEmpty(0, () -> new HotpotPlayerContent(profile, false), pos);
        hotpotBlockEntity.setContentWhenEmpty(0, () -> new HotpotPlayerContent(profile, false), pos);
    }

    @SubscribeEvent
    public static void onLivingFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        ItemStack itemStack = event.getItem();
        LivingEntity livingEntity = event.getEntity();

        if (itemStack.getItem() instanceof IHotpotItemContainer container) {
            itemStack = container.getContainedItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (!HotpotFoodEffectsDataComponent.hasEffects(itemStack)) {
            return;
        }

        HotpotFoodEffectsDataComponent.getEffects(itemStack).forEach(livingEntity::addEffect);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item.TooltipContext context = event.getContext();

        if (itemStack.getItem() instanceof IHotpotItemContainer container) {
            itemStack = container.getContainedItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (!HotpotFoodEffectsDataComponent.hasDataComponent(itemStack)) {
            return;
        }

        PotionContents.addPotionTooltip(HotpotFoodEffectsDataComponent.getEffects(itemStack), event.getToolTip()::add, 1.0f, context.tickRate());
    }
}
