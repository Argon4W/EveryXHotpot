package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.google.common.collect.Maps;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
    private static final HashMap<Integer, IHotpotSpriteProcessor> SPRITE_PROCESSORS_BY_INDEX;

    static {
        SPRITE_PROCESSORS_BY_INDEX = Maps.newHashMap();

        for (IHotpotSpriteProcessor processor : HotpotSpriteProcessors.getSpriteProcessorRegistry().getValues()) {
            SPRITE_PROCESSORS_BY_INDEX.put(processor.getIndex(), processor);
        }
    }

    @Inject(method = "getColor", at = @At("RETURN"), cancellable = true)
    public void getColor(ItemStack itemStack, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (tintIndex < HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX) {
            return;
        }

        int hotpotTint = tintIndex - HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX;
        int index = (hotpotTint - hotpotTint % ItemModelGenerator.LAYERS.size()) / 4;

        cir.setReturnValue(SPRITE_PROCESSORS_BY_INDEX.getOrDefault(index, HotpotSpriteProcessors.getEmptySpriteProcessor()).processColor(itemStack));
    }
}
