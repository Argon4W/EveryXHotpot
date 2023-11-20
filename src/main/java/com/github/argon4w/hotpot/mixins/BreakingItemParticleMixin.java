package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakingParticle.class)
public abstract class BreakingItemParticleMixin extends SpriteTexturedParticle {
    protected BreakingItemParticleMixin(ClientWorld p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    public void constructor(ClientWorld pLevel, double pX, double pY, double pZ, ItemStack pStack, CallbackInfo ci) {
        if (pStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            ItemStack chopstickFoodItemStack;

            if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(pStack)).isEmpty()) {
                IBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(chopstickFoodItemStack, level, null);
                setSprite(model.getParticleTexture(EmptyModelData.INSTANCE));
            }
        }
    }
}
