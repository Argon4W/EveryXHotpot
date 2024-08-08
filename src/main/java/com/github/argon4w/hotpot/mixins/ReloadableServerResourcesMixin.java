package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    public List<PreparableReloadListener> listeners(List<PreparableReloadListener> original) {
        return Stream.concat(Stream.of(HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER), original.stream()).toList();
    }
}
