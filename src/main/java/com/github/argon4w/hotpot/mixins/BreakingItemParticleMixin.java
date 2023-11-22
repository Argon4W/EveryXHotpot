package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakingItemParticle.class)
public abstract class BreakingItemParticleMixin extends TextureSheetParticle {
    protected BreakingItemParticleMixin(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    public void constructor(ClientLevel level, double p_105666_, double p_105667_, double p_105668_, ItemStack itemStack, CallbackInfo ci) {
        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            ItemStack chopstickFoodItemStack;

            if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack)).isEmpty()) {
                BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(chopstickFoodItemStack, level, null, 0);
                setSprite(model.getParticleIcon(EmptyModelData.INSTANCE));
            }
        }
    }
}
