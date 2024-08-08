package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.items.components.HotpotSpriteProcessorConfigDataComponent;
import com.github.argon4w.hotpot.items.process.IHotpotSpriteProcessorConfig;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
    @Inject(method = "getColor", at = @At("RETURN"), cancellable = true)
    public void getColor(ItemStack pStack, int pTintIndex, CallbackInfoReturnable<Integer> cir) {

    }

    @Unique
    public int everyxhotpot$getProcessedColor(IHotpotSpriteProcessorConfig config) {
        return HotpotSpriteProcessors.getSpriteProcessor(config.getProcessorResourceLocation()).getColor(config).toInt();
    }

    @Unique
    public IHotpotSpriteProcessorConfig everyxhotpot$getProcessorConfig(ItemStack itemStack, int tintIndex) {
        return HotpotSpriteProcessorConfigDataComponent.getProcessorConfigs(itemStack).get(tintIndex - HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX);
    }
}
