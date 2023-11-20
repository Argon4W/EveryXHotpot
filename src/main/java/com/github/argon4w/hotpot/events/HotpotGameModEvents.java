package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotpotGameModEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().equals(HotpotModEntry.HOTPOT_DAMAGE_SOURCE) && event.getEntity() instanceof PlayerEntity) {
            Vector3d vec = event.getEntity().position();
            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (vec != null) {
                BlockPosWithLevel pos = BlockPosWithLevel.fromVec3(event.getEntity().level, vec);

                if (pos.getBlockEntity() instanceof HotpotBlockEntity) {
                    HotpotBlockEntity hotpotBlockEntity = (HotpotBlockEntity) pos.getBlockEntity();
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

        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            itemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (HotpotEffectHelper.hasEffects(itemStack)) {
            HotpotEffectHelper.listEffects(itemStack, mobEffectInstance -> ((LivingEntity) event.getEntity()).addEffect(mobEffectInstance));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            itemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
        }

        if (itemStack.isEmpty()) {
            return;
        }

        if (HotpotEffectHelper.hasEffects(itemStack)) {
            ItemStack copied = itemStack.copy();
            PotionUtils.setCustomEffects(copied, HotpotEffectHelper.mergeEffects(HotpotEffectHelper.getListEffects(itemStack)));
            PotionUtils.addPotionTooltip(copied, event.getToolTip(), 1.0f);
        }
    }
}
