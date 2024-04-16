package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotpotGameModEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().is(HotpotModEntry.IN_HOTPOT_DAMAGE_KEY) && event.getEntity() instanceof Player player) {
            Vec3 vec = event.getSource().getSourcePosition();

            if (vec != null) {
                LevelBlockPos pos = LevelBlockPos.fromVec3(event.getEntity().level(), vec);

                if (pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity) {
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, true), pos);
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, false), pos);
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, false), pos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        ItemStack itemStack = event.getItem();

        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            itemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (HotpotEffectHelper.hasEffects(itemStack)) {
            HotpotEffectHelper.listEffects(itemStack, mobEffectInstance -> event.getEntity().addEffect(mobEffectInstance));
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            itemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (HotpotEffectHelper.hasEffects(itemStack)) {
            PotionUtils.addPotionTooltip(HotpotEffectHelper.mergeEffects(HotpotEffectHelper.getListEffects(itemStack)), event.getToolTip(), 1.0f);
        }
    }
}
