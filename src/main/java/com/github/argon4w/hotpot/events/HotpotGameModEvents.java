package com.github.argon4w.hotpot.events;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.items.IHotpotItemContainer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.GAME)
public class HotpotGameModEvents {

    @SubscribeEvent
    public static void onLivingBlock(LivingShieldBlockEvent event) {
        if (!(event.getDamageContainer().getSource().getDirectEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!livingEntity.hasEffect(HotpotModEntry.HOTPOT_CRISPY)) {
            return;
        }

        event.setBlocked(false);
    }

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

        hotpotBlockEntity.setContentFromNeighbors(pos, () -> new HotpotPlayerContent(profile, true));
        hotpotBlockEntity.setContentFromNeighbors(pos, () -> new HotpotPlayerContent(profile, false));
        hotpotBlockEntity.setContentFromNeighbors(pos, () -> new HotpotPlayerContent(profile, false));
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
    public static void addEffectTooltip(ItemTooltipEvent event) {
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

        List<MobEffectInstance> effects = HotpotFoodEffectsDataComponent.getEffects(itemStack);

        if (effects.isEmpty()) {
            return;
        }

        if (!event.getFlags().hasControlDown()) {
            event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.effects.collapsed").withStyle(ChatFormatting.GRAY));
            return;
        }

        event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.effects").withStyle(ChatFormatting.GRAY));
        PotionContents.addPotionTooltip(effects, component -> event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.line.2", component).withStyle(ChatFormatting.GRAY)), 1.0f, context.tickRate());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addContainerTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        if (itemStack.isEmpty()) {
            return;
        }

        if (!(itemStack.getItem() instanceof IHotpotItemContainer container)) {
            return;
        }

        List<ItemStack> itemStacks = container.getAllContainedItemStacks(itemStack);

        if (itemStacks.isEmpty()) {
            return;
        }

        if (!event.getFlags().hasShiftDown()) {
            event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.contains.collapsed").withStyle(ChatFormatting.GRAY));
            return;
        }

        event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.contains").withStyle(ChatFormatting.GRAY));
        itemStacks.forEach(itemStack1 -> event.getToolTip().add(Component.translatable("item.everyxhotpot.tooltip.line.1", itemStack1.getDisplayName()).withStyle(ChatFormatting.GRAY)));
    }
}
