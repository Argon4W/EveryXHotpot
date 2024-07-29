package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
    @Unique
    private static final Map<Integer, IHotpotSpriteProcessor> SPRITE_PROCESSORS_BY_INDEX = HotpotSpriteProcessors.getSpriteProcessorRegistry().stream().collect(Collectors.toUnmodifiableMap(IHotpotSpriteProcessor::getIndex, Function.identity()));

    @Inject(method = "getColor", at = @At("RETURN"), cancellable = true)
    public void getColor(ItemStack itemStack, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (tintIndex < HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX) {
            return;
        }

        int hotpotTint = tintIndex - HotpotModEntry.HOTPOT_SPRITE_TINT_INDEX;
        int index = (hotpotTint - hotpotTint % ItemModelGenerator.LAYERS.size()) / ItemModelGenerator.LAYERS.size();

        cir.setReturnValue(SPRITE_PROCESSORS_BY_INDEX.getOrDefault(index, HotpotSpriteProcessors.getEmptySpriteProcessor()).getColor(itemStack));
    }
}
